package lsdi.cdpo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lsdi.cdpo.Enums.DeployStatus;

@Entity
@Data
public class Deploy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String hostUuid;
    private String ruleUuid;
    private String deployUuid;
    private String parentHostUuid;
    private String level;
    private DeployStatus status;
}
