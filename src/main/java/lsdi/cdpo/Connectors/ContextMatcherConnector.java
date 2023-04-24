package lsdi.cdpo.Connectors;

import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContextMatcherConnector {
    @Value("${contextmatcher.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUrl() {
        return "http://contextmatcher:8080/";
    }

    public EpnRequestResponse findMatchesToEpn(EpnRequestResponse epn) {
        String requestUrl = this.getUrl() + "match/find/nodes_to_epn";
        return restTemplate.postForObject(requestUrl, epn, EpnRequestResponse.class);
    }

    public RuleRequestResponse findRuleByHostUuidAndRuleUuid(String hostUuid, String ruleUuid) {
        String requestUrl = this.getUrl() + "rule/" + hostUuid + "/" + ruleUuid;
        return restTemplate.getForObject(requestUrl, RuleRequestResponse.class);
    }
}
