package lsdi.cdpo.Repositories;

import lsdi.cdpo.Entities.EventProcessNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventProcessNetworkRepository extends JpaRepository<EventProcessNetwork, String> {
    EventProcessNetwork findByCommitId(String commitId);
}
