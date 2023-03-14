package lsdi.cdpo.DataTransferObjects;

import lombok.Data;

@Data
public class MatchRequestResponse {
    public String uuid;
    public String ruleUuid;
    public String hostUuid;
}
