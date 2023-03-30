package lsdi.cdpo.Services;

import lsdi.cdpo.Connectors.IoTCatalogerConnector;
import lsdi.cdpo.DataTransferObjects.*;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployEdgeRequest;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployFogRequest;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployResponse;
import lsdi.cdpo.DataTransferObjects.Undeploy.UndeployFogRequest;
import lsdi.cdpo.Entities.Deploy;
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

        List<DeployFogRequest> deployFogRequests = new ArrayList<>();
        List<DeployEdgeRequest> deployEdgeRequests = new ArrayList<>();

        deployMap.forEach((hostUuid, rules) -> {
            String level = rules.get(0).getLevel();
            if (level.equals("FOG")) {
                DeployFogRequest deployFogRequest = new DeployFogRequest();
                deployFogRequest.setHostUuid(hostUuid);
                deployFogRequest.setFogRules(rules);
                rules.forEach(rule -> {
                    if (rule.getLevel().equals(rule.getTarget())) rule.setWebhookUrl(eventProcessNetwork.getWebhookUrl());
                });
                deployFogRequests.add(deployFogRequest);
            } else if (level.equals("EDGE")) {
                DeployEdgeRequest deployEdgeRequest = new DeployEdgeRequest();
                deployEdgeRequest.setHostUuid(hostUuid);
                deployEdgeRequest.setEdgeRules(rules);
                rules.forEach(rule -> {
                    if (rule.getLevel().equals(rule.getTarget())) rule.setWebhookUrl(eventProcessNetwork.getWebhookUrl());
                });
                deployEdgeRequests.add(deployEdgeRequest);
            } else if (level.equals("CLOUD")) {
                //TODO cloud deploy
            }
        });

        //setting edge deploy requests to fog deploy requests
        deployFogRequests.forEach(deployFogRequest -> {
            deployFogRequest.setEdgeRulesDeployRequests(deployEdgeRequests);
        });

        //creating deploy entities
        List<Deploy> deploys = new ArrayList<>();
        deployMap.forEach((hostUuid, rules) -> {
            rules.forEach(rule -> {
                Deploy deploy = new Deploy();
                deploy.setHostUuid(hostUuid);
                deploy.setRuleUuid(rule.getUuid());
                deploy.setLevel(rule.getLevel());
                deploy.setStatus("PENDING");
                deploys.add(deploy);
            });
        });

        //sending deploy requests to fog gateways
        for (DeployFogRequest deployFogRequest : deployFogRequests) {
            IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(deployFogRequest.getHostUuid());
            RestTemplate restTemplate = new RestTemplate();
            DeployResponse[] deployResponses = restTemplate.postForObject(ioTGatewayRequestResponse.getUrl() + "/deploy", deployFogRequest, DeployResponse[].class);

            //setting status to done and deploy uuid
            deploys.forEach(deploy -> {
                for (DeployResponse deployResponse : deployResponses) {
                    if (deploy.getRuleUuid().equals(deployResponse.getRuleUuid())) {
                        deploy.setStatus(deployResponse.getStatus());
                        deploy.setDeployUuid(deployResponse.getDeployUuid());
                    }
                }
            });
        }

        //save all deploys
        deployRepository.saveAll(deploys);
    }

    public void undeploy(List<Deploy> deploys) {
        RestTemplate restTemplate = new RestTemplate();

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

    public void save(Deploy deploy) {
        deployRepository.save(deploy);
    }

    public Deploy findByRuleUuid(String ruleUuid) {
        return deployRepository.findByRuleUuid(ruleUuid);
    }

    public List<Deploy> findAllByEpnCommitId(String epnCommitId) {
        return deployRepository.findAllByEpnCommitId(epnCommitId);
    }
}
