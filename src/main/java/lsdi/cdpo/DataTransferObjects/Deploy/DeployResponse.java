package lsdi.cdpo.DataTransferObjects.Deploy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeployResponse {
    @JsonProperty("deploy_uuid")
    public String deployUuid;
    @JsonProperty("rule_uuid")
    public String ruleUuid;
    @JsonProperty("status")
    public String status;
    @JsonProperty("host_uuid")
    public String hostUuid;
}
