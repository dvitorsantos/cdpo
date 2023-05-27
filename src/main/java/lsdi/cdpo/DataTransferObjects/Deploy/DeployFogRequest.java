package lsdi.cdpo.DataTransferObjects.Deploy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;

import java.util.List;

@Data
public class DeployFogRequest extends DeployRequest {
    @JsonProperty("edge_rules_deploy_requests")
    public List<DeployEdgeRequest> edgeRulesDeployRequests;
    @JsonProperty("cloud_rules_deploy_requests")
    public List<DeployCloudRequest> cloudRulesDeployRequests;
    @JsonProperty("fog_rules")
    public List<RuleRequestResponse> fogRules;
}
