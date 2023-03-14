package lsdi.cdpo.Services;

import lsdi.cdpo.Connectors.IoTCatalogerConnector;
import lsdi.cdpo.DataTransferObjects.*;
import lsdi.cdpo.Entities.Deploy;
import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Repositories.DeployRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeployService {
    @Autowired
    IoTCatalogerConnector ioTCatalogerConnector;

    @Autowired
    DeployRepository deployRepository;

    public void deploy(EpnRequestResponse eventProcessNetwork) {
        List<MatchRequestResponse> matchRequestResponses = eventProcessNetwork.getMatches();
        List<RuleRequestResponse> ruleRequestResponses = eventProcessNetwork.getRules();

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<Deploy> deploys = new ArrayList<>();
        matchRequestResponses.forEach(match -> {
            ruleRequestResponses.forEach(rule -> {
                if (match.getRuleUuid().equals(rule.getUuid())) {
                    IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(match.getHostUuid());
                    DeployRequest deployRequestResponse = new DeployRequest();
                    deployRequestResponse.setRuleUuid(rule.getUuid());
                    deployRequestResponse.setRuleName(rule.getName());
                    deployRequestResponse.setRuleDefinition(rule.getDefinition());
                    deployRequestResponse.setEventType(rule.getEventType());
                    deployRequestResponse.setEventAttributes(rule.getEventAttributes());

                    List<String> edgeRulesUuids = new ArrayList<>();
                    for (RuleRequestResponse eventProcessNetworkRule : eventProcessNetwork.getRules()) {
                        if (eventProcessNetworkRule.getTarget().equals(rule.getLevel())) {
                            edgeRulesUuids.add(eventProcessNetworkRule.getUuid());
                        }
                    }

                    deployRequestResponse.setEdgeRulesUuids(edgeRulesUuids);

                    DeployResponse deployResponse = restTemplate.postForObject(
                            ioTGatewayRequestResponse.getUrl() + "/deploy",
                            deployRequestResponse,
                            DeployResponse.class);

                    Deploy deploy = new Deploy();
                    deploy.setHostUuid(match.getHostUuid());
                    deploy.setRuleUuid(deployResponse.getRuleUuid());
                    deploy.setDeployUuid(deployResponse.getDeployUuid());
                    deploys.add(deploy);
                }
            });
        });

        deployRepository.saveAll(deploys);
    }

    public void undeploy(List<Deploy> deploys) {
        RestTemplate restTemplate = new RestTemplate();

        deploys.forEach(deploy -> {
            IoTGatewayRequestResponse ioTGatewayRequestResponse = ioTCatalogerConnector.getGateway(deploy.getHostUuid());
            restTemplate.delete(ioTGatewayRequestResponse.getUrl() + "/undeploy/" + deploy.getDeployUuid());
        });

        deployRepository.deleteAll(deploys);
    }

    public void save(Deploy deploy) {
        deployRepository.save(deploy);
    }

    public List<Deploy> findAllByEpnCommitId(String epnCommitId) {
        return deployRepository.findAllByEpnCommitId(epnCommitId);
    }
}
