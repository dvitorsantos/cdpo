package lsdi.cdpo.DataTransferObjects.Undeploy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lsdi.cdpo.DataTransferObjects.Deploy.DeployEdgeRequest;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;

import java.util.List;

@Data
public class UndeployFogRequest extends UndeployRequest {
    @JsonProperty("edge_rules_deploy_uuids")
    public List<String> edgeRulesDeployUuids;
    @JsonProperty("fog_rules_deploy_uuids")
    public List<String> fogRulesDeployUuids;
}
