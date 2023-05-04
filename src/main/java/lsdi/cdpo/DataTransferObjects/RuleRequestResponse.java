package lsdi.cdpo.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lsdi.cdpo.Entities.EventAttribute;
import lsdi.cdpo.Entities.EventType;
import lsdi.cdpo.Entities.Rule;
import lsdi.cdpo.Enums.Level;
import lsdi.cdpo.Enums.Qos;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RuleRequestResponse {
    @Nullable
    String uuid;
    String name;
    String description;
    String definition;
    String level;
    String target;
    @Nullable
    String qos;
    @JsonProperty("tag_filter")
    String tagFilter;
    @JsonProperty("event_type")
    String eventType;
    @JsonProperty("event_attributes")
    Map<String, String> eventAttributes;
    @Nullable
    @JsonProperty("webhook_url")
    String webhookUrl;
    @Nullable
    RequirementsRequestResponse requirements;
    public Rule toEntity(){
        Rule rule = new Rule();
        rule.setUuid(this.uuid);
        rule.setName(this.name);
        rule.setDescription(this.description);
        rule.setTagFilter(this.tagFilter);
        rule.setLevel(this.level);
        rule.setTarget(this.target);
        rule.setDefinition(this.definition);
        rule.setQos(this.qos);

        EventType eventType = new EventType();
        eventType.setName(this.getEventType());
        eventType.setRule(rule);

        List<EventAttribute> eventAttributes = new ArrayList<>();
        this.eventAttributes.forEach((key, value) -> {
            EventAttribute eventAttribute = new EventAttribute();
            eventAttribute.setName(key);
            eventAttribute.setType(value);
            eventAttribute.setEventType(eventType);
            eventAttributes.add(eventAttribute);
        });
        eventType.setEventAttributes(eventAttributes);

        rule.setEventType(List.of(eventType));
        return rule;
    }
}
