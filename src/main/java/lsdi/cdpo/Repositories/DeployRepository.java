package lsdi.cdpo.Repositories;

import lsdi.cdpo.Entities.Deploy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployRepository extends JpaRepository<Deploy, String> {
    @Query("SELECT deploy FROM Deploy deploy " +
            "JOIN Rule rule ON deploy.ruleUuid = rule.uuid " +
            "JOIN EventProcessNetwork epn ON rule.eventProcessNetwork.uuid = epn.uuid " +
            "WHERE epn.commitId = ?1")
    List<Deploy> findAllByEpnCommitId(String epnUuid);

    Deploy findByRuleUuid(String ruleUuid);

    List<Deploy> findAllByRuleUuid(String ruleUuid);

    List<Deploy> findAllByHostUuidAndRuleUuid(String hostUuid, String ruleUuid);
}
