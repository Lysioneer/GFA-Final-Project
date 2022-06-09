package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.DestroyedBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestroyedBuildingRepository extends JpaRepository<DestroyedBuilding, Long> {

    Optional<List<DestroyedBuilding>> findAllByBattleIdAndKingdomId(Long battleId, Long kingdomId);

}
