package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.BattleIsDecidedException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.models.enums.BattleType;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.repositories.*;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BattleServiceImpl implements BattleService {

    private final KingdomRepository kingdomRepository;
    private final TroopRepository troopRepository;
    private final BattleRepository battleRepository;
    private final UnitLevelRepository unitLevelRepository;
    private final BuildingRepository buildingRepository;
    private final DestroyedBuildingRepository destroyedBuildingRepository;

    @Override
    public void saveBattle(Battle battle) {
        battleRepository.save(battle);
    }

    @Override
    public List<Troop> getTroopsForBattle(Long id, TroopType troop) {
        return troopRepository.findTroopsForBattle(id, troop.toString());
    }

    @Override
    public void sendTroopsIntoBattle(BattleRequestDTO battleRequestDTO, Battle battle) { //set battle_id to troop
        for (BattleTroopDTO troop : battleRequestDTO.getTroops()) {
            for (int i = 0; i < troop.getQuantity(); i++) {
                Troop t = getTroopsForBattle(battle.getAttackerKingdom().getId(), troop.getType()).get(0);
                t.setBattle(battle);
                troopRepository.save(t);
            }
        }
    }

    @Override
    public Double calculateDistance(Kingdom attackerKingdom, Kingdom defenderKingdom) {
        Long attackerCorX = attackerKingdom.getCorX();
        Long attackerCorY = attackerKingdom.getCorY();
        Long defenderCorX = defenderKingdom.getCorX();
        Long defenderCorY = defenderKingdom.getCorY();
        long x;
        long y;
        double distance;
        if (Objects.equals(attackerCorX, defenderCorX) || Objects.equals(attackerCorY, defenderCorY)) {
            distance = Math.abs((attackerCorX - defenderCorX) + (attackerCorY - defenderCorY));
        } else {
            x = Math.abs(attackerCorX - defenderCorX);
            y = Math.abs(attackerCorY - defenderCorY);
            distance = Math.round((Math.sqrt((x * x) + (y * y))) * 10) / 10d;
        }
        return distance;
    }

    @Override
    public Integer getSpeedOfTheSlowestTroop(BattleRequestDTO battleRequestDTO, Long attackerKingdomId) {
        int speed = Integer.MAX_VALUE;
        for (BattleTroopDTO troop : battleRequestDTO.getTroops()) {
            if (getTroopSpeed(attackerKingdomId, troop.getType()) < speed) {
                speed = getTroopSpeed(attackerKingdomId, troop.getType());
            }
        }
        return speed;
    }

    @Override
    public Integer getTroopSpeed(Long kingdomId, TroopType troopType) {
        try {
            int defaultSpeed = DefaultValueReaderUtil.getInt("troops." + troopType.getName() + ".defaultSpeed");

            if (unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType) == null) {
                return defaultSpeed;
            } else {
                int level = unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel() * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement.nonCombatUnits.increaseSpeed");
                return defaultSpeed + level;
            }

        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getTroopAttack(Long kingdomId, TroopType troopType) {
        try {
            int defaultAttack = DefaultValueReaderUtil.getInt("troops." + troopType.getName() + ".defaultAttack");

            if (unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType) == null) {
                return defaultAttack;
            } else {
                int level = unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel() * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement.combatUnits.increaseAttack");
                return defaultAttack + level;
            }

        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getTroopDefence(Long kingdomId, TroopType troopType) {
        try {
            int defaultDefence = DefaultValueReaderUtil.getInt("troops." + troopType.getName() + ".defaultDefence");

            if (unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType) == null) {
                return defaultDefence;
            } else {
                int level = unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel() * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement.combatUnits.increaseDefence");
                return defaultDefence + level;
            }

        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getTroopHp(Long kingdomId, TroopType troopType) {
        try {
            int defaultHp = DefaultValueReaderUtil.getInt("troops." + troopType.getName() + ".defaultHp");

            if (unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType) == null) {
                return defaultHp;
            } else {
                int level = unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel() * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement.combatUnits.increaseHp");
                return defaultHp + level;
            }

        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getBuildingHp(Building building) {
        try {
            return DefaultValueReaderUtil.getInt("buildings." + building.getType().getName() + ".defaultHp");
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void troopAttack(Long battleId) {
        Battle battle = battleRepository.getById(battleId);
        Optional<List<Troop>> attackerList = troopRepository.findAttackerTroopsForTroopAttack(battleId, battle.getAttackerKingdom().getId());
        Optional<List<Troop>> defenderList = troopRepository.findDefenderTroopsForTroopAttack(battle.getDefenderKingdom().getId());

        if (battle.getAttackerWins() != null) {
            throw new BattleIsDecidedException();
        } else if (defenderList.isEmpty()) {
            plunder(battle);
            battle.setAttackerWins(true);
            battleRepository.save(battle);
            return;
        } else if (attackerList.isEmpty()) {
            battle.setAttackerWins(false);
            battleRepository.save(battle);
            return;
        }

        // *** CREATE ARMIES ***
        List<BattleTroopFightDTO> attackerArmy = new ArrayList<>();
        List<BattleTroopFightDTO> defenderArmy = new ArrayList<>();
        for (Troop troop : attackerList.get()) {
            BattleTroopFightDTO dto = new BattleTroopFightDTO(troop,
                    getTroopHp(battle.getAttackerKingdom().getId(), troop.getTroopType()),
                    getTroopAttack(battle.getAttackerKingdom().getId(), troop.getTroopType()),
                    getTroopDefence(battle.getAttackerKingdom().getId(), troop.getTroopType()));
            attackerArmy.add(dto);
        }
        for (Troop troop : defenderList.get()) {
            BattleTroopFightDTO dto = new BattleTroopFightDTO(troop,
                    getTroopHp(battle.getDefenderKingdom().getId(), troop.getTroopType()),
                    getTroopAttack(battle.getDefenderKingdom().getId(), troop.getTroopType()),
                    getTroopDefence(battle.getDefenderKingdom().getId(), troop.getTroopType()));
            defenderArmy.add(dto);
        }

        // *** FIGHT ***
        for (BattleTroopFightDTO attackerTroop : attackerArmy) {
            if (!attackerTroop.isAlive()) {
                continue;
            }
            for (BattleTroopFightDTO defenderTroop : defenderArmy) {
                if (!defenderTroop.isAlive()) {
                    continue;
                } else if (!attackerTroop.isAlive()) {
                    break;
                }
                do {
                    attack(attackerTroop, defenderTroop, battle);
                    if (!defenderTroop.isAlive()) {
                        continue;
                    }
                    attack(defenderTroop, attackerTroop, battle);
                } while (attackerTroop.isAlive() && defenderTroop.isAlive());
            }
        }

        // *** RESULT ***
        long attackerAliveTroops = attackerArmy.stream().filter(BattleTroopFightDTO::isAlive).count();
        long defenderAliveTroops = defenderArmy.stream().filter(BattleTroopFightDTO::isAlive).count();
        battle.setAttackerWins(attackerAliveTroops > defenderAliveTroops);
        battleRepository.save(battle);

        // *** PLUNDER ***
        plunder(battle);

        long goldStolen = battle.getDefenderKingdom().getGoldAmount();
/*            battle.getAttackerKingdom().setGoldAmount(battle.getAttackerKingdom().getGoldAmount() + goldStolen);
            battle.getDefenderKingdom().setGoldAmount(battle.getDefenderKingdom().getGoldAmount() - goldStolen);*/
        long foodStolen = battle.getDefenderKingdom().getFoodAmount();
/*            battle.getAttackerKingdom().setFoodAmount(battle.getAttackerKingdom().getFoodAmount() + foodStolen);
            battle.getDefenderKingdom().setFoodAmount(battle.getDefenderKingdom().getFoodAmount() - foodStolen);
            kingdomRepository.save(battle.getAttackerKingdom());
            kingdomRepository.save(battle.getDefenderKingdom());*/
        battle.setGoldStolen(goldStolen);
        battle.setFoodStolen(foodStolen);
        battleRepository.save(battle);
    }

    @Override
    public void attack(BattleTroopFightDTO attacker, BattleTroopFightDTO defender, Battle battle) {
        int damage = (attacker.getAttack() - defender.getDefence()) > 0 ? (attacker.getAttack() - defender.getDefence()) : 1;
        int health = defender.getHp() - damage;
        defender.setHp(health);
        if (defender.getHp() <= 0) {
            defender.setAlive(false);
            defender.getTroop().setDestroyTime(System.currentTimeMillis() / 1000);
            defender.getTroop().setBattle(battle);
            troopRepository.save(defender.getTroop());
        }
    }

    @Override
    public void plunder(Battle battle) {
        if (battle.getAttackerWins()) {
            long goldStolen = battle.getDefenderKingdom().getGoldAmount();
            battle.getAttackerKingdom().setGoldAmount(battle.getAttackerKingdom().getGoldAmount() + goldStolen);
            battle.getDefenderKingdom().setGoldAmount(battle.getDefenderKingdom().getGoldAmount() - goldStolen);
            long foodStolen = battle.getDefenderKingdom().getFoodAmount();
            battle.getAttackerKingdom().setFoodAmount(battle.getAttackerKingdom().getFoodAmount() + foodStolen);
            battle.getDefenderKingdom().setFoodAmount(battle.getDefenderKingdom().getFoodAmount() - foodStolen);
            kingdomRepository.save(battle.getAttackerKingdom());
            kingdomRepository.save(battle.getDefenderKingdom());
            battle.setGoldStolen(goldStolen);
            battle.setFoodStolen(foodStolen);
            battleRepository.save(battle);
        }
    }

    @Override
    public void destructionAttack(Long battleId) {
        Battle battle = battleRepository.getById(battleId);

        // *** TROOP ATTACK ***
        troopAttack(battleId);

        Optional<List<Troop>> attackerArtyList = troopRepository.findAttackerTroopsForDestructionAttack(battleId, battle.getAttackerKingdom().getId());
        Optional<List<Building>> defenderBuildingList = buildingRepository.findDefenderBuildingsForDestructionAttack(battle.getDefenderKingdom().getId());

        // *** DESTRUCTION ATTACK ***
        if (battle.getAttackerWins()) {

            List<BuildingDestructionAttackDTO> buildingDestructionAttackDTOList = new ArrayList<>();
            for (Building building : defenderBuildingList.get()) {
                BuildingDestructionAttackDTO dto = new BuildingDestructionAttackDTO(building, getBuildingHp(building));
                buildingDestructionAttackDTOList.add(dto);
            }

            if (buildingDestructionAttackDTOList.isEmpty()) {
                return;
            }

            int attack = getTroopAttack(battle.getAttackerKingdom().getId(), TroopType.TREBUCHET) * attackerArtyList.get().size();
            do {

                if (buildingDestructionAttackDTOList.isEmpty()) {
                    break;
                }

                Random rand = new Random();
                BuildingDestructionAttackDTO randomBuilding = buildingDestructionAttackDTOList.get(rand.nextInt(buildingDestructionAttackDTOList.size()));

                if ((randomBuilding.getBuilding().getType() == BuildingType.TOWN_HALL && randomBuilding.getBuilding().getLevel() == 1) ||
                        (randomBuilding.getBuilding().getType() == BuildingType.GOLD_MINE && randomBuilding.getBuilding().getLevel() == 1) ||
                        (randomBuilding.getBuilding().getType() == BuildingType.FARM && randomBuilding.getBuilding().getLevel() == 1)) {

                    buildingDestructionAttackDTOList.remove(randomBuilding);

                } else if (randomBuilding.getBuilding().getLevel() > 0) {
                    attack -= randomBuilding.getHp();
                    randomBuilding.getBuilding().setLevel(randomBuilding.getBuilding().getLevel() - 1);
                    buildingRepository.save(randomBuilding.getBuilding());
                    if (randomBuilding.getBuilding().getLevel() == 0) {
                        DestroyedBuilding building = new DestroyedBuilding(battle, battle.getDefenderKingdom(), randomBuilding.getBuilding().getType(), System.currentTimeMillis() / 1000);
                        destroyedBuildingRepository.save(building);
                        buildingDestructionAttackDTOList.remove(randomBuilding);
                        buildingRepository.delete(randomBuilding.getBuilding());
                    }
                }

            } while (attack > 0);

        } else {
            for (Troop troop : attackerArtyList.get()) {
                troop.setDestroyTime(System.currentTimeMillis() / 1000);
                troopRepository.save(troop);
            }
        }
    }

    @Override
    public void spyAttack(Long battleId) {
        Battle battle = battleRepository.getById(battleId);
        Optional<List<Troop>> attackerList = troopRepository.findAttackerTroopsForSpyAttack(battleId, battle.getAttackerKingdom().getId());
        Optional<List<Troop>> defenderList = troopRepository.findDefenderTroopsForSpyAttack(battle.getDefenderKingdom().getId());

        if (battle.getAttackerWins() != null) {
            return;
        }

        if (defenderList.isEmpty()) {
            battle.setAttackerWins(true);
            battleRepository.save(battle);

        } else if (defenderList.get().size() >= attackerList.get().size()) {
            battle.setAttackerWins(false);
            battleRepository.save(battle);
            for (Troop troop : attackerList.get()) {
                troop.setDestroyTime(System.currentTimeMillis() / 1000);
                troopRepository.save(troop);
            }
        } else {
            battle.setAttackerWins(true);
            battleRepository.save(battle);
            for (Troop troop : defenderList.get()) {
                troop.setDestroyTime(System.currentTimeMillis() / 1000);
                troop.setBattle(battle);
                troopRepository.save(troop);
            }
        }
    }

    @Override
    public void takeoverAttack(Long battleId) {
        Battle battle = battleRepository.getById(battleId);

        // *** TROOP ATTACK ***
        troopAttack(battleId);

        // *** TAKEOVER ATTACK ***
        if (battle.getAttackerWins()) {
            int aleaIactaEst = randomNumber();

            if (aleaIactaEst == 6) {
                Kingdom defenderKingdom = battle.getDefenderKingdom();
                defenderKingdom.setAppUser(battle.getAttackerKingdom().getAppUser());
                kingdomRepository.save(defenderKingdom);
            }
        } else {
            Optional<List<Troop>> diplomats = troopRepository.findAllByBattleIdAndKingdomIdAndTroopType(
                    battleId, battle.getAttackerKingdom().getId(), TroopType.DIPLOMAT);
            for (Troop troop : diplomats.get()) {
                troop.setDestroyTime(System.currentTimeMillis() / 1000);
                troopRepository.save(troop);
            }
        }
    }

    public int randomNumber() {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }

    @Override
    public ResponseEntity<?> battleResult(Battle battle, Long kingdomId) {

        JSONObject attackerDetails = new JSONObject();
        attackerDetails.put("kingdomId", battle.getAttackerKingdom().getId());
        attackerDetails.put("kingdomName", battle.getAttackerKingdom().getName());
        attackerDetails.put("ruler", battle.getAttackerKingdom().getAppUser().getUsername());

        JSONObject attackerResourcesStolen = new JSONObject();
        attackerResourcesStolen.put("gold", battle.getGoldStolen());
        attackerResourcesStolen.put("food", battle.getFoodStolen());

        JSONObject attacker = new JSONObject();
        attacker.put("details", attackerDetails);
        attacker.put("resourcesStolen", attackerResourcesStolen);
        attacker.put("troopsLost", getTroopsForBattleResult(battle.getId(), battle.getAttackerKingdom().getId()));

        JSONObject defenderDetails = new JSONObject();
        defenderDetails.put("kingdomId", battle.getDefenderKingdom().getId());
        defenderDetails.put("kingdomName", battle.getDefenderKingdom().getName());
        defenderDetails.put("ruler", battle.getDefenderKingdom().getAppUser().getUsername());

        JSONObject defender = new JSONObject();
        defender.put("details", defenderDetails);
        defender.put("troopsLost", getTroopsForBattleResult(battle.getId(), battle.getDefenderKingdom().getId()));
        if (battle.getBattleType() == BattleType.DESTRUCTION_ATTACK) {
            defender.put("buildingsLost", getBuildingsForBattleResult(battle.getId(), battle.getDefenderKingdom().getId()));
        }

        JSONObject result = new JSONObject();
        result.put("battleId", battle.getId());
        result.put("resolutionTime", battle.getBattleTime());
        result.put("battleType", battle.getBattleType());
        result.put("attacker", attacker);
        result.put("defender", defender);

        if (battle.getBattleType() == BattleType.SPY_ATTACK && battle.getAttackerWins()) {
            JSONObject intelligence = new JSONObject();
            intelligence.put("amount of gold", battle.getDefenderKingdom().getGoldAmount());
            intelligence.put("amount of food", battle.getDefenderKingdom().getFoodAmount());

            List<Building> buildings = buildingRepository.findBuildingsInKingdom(battle.getDefenderKingdom().getId());
            Map<BuildingType, Integer> buildingsType = new HashMap<>();
            for (Building building : buildings) {
                buildingsType.put(building.getType(), building.getLevel());
            }
            intelligence.put("buildings", buildingsType);
            result.put("intelligence", intelligence);
        }

        if (battle.getBattleType() == BattleType.TAKEOVER_ATTACK && battle.getAttackerWins()) {
            JSONObject takeover = new JSONObject();
            if (battle.getAttackerKingdom().getAppUser() == battle.getDefenderKingdom().getAppUser()) {
                takeover.put("result", "Diplomat mission was successful. Defender kingdom joined you!");
            } else {
                takeover.put("result", "Diplomat failed. Defender kingdom won´t join you");
            }
            result.put("takeover", takeover);
        }

        if (Objects.equals(kingdomId, battle.getAttackerKingdom().getId())) { // if attacker request result

            if (battle.getReturnTime() >= System.currentTimeMillis() / 1000) { // check if results are available
                JSONObject body = new JSONObject();
                body.put("info", "Results are not available. Troops are still on the way!");
                return new ResponseEntity<>(body, HttpStatus.OK);
            }
            if (!battle.getAttackerWins()) { // if attacker lost battle
                JSONObject body = new JSONObject();
                body.put("info", "Nobody returned!");
                return new ResponseEntity<>(body, HttpStatus.OK);

            } else { //if attacker won battle
                String battleResult = (battle.getAttackerWins()) ? "win" : "lost";
                result.put("result", battleResult);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

        } else if (Objects.equals(kingdomId, battle.getDefenderKingdom().getId())) { // if defender request result

            if (battle.getBattleTime() >= System.currentTimeMillis() / 1000) { // check if results are available
                JSONObject body = new JSONObject();
                body.put("info", "Results are not available. Troops are still on the way!");
                return new ResponseEntity<>(body, HttpStatus.OK);
            }

            String battleResult = (battle.getAttackerWins()) ? "lost" : "win";
            result.put("result", battleResult);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {
            JSONObject body = new JSONObject();
            body.put("error", "You were not involved in this battle. Stay out of it!");
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public Map<String, Integer> getTroopsForBattleResult(Long battleId, Long kingdomId) {
        Optional<List<Troop>> list = troopRepository.findAllByBattleIdAndKingdomIdAndDestroyTimeIsNotNull(battleId, kingdomId);
        Map<String, Integer> troopCounts = new HashMap<>();
        for (Troop troop : list.get()) {
            if (!troopCounts.containsKey(troop.getTroopType().getName())) {
                troopCounts.put(troop.getTroopType().getName(), 1);
            } else {
                troopCounts.put(troop.getTroopType().getName(), troopCounts.get(troop.getTroopType().getName()) + 1);
            }
        }
        return troopCounts;
    }

    @Override
    public Map<String, Integer> getBuildingsForBattleResult(Long battleId, Long kingdomId) {
        Optional<List<DestroyedBuilding>> list = destroyedBuildingRepository.findAllByBattleIdAndKingdomId(battleId, kingdomId);
        Map<String, Integer> buildingCounts = new HashMap<>();
        for (DestroyedBuilding building : list.get()) {
            if (!buildingCounts.containsKey(building.getBuildingType().getName())) {
                buildingCounts.put(building.getBuildingType().getName(), 1);
            } else {
                buildingCounts.put(building.getBuildingType().getName(), buildingCounts.get(building.getBuildingType().getName()) + 1);
            }
        }
        return buildingCounts;
    }

    @Override
    public void troopsCameBackFromTheBattle(Long battleId) { // run method when return time
        Battle battle = battleRepository.getById(battleId);
        if (battle.getAttackerWins()) {
            Optional<List<Troop>> troops = troopRepository.findAllByBattleIdAndKingdomIdAndDestroyTimeIsNull(battleId, battle.getAttackerKingdom().getId());
            for (Troop troop : troops.get()) {
                troop.setBattle(null);
                troopRepository.save(troop);
            }
        }
    }

    @Override
    public void chooseBattleType(Battle battle) {
        if (battle.getBattleType() == BattleType.TROOP_ATTACK) {
            troopAttack(battle.getId());
        }
        if (battle.getBattleType() == BattleType.SPY_ATTACK) {
            spyAttack(battle.getId());
        }
        if (battle.getBattleType() == BattleType.DESTRUCTION_ATTACK) {
            destructionAttack(battle.getId());
        }
        if (battle.getBattleType() == BattleType.TAKEOVER_ATTACK) {
            takeoverAttack(battle.getId());
        }
    }

    @Override
    public ResponseEntity<?> listOfBattles(Long kingdomId) {

        List<BattleDTO> battleDTOS = new ArrayList<>();
        List<Battle> battles = battleRepository.findAllBattles(kingdomId);

        for (Battle battle : battles) {
            battleDTOS.add(new BattleDTO(
                    battle.getId(),
                    battle.getBattleType(),
                    battle.getArmyLeftKingdomTime(),
                    battle.getBattleTime(),
                    battle.getReturnTime(),
                    battle.getAttackerWins(),
                    battle.getGoldStolen(),
                    battle.getFoodStolen(),
                    battle.getAttackerKingdom().getId(),
                    battle.getDefenderKingdom().getId()
            ));
        }

        battleDTOS.sort((o1, o2) -> (int) (o2.getBattleTime() - o1.getBattleTime()));

        JSONObject result = new JSONObject();
        result.put("pastBattles", battleDTOS);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }


}