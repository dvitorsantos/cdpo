package lsdi.cdpo.Connectors;

import lsdi.cdpo.DataTransferObjects.EpnRequestResponse;
import lsdi.cdpo.DataTransferObjects.IoTGatewayRequestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IoTCatalogerConnector {

    @Value("${iotcataloger.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUrl() {
        return "http://iotcataloguer:8080/";
    }

    public IoTGatewayRequestResponse getGateway(String uuid) {
        String requestUrl = this.getUrl() + "/gateway/" + uuid;

        return restTemplate.getForObject(requestUrl, IoTGatewayRequestResponse.class);
    }
}
