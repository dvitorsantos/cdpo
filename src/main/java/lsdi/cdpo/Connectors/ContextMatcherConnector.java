package lsdi.cdpo.Connectors;

import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContextMatcherConnector {
    private final String url = "http://localhost:8980/";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUrl() {
        return this.url;
    }

    public EpnRequestResponse findMatchesToEpn(EpnRequestResponse epn) {
        String requestUrl = this.getUrl() + "match/find/nodes_to_epn";
        return restTemplate.postForObject(requestUrl, epn, EpnRequestResponse.class);
    }
}
