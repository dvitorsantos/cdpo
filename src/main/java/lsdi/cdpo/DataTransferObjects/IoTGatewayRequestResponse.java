package lsdi.cdpo.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IoTGatewayRequestResponse {
    @JsonProperty("rule_uuid")
    public String ruleUuid;
    @JsonProperty("rule_name")
    public String ruleName;
    @JsonProperty("rule_definition")
    public String ruleDefinition;
    @JsonProperty("dn")
    private String distinguishedName;
    private String url;
}
