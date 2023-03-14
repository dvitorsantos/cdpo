package lsdi.cdpo.Services;

import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Entities.Rule;
import lsdi.cdpo.Repositories.EventProcessNetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventProcessNetworkService {
    @Autowired
    EventProcessNetworkRepository eventProcessNetworkRepository;
    public void save(EventProcessNetwork eventProcessNetwork) {
        List<Rule> rules = eventProcessNetwork.getRules();
        rules.forEach(rule -> rule.setEventProcessNetwork(eventProcessNetwork));
        eventProcessNetworkRepository.save(eventProcessNetwork);
    }

    public EventProcessNetwork findByCommitId(String commitId) {
        return eventProcessNetworkRepository.findByCommitId(commitId);
    }
}
