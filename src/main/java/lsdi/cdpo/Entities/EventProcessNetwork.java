package lsdi.cdpo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lsdi.cdpo.DataTransferObjects.RuleRequestResponse;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProcessNetwork {
    @Id
    private String uuid;
    @Column(unique = true)
    private String commitId;
    private String version;
    private Boolean enabled;
    private String qos;
    private Boolean atomic;
    private String webhookUrl;
    @OneToMany(mappedBy = "eventProcessNetwork", cascade = CascadeType.ALL)
    private List<Rule> rules;
}
