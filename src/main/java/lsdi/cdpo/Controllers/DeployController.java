package lsdi.cdpo.Controllers;

import lsdi.cdpo.Connectors.ContextMatcherConnector;

import lsdi.cdpo.DataTransferObjects.Deploy.DeployEdgeRequest;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployFogRequest;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployResponse;
import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;
import lsdi.cdpo.DataTransferObjects.Undeploy.UndeployFogRequest;
import lsdi.cdpo.Entities.Deploy;
import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Services.DeployService;
import lsdi.cdpo.Services.EventProcessNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeployController {
    @Autowired
    private ContextMatcherConnector contextMatcherConnector;

    @Autowired
    private DeployService deployService;

    @Autowired
    private EventProcessNetworkService eventProcessNetworkService;

    @PostMapping("/deploy")
    public EpnRequestResponse deploy(@RequestBody EpnRequestResponse epn) {
        EpnRequestResponse epnRequestResponse = contextMatcherConnector.findMatchesToEpn(epn);
        epnRequestResponse.setWebhookUrl(epn.getWebhookUrl());
        deployService.deploy(epnRequestResponse);
        EventProcessNetwork eventProcessNetwork = epnRequestResponse.toEntity();
        eventProcessNetworkService.save(eventProcessNetwork);

        return epnRequestResponse;
    }
    @PostMapping("/undeploy/{epnCommitId}")
    public void undeploy(@PathVariable String epnCommitId) {
        List<Deploy> deploys = deployService.findAllByEpnCommitId(epnCommitId);
        deployService.undeploy(deploys);
    }

    @PostMapping("/undeploy/rule/{ruleUuid}")
    public void undeployRule(@PathVariable String ruleUuid) {
        Deploy deploy = deployService.findByRuleUuid(ruleUuid);
        deploy.setStatus("UNDEPLOYED");
        UndeployFogRequest undeployFogRequest = new UndeployFogRequest();
        if (deploy.getParentHostUuid() != null) {
            undeployFogRequest.setHostUuid(deploy.getParentHostUuid());
            undeployFogRequest.setFogRulesDeployUuids(new ArrayList<>());
            undeployFogRequest.setEdgeRulesDeployUuids(new ArrayList<>(List.of(deploy.getDeployUuid())));
        } else {
            undeployFogRequest.setHostUuid(deploy.getHostUuid());
            undeployFogRequest.setFogRulesDeployUuids(new ArrayList<>(List.of(deploy.getDeployUuid())));
            undeployFogRequest.setEdgeRulesDeployUuids(new ArrayList<>());
        }
        deployService.undeploy(undeployFogRequest);
        deployService.save(deploy);
    }

    @PostMapping("/deploy/rule/{ruleUuid}")
    public RuleRequestResponse deployRule(@PathVariable String ruleUuid) {
        Deploy deploy = deployService.findByRuleUuid(ruleUuid);
        deploy.setStatus("DEPLOYED");
        DeployFogRequest deployFogRequest = new DeployFogRequest();
        RuleRequestResponse ruleRequestResponse = contextMatcherConnector.findRule(ruleUuid);
        if (deploy.getParentHostUuid() != null) {
            DeployEdgeRequest deployEdgeRequest = new DeployEdgeRequest();
            deployEdgeRequest.setEdgeRules(new ArrayList<>(List.of(ruleRequestResponse)));
            deployEdgeRequest.setHostUuid(deploy.getHostUuid());
            deployFogRequest.setHostUuid(deploy.getParentHostUuid());
            deployFogRequest.setEdgeRulesDeployRequests(new ArrayList<>(List.of(deployEdgeRequest)));
            deployFogRequest.setFogRules(new ArrayList<>());
        } else {
            deployFogRequest.setHostUuid(deploy.getHostUuid());
            deployFogRequest.setEdgeRulesDeployRequests(new ArrayList<>());
            deployFogRequest.setFogRules(new ArrayList<>(List.of(ruleRequestResponse)));
        }

        deployService.deploy(deployFogRequest);
        deployService.save(deploy);
        return ruleRequestResponse;
    }


    @PutMapping("/deploy/{ruleUuid}")
    public Deploy updateDeploy(@PathVariable String ruleUuid, @RequestBody DeployResponse deployResponse) {
        Deploy deploy = deployService.findByRuleUuid(ruleUuid);
        deploy.setDeployUuid(deployResponse.getDeployUuid());
        deploy.setStatus(deployResponse.getStatus());
        deployService.save(deploy);
        return deploy;
    }

    //webhook tester
    @PostMapping("/webhook")
    public void result(@RequestBody String result) {
        System.out.println(result);
    }
}
