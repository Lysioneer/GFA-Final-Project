package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findAllByKingdom_Id(Long kingdomId);

    Optional<Building> findByKingdom_IdAndBuildingPosition(Long kingdomId, Integer position);

    @Query(value= "SELECT * FROM public.\"Building\" where \"type\"= 'TOWN_HALL' and \"kingdom_id\" = ?1", nativeQuery = true)
    Optional<Building> findTownHallOfKingdom(Long kingdomId);

    @Query(value= "SELECT * FROM public.\"Building\" where (\"constructTime\" IS NOT NULL or \"destroyTime\" IS NOT NULL) and \"kingdom_id\" = ?1", nativeQuery = true)
    Optional<Building> findUnderwayConstruction(Long kingdomId);

    @Query(value= "SELECT * FROM public.\"Building\" where \"constructTime\" <= ?1+60 and \"constructTime\" > ?1 and \"kingdom_id\" = ?2", nativeQuery = true)
    List<Building> findBuildingsInConstruction(Long constructTime, Long kingdomId);

    @Query(value= "SELECT * FROM public.\"Building\" where \"kingdom_id\" = ?1", nativeQuery = true)
    List<Building> findBuildingsInKingdom(Long kingdomId);

    @Query(value= "SELECT * FROM public.\"Building\" where \"destroyTime\" <= ?1+60 and \"destroyTime\" > ?1 and \"kingdom_id\" = ?2", nativeQuery = true)
    List<Building> findBuildingsToDestroy(Long constructTime,Long buildingId);

    Boolean existsByKingdomIdAndType(Long id, BuildingType buildingType);

    Building findByKingdomIdAndType(Long id, BuildingType buildingType);

    @Query(value = "SELECT * FROM \"Building\" WHERE kingdom_id = ?1 AND level >= 1", nativeQuery = true)
    Optional<List<Building>> findDefenderBuildingsForDestructionAttack(Long defenderKingdomId);

    Optional<Building> findBuildingByIdAndKingdom_Id(Long buildingId, Long KingdomId);

    Building findBuildingByKingdom_IdAndType(Long KingdomId, BuildingType buildingType);
}
