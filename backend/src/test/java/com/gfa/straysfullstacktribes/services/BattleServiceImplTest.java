package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.models.dtos.BattleRequestDTO;
import com.gfa.straysfullstacktribes.models.dtos.BattleTroopDTO;
import com.gfa.straysfullstacktribes.models.dtos.BattleTroopFightDTO;
import com.gfa.straysfullstacktribes.models.enums.BattleType;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.repositories.*;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import org.junit.jupiter.api.Test;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BattleServiceImplTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    BattleService battleService;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    KingdomRepository kingdomRepository;
    @Autowired
    UnitLevelRepository unitLevelRepository;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    TroopRepository troopRepository;
    @Autowired
    BuildingRepository buildingRepository;
    @Autowired
    DestroyedBuildingRepository destroyedBuildingRepository;

    @MockBean
    RegistrationService registrationService;
    @MockBean
    EmailService emailService;
    @MockBean
    UnitLevelService unitLevelService;

    @Test
    void saveBattle() {
        assertEquals(0, battleRepository.findAll().size());
        battleRepository.save(new Battle());
        assertEquals(1, battleRepository.findAll().size());
    }

    @Test
    void sendTroopsForBattle() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop troop = new Troop(TroopType.KNIGHT, kingdom);
        troop.setEndOfTrainingTime(1639737103L);
        troopRepository.save(troop);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TROOP_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO = new BattleTroopDTO();
        battleTroopDTO.setType(TroopType.KNIGHT);
        battleTroopDTO.setQuantity(1L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.TROOP_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(1, troopRepository.findById(1L).get().getBattle().getId());
    }

    @Test
    void calculateDistanceWithOneCommonAxis() {
        Kingdom attackerKingdom = new Kingdom();
        Kingdom defenderKingdom = new Kingdom();
        attackerKingdom.setCorX(1L);
        attackerKingdom.setCorY(5L);
        defenderKingdom.setCorX(1L);
        defenderKingdom.setCorY(1L);
        Double distance = battleService.calculateDistance(attackerKingdom, defenderKingdom);
        assertEquals(4.0, distance);
    }

    @Test
    void calculateDistanceWithoutCommonAxis() {
        Kingdom attackerKingdom = new Kingdom();
        Kingdom defenderKingdom = new Kingdom();
        attackerKingdom.setCorX(1L);
        attackerKingdom.setCorY(5L);
        defenderKingdom.setCorX(3L);
        defenderKingdom.setCorY(15L);
        Double distance = battleService.calculateDistance(attackerKingdom, defenderKingdom);
        assertEquals(10.2, distance);
    }

    @Test
    void getTroopSpeedWithoutUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int speed = DefaultValueReaderUtil.getInt("troops.knight.defaultSpeed");
        assertEquals(speed, battleService.getTroopSpeed(1L, TroopType.KNIGHT));
    }

    @Test
    void getTroopSpeedWithUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        UnitLevel unitLevel = new UnitLevel(kingdom, TroopType.KNIGHT);
        unitLevel.setUpgradeLevel(3);
        unitLevelRepository.save(unitLevel);
        int speed = DefaultValueReaderUtil.getInt("troops.knight.defaultSpeed");
        assertEquals(speed + 3, battleService.getTroopSpeed(kingdom.getId(), TroopType.KNIGHT));
    }

    @Test
    void getTroopAttackWithoutUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int attack = DefaultValueReaderUtil.getInt("troops.knight.defaultAttack");
        assertEquals(attack, battleService.getTroopAttack(1L, TroopType.KNIGHT));
    }

    @Test
    void getTroopAttackWithUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        UnitLevel unitLevel = new UnitLevel(kingdom, TroopType.KNIGHT);
        unitLevel.setUpgradeLevel(3);
        unitLevelRepository.save(unitLevel);
        int attack = DefaultValueReaderUtil.getInt("troops.knight.defaultAttack");
        assertEquals(attack + 3, battleService.getTroopAttack(kingdom.getId(), TroopType.KNIGHT));
    }

    @Test
    void getTroopDefenceWithoutUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int defence = DefaultValueReaderUtil.getInt("troops.knight.defaultDefence");
        assertEquals(defence, battleService.getTroopDefence(1L, TroopType.KNIGHT));
    }

    @Test
    void getTroopDefenceWithUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        UnitLevel unitLevel = new UnitLevel(kingdom, TroopType.KNIGHT);
        unitLevel.setUpgradeLevel(3);
        unitLevelRepository.save(unitLevel);
        int defence = DefaultValueReaderUtil.getInt("troops.knight.defaultDefence");
        assertEquals(defence + 3, battleService.getTroopDefence(kingdom.getId(), TroopType.KNIGHT));
    }

    @Test
    void getTroopHpWithoutUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int hp = DefaultValueReaderUtil.getInt("troops.knight.defaultHp");
        assertEquals(hp, battleService.getTroopHp(1L, TroopType.KNIGHT));
    }

    @Test
    void getTroopHpWithUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        UnitLevel unitLevel = new UnitLevel(kingdom, TroopType.KNIGHT);
        unitLevel.setUpgradeLevel(3);
        unitLevelRepository.save(unitLevel);
        int hp = DefaultValueReaderUtil.getInt("troops.knight.defaultHp");
        assertEquals(hp + 3, battleService.getTroopHp(kingdom.getId(), TroopType.KNIGHT));
    }

    @Test
    void getBuildingHp() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Building building = new Building(kingdom, BuildingType.ACADEMY, 5);
        int hp = DefaultValueReaderUtil.getInt("buildings.academy.defaultHp");
        assertEquals(hp, battleService.getBuildingHp(building));
    }


    @Test
    void getSpeedOfTheSlowestTroopWithoutUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        BattleTroopDTO footman = new BattleTroopDTO(TroopType.FOOTMAN, 1L);
        BattleTroopDTO knight = new BattleTroopDTO(TroopType.KNIGHT, 1L);
        battleRequestDTO.addTroops(footman);
        battleRequestDTO.addTroops(knight);
        int defaultSpeed = DefaultValueReaderUtil.getInt("troops.footman.defaultSpeed");
        int speed = battleService.getSpeedOfTheSlowestTroop(battleRequestDTO, 1L);
        assertEquals(defaultSpeed, speed);
    }

    @Test
    void getSpeedOfTheSlowestTroopWithUpgradeLevel() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        BattleTroopDTO footman = new BattleTroopDTO(TroopType.FOOTMAN, 1L);
        BattleTroopDTO knight = new BattleTroopDTO(TroopType.KNIGHT, 1L);
        battleRequestDTO.addTroops(footman);
        battleRequestDTO.addTroops(knight);

        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        Kingdom kingdom = new Kingdom("test", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        UnitLevel unitLevel = new UnitLevel(kingdom, TroopType.FOOTMAN);
        unitLevel.setUpgradeLevel(3);
        unitLevelRepository.save(unitLevel);

        int defaultSpeed = DefaultValueReaderUtil.getInt("troops.knight.defaultSpeed"); // After FOOTMAN update, KNIGHT will be slower
        int speed = battleService.getSpeedOfTheSlowestTroop(battleRequestDTO, 1L);
        assertEquals(defaultSpeed, speed);
    }

    @Test
    void attack() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop);
        Troop defenderTroop = new Troop(TroopType.KNIGHT, kingdom2);
        defenderTroop.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TROOP_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopFightDTO attacker = new BattleTroopFightDTO();
        attacker.setTroop(attackerTroop);
        attacker.setAttack(10);
        attacker.setDefence(5);
        attacker.setHp(15);
        BattleTroopFightDTO defender = new BattleTroopFightDTO();
        defender.setTroop(defenderTroop);
        defender.setAttack(10);
        defender.setDefence(5);
        defender.setHp(15);

        battleService.attack(attacker, defender, battle);
        assertEquals(10, defender.getHp());
        battleService.attack(defender, attacker, battle);
        assertEquals(10, attacker.getHp());
        battleService.attack(attacker, defender, battle);
        battleService.attack(attacker, defender, battle);
        assertFalse(defender.isAlive());
        assertEquals(0, defender.getHp());
        assertNotNull(troopRepository.findById(defenderTroop.getId()).get().getDestroyTime());
        assertEquals(1, troopRepository.findById(defenderTroop.getId()).get().getBattle().getId());
    }

    @Test
    void plunder() {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);
        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TROOP_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battle.setAttackerWins(false);
        battleRepository.save(battle);

        battleService.plunder(battle);
        assertEquals(100, kingdomRepository.getById(kingdom.getId()).getFoodAmount());
        assertEquals(100, kingdomRepository.getById(kingdom.getId()).getGoldAmount());

        battle.setAttackerWins(true);
        battleRepository.save(battle);
        battleService.plunder(battle);
        assertEquals(200, kingdomRepository.getById(kingdom.getId()).getFoodAmount());
        assertEquals(200, kingdomRepository.getById(kingdom.getId()).getGoldAmount());
        assertEquals(0, kingdomRepository.getById(kingdom2.getId()).getFoodAmount());
        assertEquals(0, kingdomRepository.getById(kingdom2.getId()).getGoldAmount());
    }

    @Test
    void troopAttackWhenDefenderHasNoArmy() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop troop = new Troop(TroopType.KNIGHT, kingdom);
        troop.setEndOfTrainingTime(1639737103L);
        troopRepository.save(troop);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TROOP_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO = new BattleTroopDTO();
        battleTroopDTO.setType(TroopType.KNIGHT);
        battleTroopDTO.setQuantity(1L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.TROOP_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(1, troopRepository.findById(1L).get().getBattle().getId());

        assertEquals(100L, kingdom2.getGoldAmount());
        assertEquals(100L, kingdom2.getFoodAmount());

        battleService.troopAttack(battle.getId());

        assertTrue(battleRepository.getById(battle.getId()).getAttackerWins());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getGoldAmount());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getFoodAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getGoldAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getFoodAmount());
    }

    @Test
    void troopAttack() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop1 = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop1);
        Troop defenderTroop2 = new Troop(TroopType.KNIGHT, kingdom2);
        defenderTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop2);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TROOP_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO = new BattleTroopDTO();
        battleTroopDTO.setType(TroopType.KNIGHT);
        battleTroopDTO.setQuantity(1L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.TROOP_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom.getId()).size());
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom2.getId()).size());

        assertEquals(100L, kingdom2.getGoldAmount());
        assertEquals(100L, kingdom2.getFoodAmount());

        battleService.troopAttack(battle.getId());
        assertNull(troopRepository.findById(1L).get().getDestroyTime());
        assertNotNull(troopRepository.findById(2L).get().getDestroyTime());
    }

    @Test
    void spyAttackAttackerLost() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop1 = new Troop(TroopType.SCOUT, kingdom);
        attackerTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop1);
        Troop defenderTroop1 = new Troop(TroopType.SCOUT, kingdom2);
        defenderTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop1);
        Troop defenderTroop2 = new Troop(TroopType.SCOUT, kingdom2);
        defenderTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop2);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.SPY_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO = new BattleTroopDTO();
        battleTroopDTO.setType(TroopType.SCOUT);
        battleTroopDTO.setQuantity(1L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.SPY_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom.getId()).size());
        assertEquals(2, troopRepository.findAllByKingdom_Id(kingdom2.getId()).size());

        battleService.spyAttack(kingdom.getId());

        assertNotNull(troopRepository.findById(1L).get().getDestroyTime());
        assertFalse(battleRepository.getById(1L).getAttackerWins());
    }

    @Test
    void spyAttackAttackerWin() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop1 = new Troop(TroopType.SCOUT, kingdom);
        attackerTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop1);
        Troop attackerTroop2 = new Troop(TroopType.SCOUT, kingdom);
        attackerTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop2);
        Troop defenderTroop2 = new Troop(TroopType.SCOUT, kingdom2);
        defenderTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop2);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.SPY_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO = new BattleTroopDTO();
        battleTroopDTO.setType(TroopType.SCOUT);
        battleTroopDTO.setQuantity(2L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.SPY_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(2, troopRepository.findAllByKingdom_Id(kingdom.getId()).size());
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom2.getId()).size());

        battleService.spyAttack(kingdom.getId());

        assertNull(troopRepository.findById(1L).get().getDestroyTime());
        assertNull(troopRepository.findById(2L).get().getDestroyTime());
        assertNotNull(troopRepository.findById(3L).get().getDestroyTime());
        assertTrue(battleRepository.getById(1L).getAttackerWins());
    }

    @Test
    void destructionAttack() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop1 = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop1);
        Troop attackerTroop2 = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop2);
        Troop attackerTroop3 = new Troop(TroopType.TREBUCHET, kingdom);
        attackerTroop3.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop3);
        Troop attackerTroop4 = new Troop(TroopType.TREBUCHET, kingdom);
        attackerTroop4.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop4);
        Troop attackerTroop5 = new Troop(TroopType.TREBUCHET, kingdom);
        attackerTroop5.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop5);
        Troop attackerTroop6 = new Troop(TroopType.TREBUCHET, kingdom);
        attackerTroop6.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop6);
        Troop attackerTroop7 = new Troop(TroopType.TREBUCHET, kingdom);
        attackerTroop7.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop7);
        Troop defenderTroop1 = new Troop(TroopType.KNIGHT, kingdom2);
        defenderTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop1);

        Building building1 = new Building(kingdom2, BuildingType.TOWN_HALL, 1, 2);
        buildingRepository.save(building1);
        Building building2 = new Building(kingdom2, BuildingType.ACADEMY, 2, 1);
        buildingRepository.save(building2);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.DESTRUCTION_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO1 = new BattleTroopDTO();
        battleTroopDTO1.setType(TroopType.KNIGHT);
        battleTroopDTO1.setQuantity(2L);
        BattleTroopDTO battleTroopDTO2 = new BattleTroopDTO();
        battleTroopDTO2.setType(TroopType.TREBUCHET);
        battleTroopDTO2.setQuantity(5L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.DESTRUCTION_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO1);
        battleRequestDTO.addTroops(battleTroopDTO2);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(7, troopRepository.findAllByKingdom_Id(kingdom.getId()).size());
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom2.getId()).size());

        battleService.destructionAttack(battle.getId());

        assertTrue(battleRepository.getById(battle.getId()).getAttackerWins());
        assertNotNull(troopRepository.getById(8L).getDestroyTime());
        assertNotNull(destroyedBuildingRepository.getById(1L).getDestroyTime());
        assertEquals(1, buildingRepository.getById(1L).getLevel());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getGoldAmount());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getFoodAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getGoldAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getFoodAmount());
    }

    /*@Test //cannot fake random number to be 6
    void takeoverAttack() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        AppUser appUser = new AppUser("test", "test@test.com", "test", "123");
        appUserRepository.save(appUser);
        AppUser appUser2 = new AppUser("test2", "test2@test.com", "test", "456");
        appUserRepository.save(appUser2);
        Kingdom kingdom = new Kingdom("test1", 1L, 1L, appUser, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom);
        Kingdom kingdom2 = new Kingdom("test2", 1L, 6L, appUser2, 100L, 100L, 10, 10);
        kingdomRepository.save(kingdom2);

        Troop attackerTroop1 = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop1);
        Troop attackerTroop2 = new Troop(TroopType.KNIGHT, kingdom);
        attackerTroop2.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop2);
        Troop attackerTroop3 = new Troop(TroopType.DIPLOMAT, kingdom);
        attackerTroop3.setEndOfTrainingTime(1639737103L);
        troopRepository.save(attackerTroop3);
        Troop defenderTroop1 = new Troop(TroopType.KNIGHT, kingdom2);
        defenderTroop1.setEndOfTrainingTime(1639737103L);
        troopRepository.save(defenderTroop1);

        Battle battle = new Battle();
        battle.setAttackerKingdom(kingdom);
        battle.setDefenderKingdom(kingdom2);
        battle.setBattleType(BattleType.TAKEOVER_ATTACK);
        battle.setBattleTime(123L);
        battle.setReturnTime(123L);
        battleRepository.save(battle);

        BattleTroopDTO battleTroopDTO1 = new BattleTroopDTO();
        battleTroopDTO1.setType(TroopType.KNIGHT);
        battleTroopDTO1.setQuantity(2L);
        BattleTroopDTO battleTroopDTO2 = new BattleTroopDTO();
        battleTroopDTO2.setType(TroopType.DIPLOMAT);
        battleTroopDTO2.setQuantity(1L);

        BattleRequestDTO battleRequestDTO = new BattleRequestDTO();
        battleRequestDTO.setDefenderKingdomId(kingdom2.getId());
        battleRequestDTO.setBattleType(BattleType.TAKEOVER_ATTACK);
        battleRequestDTO.addTroops(battleTroopDTO1);
        battleRequestDTO.addTroops(battleTroopDTO2);

        assertNull(troopRepository.findById(1L).get().getBattle());
        battleService.sendTroopsIntoBattle(battleRequestDTO, battle);
        assertEquals(3, troopRepository.findAllByKingdom_Id(kingdom.getId()).size());
        assertEquals(1, troopRepository.findAllByKingdom_Id(kingdom2.getId()).size());

        //Mockito.when(battleServiceBean.randomNumber()).thenReturn(6);

        battleService.takeoverAttack(battle.getId());

        assertTrue(battleRepository.getById(battle.getId()).getAttackerWins());
        assertNotNull(troopRepository.getById(4L).getDestroyTime());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getGoldAmount());
        assertEquals(200L, kingdomRepository.getById(kingdom.getId()).getFoodAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getGoldAmount());
        assertEquals(0L, kingdomRepository.getById(kingdom2.getId()).getFoodAmount());
        assertEquals(1, kingdomRepository.getById(2L).getAppUser().getId());
    }*/
}