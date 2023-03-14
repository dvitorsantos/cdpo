package lsdi.cdpo.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DeployRequest {
    @JsonProperty("rule_uuid")
    public String ruleUuid;
    @JsonProperty("rule_name")
    public String ruleName;
    @JsonProperty("rule_definition")
    public String ruleDefinition;
    @JsonProperty("event_type")
    public String eventType;
    @JsonProperty("event_attributes")
    public Map<String, String> eventAttributes;
    @JsonProperty("edge_rules_uuids")
    public List<String> edgeRulesUuids;
}
