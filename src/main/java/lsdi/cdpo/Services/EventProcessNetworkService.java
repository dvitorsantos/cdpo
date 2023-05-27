package lsdi.cdpo.Services;

import lsdi.cdpo.Entities.EventProcessNetwork;
import lsdi.cdpo.Repositories.EventProcessNetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProcessNetworkService {
    @Autowired
    EventProcessNetworkRepository eventProcessNetworkRepository;
    public void save(EventProcessNetwork eventProcessNetwork) {
        eventProcessNetworkRepository.save(eventProcessNetwork);
    }
}
