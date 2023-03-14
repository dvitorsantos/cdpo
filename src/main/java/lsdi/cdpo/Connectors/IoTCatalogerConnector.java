package lsdi.cdpo.Connectors;

import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.DataTransferObjects.IoTGatewayRequestResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IoTCatalogerConnector {
    private final String url = "http://localhost:8280/";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUrl() {
        return this.url;
    }

    public IoTGatewayRequestResponse getGateway(String uuid) {
        String requestUrl = this.getUrl() + "/gateway/" + uuid;

        return restTemplate.getForObject(requestUrl, IoTGatewayRequestResponse.class);
    }
}
