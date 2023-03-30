package lsdi.cdpo.Controllers;

import lsdi.cdpo.Connectors.ContextMatcherConnector;

import lsdi.cdpo.DataTransferObjects.Deploy.DeployResponse;
import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.Entities.Deploy;
import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Services.DeployService;
import lsdi.cdpo.Services.EventProcessNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
