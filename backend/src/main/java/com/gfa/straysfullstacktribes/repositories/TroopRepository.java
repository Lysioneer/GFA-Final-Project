package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.Troop;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TroopRepository extends JpaRepository<Troop, Long> {

    @Query(value = "SELECT * FROM public.\"Troop\" WHERE \"endOfTrainingTime\" <= ?1+60 AND \"endOfTrainingTime\" > ?1 AND \"kingdom_id\" = ?2", nativeQuery = true)
    List<Troop> findTroopsInTraining(Long endOfTrainingTime, Long KingdomId);

    @Query(value = "SELECT * FROM public.\"Troop\" WHERE \"endOfTrainingTime\" <= ?1 AND \"kingdom_id\" = ?2 AND \"battle_id\" is NULL AND \"destroyTime\" is NULL order by id desc", nativeQuery = true)
    List<Troop> findTroopsInKingdom(Long endOfTrainingTime, Long KingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE kingdom_id = ?1 AND battle_id is null AND \"destroyTime\" is null AND \"troopType\" = ?2 AND to_timestamp(\"endOfTrainingTime\") < now()", nativeQuery = true)
    List<Troop> findTroopsForBattle(Long kingdom_id, String troopType);

    List<Troop> findAllByKingdom_Id(Long kingdomId);

    Optional<List<Troop>> findAllByKingdomIdOrderByEndOfTrainingTimeDesc(Long kingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE battle_id = ?1 AND \"troopType\" LIKE 'SCOUT' AND kingdom_id = ?2 AND \"destroyTime\" IS NULL", nativeQuery = true)
    Optional<List<Troop>> findAttackerTroopsForSpyAttack(Long battleId, Long attackerKingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE \"troopType\" LIKE 'SCOUT' AND kingdom_id = ?1 AND \"destroyTime\" IS NULL ", nativeQuery = true)
    Optional<List<Troop>> findDefenderTroopsForSpyAttack(Long defenderKingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE battle_id = ?1 AND kingdom_id = ?2 AND \"destroyTime\" IS NULL AND \"troopType\" IN ('PHALANX','FOOTMAN','KNIGHT','SCOUT')  ", nativeQuery = true)
    Optional<List<Troop>> findAttackerTroopsForTroopAttack(Long battleId, Long attackerKingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE kingdom_id = ?1 AND \"destroyTime\" IS NULL AND \"battle_id\" IS NULL AND \"troopType\" NOT IN ('DIPLOMAT','SETTLER','TREBUCHET')", nativeQuery = true)
    Optional<List<Troop>> findDefenderTroopsForTroopAttack(Long attackerKingdomId);

    @Query(value = "SELECT * FROM \"Troop\" WHERE battle_id = ?1 AND kingdom_id = ?2 AND \"destroyTime\" IS NULL AND \"troopType\" IN ('TREBUCHET')  ", nativeQuery = true)
    Optional<List<Troop>> findAttackerTroopsForDestructionAttack(Long battleId, Long attackerKingdomId);

    Optional<List<Troop>> findAllByBattleIdAndKingdomIdAndDestroyTimeIsNotNull(Long battleId, Long kingdomId);

    Optional<List<Troop>> findAllByKingdomIdAndEndOfTrainingTimeNotNullOrderByEndOfTrainingTimeDesc(Long kingdomId);

    Optional<List<Troop>> findAllByBattleIdAndKingdomIdAndDestroyTimeIsNull(Long battleId, Long kingdomId);

    Optional<List<Troop>> findAllByBattleIdAndKingdomIdAndTroopType(Long battleId, Long kingdomId, TroopType troopType);
}
