package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.repositories.BattleRepository;
import com.gfa.straysfullstacktribes.repositories.BuildingRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.repositories.UnitLevelRepository;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UpdateResourcesServiceImpl implements UpdateResourcesService {

    private final KingdomService kingdomService;
    private final TroopRepository troopRepository;
    private final BuildingRepository buildingRepository;
    private final UnitLevelRepository unitLevelRepository;
    private final BattleRepository battleRepository;
    private final BattleService battleService;

    @Override
    public void updateResources(Long kingdomId) {
        Long currentTime = System.currentTimeMillis() / 1000;
        updateResources(kingdomId,currentTime);
    }

    public void updateResources(Long kingdomId, Long updateTime) {
        Kingdom currentKingdom;
        currentKingdom = kingdomService.getKingdomById(kingdomId).get();
        if (currentKingdom == null) {
            return;
        }
        try {
            Long capacity = (long) DefaultValueReaderUtil.getInt("buildings.town_hall.foodCapacity") *
                    buildingRepository.findTownHallOfKingdom(currentKingdom.getId()).get().getLevel();
            currentKingdom.setFoodProduction(calculateFoodConsumption(currentKingdom));
            if (!buildingRepository.findBuildingsInKingdom(currentKingdom.getId()).isEmpty()) {
                currentKingdom.setGoldProduction(buildingGoldProduction(buildingRepository.findBuildingsInKingdom(currentKingdom.getId())));
            }
            while (currentKingdom.getUpdatedAt()+60 < updateTime) {
                List<Battle> defenseBattles = battleRepository.findBattleDefense(currentKingdom.getUpdatedAt(),currentKingdom.getId());
                if (!defenseBattles.isEmpty()) {
                    Kingdom updatedKingdom;
                    for(Battle battle: defenseBattles){
                        updatedKingdom = battleDefenseResult(battle, currentKingdom);
                        currentKingdom.setGoldAmount(updatedKingdom.getGoldAmount());
                        currentKingdom.setFoodAmount(updatedKingdom.getGoldAmount());
                        currentKingdom.setFoodProduction(updatedKingdom.getFoodProduction());
                    }
                }
                List<Battle> attackBattles = battleRepository.findBattleAttack(currentKingdom.getUpdatedAt(),currentKingdom.getId());
                if (!attackBattles.isEmpty()) {
                    Kingdom updatedKingdom;
                    for(Battle battle: attackBattles){
                        updatedKingdom = battleAttackResult(battle, currentKingdom);
                        currentKingdom.setGoldAmount(updatedKingdom.getGoldAmount());
                        currentKingdom.setFoodAmount(updatedKingdom.getGoldAmount());
                        currentKingdom.setFoodProduction(updatedKingdom.getFoodProduction());
                    }
                }
                List<Building> buildings = buildingRepository.findBuildingsInConstruction(currentKingdom.getUpdatedAt(), currentKingdom.getId());
                if (!buildings.isEmpty()) {
                    for (Building building : buildings) {
                        currentKingdom.setFoodProduction(currentKingdom.getFoodProduction() + buildingUpgrade(building));
                    }
                }
                buildings = buildingRepository.findBuildingsToDestroy(currentKingdom.getUpdatedAt(), currentKingdom.getId());
                if (!buildings.isEmpty()) {
                    for (Building building : buildings) {
                        currentKingdom.setFoodProduction(currentKingdom.getFoodProduction() + buildingDestroy(building));
                    }
                }
                List<Troop> troopsInTraining = troopRepository.findTroopsInTraining(currentKingdom.getUpdatedAt(), currentKingdom.getId());
                if (!troopsInTraining.isEmpty()) {
                    currentKingdom.setFoodProduction(currentKingdom.getFoodProduction() - troopsConsumption(troopsInTraining));
                }
                List<UnitLevel> unitLevels = unitLevelRepository.findUpgradeInProgress(currentKingdom.getUpdatedAt(), currentKingdom.getId());
                if (!unitLevels.isEmpty()) {
                    for (UnitLevel unitLevel : unitLevels) {
                        upgradeTroops(unitLevel);
                    }
                }
                capacity = (long) DefaultValueReaderUtil.getInt("buildings.town_hall.foodCapacity") *
                        buildingRepository.findTownHallOfKingdom(currentKingdom.getId()).get().getLevel();
                currentKingdom.setFoodAmount(updateFood(capacity, currentKingdom));
                currentKingdom.setFoodProduction(calculateFoodConsumption(currentKingdom));// need to be updated in case food production was negative
                if (!buildingRepository.findBuildingsInKingdom(currentKingdom.getId()).isEmpty()) {
                    currentKingdom.setGoldProduction(buildingGoldProduction(buildingRepository.findBuildingsInKingdom(currentKingdom.getId())));
                }
                currentKingdom.setGoldAmount(updateGold(capacity, currentKingdom));
                currentKingdom.setUpdatedAt(currentKingdom.getUpdatedAt() + DefaultValueReaderUtil.getInt("gameCycle.length"));
            }
            kingdomService.saveKingdom(currentKingdom);
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
    }

    public Long updateGold(Long capacity, Kingdom currentKingdom) {
        if (currentKingdom.getGoldAmount() + currentKingdom.getGoldProduction() > capacity) {
            return capacity;
        } else {
            return currentKingdom.getGoldAmount() + currentKingdom.getGoldProduction();
        }
    }

    public Long updateFood(Long capacity, Kingdom currentKingdom) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        Long foodInKingdom = currentKingdom.getFoodAmount() + currentKingdom.getFoodProduction();
        if(foodInKingdom < 0){
            return unitsDieOfHunger(currentKingdom,foodInKingdom);
        }
        if (foodInKingdom > capacity) {
            return capacity;
        } else {
            return foodInKingdom;
        }
    }

    public Long unitsDieOfHunger (Kingdom currentKingdom, Long foodMissing) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        while(foodMissing<0){
            List<Troop> troops = troopRepository.findTroopsInKingdom(currentKingdom.getUpdatedAt(), currentKingdom.getId());
            if(troops.isEmpty()){
                return 0L;
            }
            foodMissing+= DefaultValueReaderUtil.getInt("troops." + troops.get(0).getTroopType().toString().toLowerCase() + ".foodConsumption");
            troops.get(0).setDestroyTime(currentKingdom.getUpdatedAt());
            troopRepository.save(troops.get(0));
            troops.remove(0);

        }
        return foodMissing;
    }

    public Kingdom battleDefenseResult(Battle battle ,Kingdom currentKingdom) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        if(battle.getAttackerWins()==null) {
            battleService.chooseBattleType(battle);
        }
        currentKingdom.setFoodProduction(calculateFoodConsumption(currentKingdom));
        if (!buildingRepository.findBuildingsInKingdom(currentKingdom.getId()).isEmpty()) {
            currentKingdom.setGoldProduction(buildingGoldProduction(buildingRepository.findBuildingsInKingdom(currentKingdom.getId())));
        }
        Battle resolvedBattle = battleRepository.getById(battle.getId());
        if (resolvedBattle.getAttackerWins()) {
            return resourcesStolen(resolvedBattle,currentKingdom);
        }
        return currentKingdom;
    }

    public Kingdom battleAttackResult(Battle battle,Kingdom currentKingdom) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        if(battle.getAttackerWins()==null){
            updateResources(battle.getDefenderKingdom().getId(),battle.getBattleTime()+60);
        }
        battleService.troopsCameBackFromTheBattle(battle.getId());
        if (!troopRepository.findTroopsInKingdom(currentKingdom.getUpdatedAt(), currentKingdom.getId()).isEmpty()) {
            currentKingdom.setFoodProduction(currentKingdom.getFoodProduction() -
                    troopsConsumption(troopRepository.findTroopsInKingdom(currentKingdom.getUpdatedAt(), currentKingdom.getId())));
        }
        Battle resolvedBattle = battleRepository.getById(battle.getId());
        if(resolvedBattle.getAttackerWins()) {
            return resourcesTaken(resolvedBattle, currentKingdom);
        }
        return currentKingdom;
    }

    public Kingdom resourcesStolen(Battle battle,Kingdom currentKingdom) {
        currentKingdom.setFoodAmount(currentKingdom.getFoodAmount()-battle.getFoodStolen());
        currentKingdom.setGoldAmount(currentKingdom.getGoldAmount()-battle.getGoldStolen());
        return  currentKingdom;
    }

    public Kingdom resourcesTaken(Battle battle,Kingdom currentKingdom) {
        currentKingdom.setFoodAmount(currentKingdom.getFoodAmount()+battle.getFoodStolen());
        currentKingdom.setGoldAmount(currentKingdom.getGoldAmount()+battle.getGoldStolen());
        return  currentKingdom;
    }

    public Integer buildingDestroy(Building building)
            throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int foodProduction;
        if (building.getLevel() == 1) {
            foodProduction = -DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base");
            buildingRepository.delete(building);
        } else {
            foodProduction = (DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base") * (building.getLevel()) - 1) -
                    (DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base") * building.getLevel());
            building.setLevel(building.getLevel() - 1);
            building.setDestroyTime(null);
            buildingRepository.save(building);
        }
        return foodProduction;
    }

    public Integer buildingUpgrade(Building building)
            throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int foodProduction = DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base") * ((building.getLevel()) + 1) -
                (DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base") * building.getLevel());
        building.setLevel(building.getLevel() + 1);
        building.setConstructTime(null);
        buildingRepository.save(building);
        return foodProduction;
    }

    public Integer buildingGoldProduction(List<Building> buildings) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int goldProduction=0;
        for(Building building: buildings){
            if(building.getType().getName().equals("gold_mine")) {
                goldProduction = DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".gold.base") * building.getLevel();
            }
        }
        return goldProduction;
    }

    public Integer buildingFoodConsumption(List<Building> buildings)
            throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int foodProduction = 0;
        for (Building building : buildings) {
            foodProduction += DefaultValueReaderUtil.getInt("buildings." + building.getType().toString().toLowerCase() + ".food.base") * building.getLevel();
        }
        return foodProduction;
    }

    public Integer currentTroopsConsumption(Kingdom currentKingdom) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        if (!troopRepository.findTroopsInKingdom(currentKingdom.getUpdatedAt(), currentKingdom.getId()).isEmpty()) {
            return currentKingdom.getFoodProduction() -
                    troopsConsumption(troopRepository.findTroopsInKingdom(currentKingdom.getUpdatedAt(), currentKingdom.getId()));
        }
        return currentKingdom.getFoodProduction();
    }

    public Integer troopsConsumption(List<Troop> troops)
            throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int foodConsumption = 0;
        for (Troop troop : troops) {
            foodConsumption += DefaultValueReaderUtil.getInt("troops." + troop.getTroopType().toString().toLowerCase() + ".foodConsumption");
        }
        return foodConsumption;
    }
    public Integer calculateFoodConsumption(Kingdom currentKingdom) throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        currentKingdom.setFoodProduction(0);
        currentKingdom.setFoodProduction(currentTroopsConsumption(currentKingdom));
        if (!buildingRepository.findBuildingsInKingdom(currentKingdom.getId()).isEmpty()) {
            currentKingdom.setFoodProduction(currentKingdom.getFoodProduction() +
                    buildingFoodConsumption(buildingRepository.findBuildingsInKingdom(currentKingdom.getId())));
        }
        return currentKingdom.getFoodProduction();
    }

    private void upgradeTroops(UnitLevel unitLevel) {
        unitLevel.setUpgradeLevel(unitLevel.getUpgradeLevel() + 1);
        unitLevelRepository.save(unitLevel);
    }
}
