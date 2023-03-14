package lsdi.cdpo.Controllers;

import lsdi.cdpo.Connectors.ContextMatcherConnector;

import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.Entities.Deploy;
import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Services.DeployService;
import lsdi.cdpo.Services.EventProcessNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
        deployService.deploy(epnRequestResponse);
        EventProcessNetwork eventProcessNetwork = epnRequestResponse.toEntity();
        eventProcessNetworkService.save(eventProcessNetwork);

        return epn;
    }

    @PostMapping("/undeploy/{epnCommitId}")
    public void undeploy(@PathVariable String epnCommitId) {
        List<Deploy> deploys = deployService.findAllByEpnCommitId(epnCommitId);
        deployService.undeploy(deploys);
    }
}
