package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Battle;
import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.Troop;
import com.gfa.straysfullstacktribes.models.dtos.BattleRequestDTO;
import com.gfa.straysfullstacktribes.models.dtos.BattleTroopFightDTO;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BattleService {

    void saveBattle(Battle battle);

    void sendTroopsIntoBattle(BattleRequestDTO battleRequestDTO, Battle battle);

    List<Troop> getTroopsForBattle(Long id, TroopType troop);

    Double calculateDistance(Kingdom attackerKingdom, Kingdom defenderKingdom);

    Integer getSpeedOfTheSlowestTroop(BattleRequestDTO battleRequestDTO, Long attackerKingdomId);

    Integer getTroopSpeed(Long kingdomId, TroopType troopType);

    Integer getTroopAttack(Long kingdomId, TroopType troopType);

    Integer getTroopDefence(Long kingdomId, TroopType troopType);

    Integer getTroopHp(Long kingdomId, TroopType troopType);

    Integer getBuildingHp(Building building);

    void troopAttack(Long battleId);

    void attack(BattleTroopFightDTO attacker, BattleTroopFightDTO defender, Battle battle);

    void plunder(Battle battle);

    void destructionAttack(Long battleId);

    void spyAttack(Long battleId);

    ResponseEntity<?> battleResult(Battle battle, Long kingdomId);

    void takeoverAttack(Long battleId);

    Map<String, Integer> getTroopsForBattleResult(Long battleId, Long kingdomId);

    Map<String, Integer> getBuildingsForBattleResult(Long battleId, Long kingdomId);

    void troopsCameBackFromTheBattle(Long battleId);

    void chooseBattleType (Battle battle);

    ResponseEntity<?> listOfBattles(Long kingdomId);
}
