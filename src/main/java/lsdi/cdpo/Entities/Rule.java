package lsdi.cdpo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    @Id
    private String uuid;
    private String name;
    private String description;
    private String tagFilter;
    private String level;
    private String target;
    @Column(length = 10000)
    private String definition;
    private String qos;
    @ManyToOne
    private EventProcessNetwork eventProcessNetwork;
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
    private List<EventType> eventType;
    private String outputEventType;
}
