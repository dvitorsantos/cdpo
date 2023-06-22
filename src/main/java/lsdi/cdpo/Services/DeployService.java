package lsdi.cdpo.Services;

import lsdi.cdpo.Connectors.IoTCatalogerConnector;
import lsdi.cdpo.DataTransferObjects.*;
import lsdi.cdpo.DataTransferObjects.Deploy.*;
import lsdi.cdpo.DataTransferObjects.Undeploy.UndeployFogRequest;
import lsdi.cdpo.DataTransferObjects.Undeploy.UndeployRequest;
import lsdi.cdpo.Entities.Deploy;
import lsdi.cdpo.Enums.DeployStatus;
import lsdi.cdpo.Repositories.DeployRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DeployService {
    @Autowired
    IoTCatalogerConnector ioTCatalogerConnector;

    @Autowired
    DeployRepository deployRepository;

    RestTemplate restTemplate = new RestTemplate();

    public void deploy(EpnRequestResponse eventProcessNetwork) {
        List<MatchRequestResponse> matchRequestResponses = eventProcessNetwork.getMatches();
        List<RuleRequestResponse> ruleRequestResponses = eventProcessNetwork.getRules();

        Map<String, ArrayList<RuleRequestResponse>> deployMap = new HashMap<>();

        for (MatchRequestResponse match : matchRequestResponses) {
            for (RuleRequestResponse rule : ruleRequestResponses) {
                if (rule.getUuid().equals(match.getRuleUuid())) {
                    if (deployMap.containsKey(match.getHostUuid())) {
                        deployMap.get(match.getHostUuid()).add(rule);
                    } else {
                        ArrayList<RuleRequestResponse> rules = new ArrayList<>();
                        rules.add(rule);
                        deployMap.put(match.getHostUuid(), rules);
                    }
                }
            }
        }

        //creating deploy entities
        List<Deploy> deploys = new ArrayList<>();
        deployMap.forEach((hostUuid, rules) -> {
            rules.forEach(rule -> {
                Deploy deploy = new Deploy();
                deploy.setHostUuid(hostUuid);
                deploy.setRuleUuid(rule.getUuid());
                deploy.setLevel(rule.getLevel());
                deploy.setStatus(DeployStatus.PENDING);
                deploys.add(deploy);
            });
        });

        List<DeployFogRequest> deployFogRequests = new ArrayList<>();
        List<DeployRequest> deployEdgeRequests = new ArrayList<>();
        List<DeployRequest> deployCloudRequests = new ArrayList<>();

        //separando fog e edge deploy requests
        deployMap.forEach((hostUuid, rules) -> {
            String level = rules.get(0).getLevel();
            if (level.equals("FOG")) {
                DeployFogRequest deployFogRequest = new DeployFogRequest();
                deployFogRequest.setHostUuid(hostUuid);
                deployFogRequest.setRules(rules);
                rules.forEach(rule -> {
                    if (rule.getLevel().equals("WEBHOOK"))
                        rule.setWebhookUrl(eventProcessNetwork.getWebhookUrl());
                });
                deployFogRequests.add(deployFogRequest);
            } else if (level.equals("EDGE")) {
                DeployRequest deployEdgeRequest = new DeployRequest();
                deployEdgeRequest.setHostUuid(hostUuid);
                deployEdgeRequest.setRules(rules);
                rules.forEach(rule -> {
                    if (rule.getLevel().equals("WEBHOOK"))
                        rule.setWebhookUrl(eventProcessNetwork.getWebhookUrl());
                });
                deployEdgeRequests.add(deployEdgeRequest);
            } else if (level.equals("CLOUD")) {
                DeployRequest deployCloudRequest = new DeployRequest();
                deployCloudRequest.setHostUuid(hostUuid);
                deployCloudRequest.setRules(rules);
                rules.forEach(rule -> {
                    if (rule.getLevel().equals("WEBHOOK"))
                        rule.setWebhookUrl(eventProcessNetwork.getWebhookUrl());
                });
                deployCloudRequests.add(deployCloudRequest);
            }
        });

        //setting edge deploy requests to fog deploy requests
        deployFogRequests.forEach(deployFogRequest -> {
            deployFogRequest.setEdgeDeployRequests(deployEdgeRequests);
            deploys.forEach(deploy -> {
                if (deploy.getLevel().equals("EDGE") && deploy.getParentHostUuid() == null)
                    deploy.setParentHostUuid(deployFogRequest.getHostUuid());
            });
        });

        //sending deploy requests to fog gateways
        for (DeployFogRequest deployFogRequest : deployFogRequests) {
            IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(deployFogRequest.getHostUuid());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/deploy", deployFogRequest, DeployResponse[].class);
        }

        //sending deploy requests to cloud gateways
        for (DeployRequest deployCloudRequest : deployCloudRequests) {
            IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(deployCloudRequest.getHostUuid());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/deploy", deployCloudRequest, DeployResponse[].class);
        }

        //save all deploys
        deployRepository.saveAll(deploys);
    }

    public void undeploy(List<Deploy> deploys) {
        List<UndeployFogRequest> undeployFogRequests = new ArrayList<>();

        Map<String, List<String>> undeployFogMap = new HashMap<>();
        Map<String, List<String>> undeployEdgeMap = new HashMap<>();

        deploys.forEach(deploy -> {
            if (deploy.getLevel().equals("FOG")) {
                if (undeployFogMap.containsKey(deploy.getHostUuid())) {
                    undeployFogMap.get(deploy.getHostUuid()).add(deploy.getDeployUuid());
                } else {
                    List<String> deployUuids = new ArrayList<>();
                    deployUuids.add(deploy.getDeployUuid());
                    undeployFogMap.put(deploy.getHostUuid(), deployUuids);
                }
            } else if (deploy.getLevel().equals("EDGE")) {
                if (undeployEdgeMap.containsKey(deploy.getHostUuid())) {
                    undeployEdgeMap.get(deploy.getHostUuid()).add(deploy.getDeployUuid());
                } else {
                    List<String> deployUuids = new ArrayList<>();
                    deployUuids.add(deploy.getDeployUuid());
                    undeployEdgeMap.put(deploy.getHostUuid(), deployUuids);
                }
            }
        });

        undeployFogMap.forEach((fogHostUuid, fogRulesDeployUuid) -> {
            UndeployFogRequest undeployFogRequest = new UndeployFogRequest();
            undeployFogRequest.setHostUuid(fogHostUuid);
            undeployFogRequest.setFogRulesDeployUuids(fogRulesDeployUuid);
            undeployEdgeMap.forEach((edgeHostUuid, edgeRulesDeployUuid) -> {
                undeployFogRequest.setEdgeRulesDeployUuids(edgeRulesDeployUuid);
            });
            undeployFogRequests.add(undeployFogRequest);
        });

        //sending undeploy requests to fog gateways
        for (Map.Entry<String, List<String>> entry : undeployFogMap.entrySet()) {
            IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(entry.getKey());
            restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/undeploy", undeployFogRequests, DeployResponse[].class);
        }
    }

    public void undeploy(UndeployFogRequest undeployFogRequest) {
        //arraylist to send to fog gateway because it is expecting an array
        ArrayList<UndeployFogRequest> undeployFogRequests = new ArrayList<>();
        undeployFogRequests.add(undeployFogRequest);

        IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(undeployFogRequest.getHostUuid());
        restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/undeploy", undeployFogRequests, DeployResponse[].class);
    }

    public void deploy(DeployFogRequest deployFogRequest) {
        IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(deployFogRequest.getHostUuid());
        restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/deploy", deployFogRequest, DeployResponse[].class);
    }

    public void save(Deploy deploy) {
        deployRepository.save(deploy);
    }

    public Deploy findByRuleUuid(String ruleUuid) {
        return deployRepository.findByRuleUuid(ruleUuid);
    }

    public List<Deploy> findAllByRuleUuid(String ruleUuid) {
        return deployRepository.findAllByRuleUuid(ruleUuid);
    }

    public List<Deploy> findAllByHostUuidAndRuleUuid(String hostUuid, String ruleUuid) {
        return deployRepository.findAllByHostUuidAndRuleUuid(hostUuid, ruleUuid);
    }

    public List<Deploy> findAllByEpnCommitId(String epnCommitId) {
        return deployRepository.findAllByEpnCommitId(epnCommitId);
    }
}
