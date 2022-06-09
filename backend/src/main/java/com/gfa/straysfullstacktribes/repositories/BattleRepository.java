package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {

    @Query(value= "SELECT * FROM public.\"Battle\" where \"battleTime\" <= ?1+60 and \"battleTime\" > ?1 and \"defenderKingdom_id\" = ?2", nativeQuery = true)
    List<Battle> findBattleDefense(Long battleTime, Long defenderKingdomId);

    @Query(value= "SELECT * FROM public.\"Battle\" where \"returnTime\" <= ?1+60 and \"returnTime\" > ?1 and \"attackerKingdom_id\" = ?2", nativeQuery = true)
    List<Battle> findBattleAttack(Long battleTime, Long attackerKingdomId);

    @Query(value= "SELECT * FROM public.\"Battle\" where \"attackerKingdom_id\" = ?1 or \"defenderKingdom_id\" = ?1", nativeQuery = true)
    List<Battle> findAllBattles(Long kingdomId);
}
