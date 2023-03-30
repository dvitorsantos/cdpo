package lsdi.cdpo.DataTransferObjects.Undeploy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UndeployRequest {
    @JsonProperty("host_uuid")
    public String hostUuid;
}
