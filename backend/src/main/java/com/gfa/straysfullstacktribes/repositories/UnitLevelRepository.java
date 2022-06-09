package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.UnitLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.gfa.straysfullstacktribes.models.enums.TroopType;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitLevelRepository extends JpaRepository<UnitLevel,Long> {

    @Query(value = "SELECT * FROM public.\"UnitLevel\" where \"upgradeFinishedAt\" <= ?1+60 and \"upgradeFinishedAt\" > ?1 and \"kingdom_id\" = ?2", nativeQuery = true)
    List<UnitLevel> findUpgradeInProgress(Long upgradeFinishedAt, Long KingdomId);

    List<UnitLevel> findAllByKingdomId(Long kingdomId);

    UnitLevel findAllByKingdomIdAndTroopType(Long kingdomId, TroopType troopType);

    Optional<List<UnitLevel>> findByKingdomIdAndTroopTypeAndUpgradeFinishedAtNotNull(Long kingdomId, TroopType troopType);

    List<UnitLevel> findAllByKingdomIdAndUpgradeFinishedAtNotNullOrderByUpgradeFinishedAtDesc(Long kingdomId);
}