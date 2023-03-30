package lsdi.cdpo.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Entities.Rule;
import lsdi.cdpo.Enums.Qos;
import org.springframework.lang.Nullable;

import java.util.*;

@Data
public class EpnRequestResponse {
    @Nullable
    String uuid;
    @Nullable
    @JsonProperty("commit_id")
    String commitId;
    String version;
    List<RuleRequestResponse> rules;
    @Nullable
    Boolean enabled;
    @Nullable
    String qos;
    @Nullable
    Boolean atomic;
    @Nullable
    List<MatchRequestResponse> matches;
    @Nullable
    @JsonProperty("webhook_url")
    String webhookUrl;

    public EventProcessNetwork toEntity() {
        EventProcessNetwork eventProcessNetwork = new EventProcessNetwork();
        eventProcessNetwork.setUuid(this.uuid);
        eventProcessNetwork.setCommitId(this.commitId);
        eventProcessNetwork.setVersion(this.version);
        eventProcessNetwork.setEnabled(this.enabled);
        eventProcessNetwork.setQos(this.qos);
        eventProcessNetwork.setAtomic(this.atomic);
        eventProcessNetwork.setWebhookUrl(this.webhookUrl);
        List<Rule> rules = this.getRules().stream().map(RuleRequestResponse::toEntity).toList();
        rules.forEach(rule -> rule.setEventProcessNetwork(eventProcessNetwork));
        eventProcessNetwork.setRules(rules);
        return eventProcessNetwork;
    }
}
