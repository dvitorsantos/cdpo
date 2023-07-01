package lsdi.cdpo.DataTransferObjects.Deploy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@Data
public class DeployRequest {
    @JsonProperty("host_uuid")
    public String hostUuid;
    @Nullable
    @JsonProperty("webhook_url")
    public String webhookUrl;
    public List<RuleRequestResponse> rules;
}
