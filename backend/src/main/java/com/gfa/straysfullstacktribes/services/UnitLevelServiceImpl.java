package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.UnitLevel;
import com.gfa.straysfullstacktribes.models.dtos.KingdomUnitLevelDTO;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.repositories.UnitLevelRepository;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UnitLevelServiceImpl implements UnitLevelService {

    private UnitLevelRepository unitLevelRepository;
    private KingdomService kingdomService;

    @Override
    public Integer getKingdomUnitLevel(Long kingdomId, TroopType troopType) {
        return unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel();
    }

    @Override
    public void createUnitLevelForNewKingdom(Kingdom kingdom) {
        for (TroopType type : TroopType.values()) {
            UnitLevel unitLevel = new UnitLevel(kingdom, type);
            unitLevelRepository.save(unitLevel);
        }
    }

    @Override
    public Boolean isUnitLevelUpgrading(Long kingdomId, TroopType troopType) {
        if (unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeFinishedAt() == null) {
            return false;
        } else return unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeFinishedAt()
                >=
                (System.currentTimeMillis() / 1000);
    }

    @Override
    public void upgradeTroopType(Long kingdomId, TroopType troopType)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        Kingdom kingdom = kingdomService.findKingdomById(kingdomId);

//      This will decrease the gold amount of selected kingdom by unit level + 1 amount of gold
        kingdom.setGoldAmount((kingdom.getGoldAmount() - ((long) DefaultValueReaderUtil.getInt(
                "buildings.academy.troopUpgradePriceCoefficient"))
                * ((unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType).getUpgradeLevel()) + 1)));
        kingdomService.saveKingdom(kingdom);

        List<UnitLevel> listOfUnitsToUpgrade = unitLevelRepository.
                findAllByKingdomIdAndUpgradeFinishedAtNotNullOrderByUpgradeFinishedAtDesc
                        (kingdomId);

        UnitLevel unitToUpgrade = unitLevelRepository.findAllByKingdomIdAndTroopType(kingdomId, troopType);

        if (listOfUnitsToUpgrade.isEmpty()) {
            unitToUpgrade.setFinishedAt(System.currentTimeMillis() / 1000
                    + (long) DefaultValueReaderUtil.getInt("buildings.academy.troopUpgradeTimeCoefficient")
                    * (unitToUpgrade.getUpgradeLevel() + 1));

        } else if (listOfUnitsToUpgrade.get(0).getUpgradeFinishedAt() == null ||
                listOfUnitsToUpgrade.get(0).getUpgradeFinishedAt() <= (System.currentTimeMillis() / 1000)) {

            unitToUpgrade.setFinishedAt(System.currentTimeMillis() / 1000
                    + (long) DefaultValueReaderUtil.getInt("buildings.academy.troopUpgradeTimeCoefficient")
                    * (unitToUpgrade.getUpgradeLevel() + 1));

        } else {
            unitToUpgrade.setFinishedAt(listOfUnitsToUpgrade.get(0).getUpgradeFinishedAt()
                    + (long) DefaultValueReaderUtil.getInt("buildings.academy.troopUpgradeTimeCoefficient")
                    * (unitToUpgrade.getUpgradeLevel() + 1));
        }
        saveUnitLevel(unitToUpgrade);
    }

    @Override
    public void saveUnitLevel(UnitLevel unitLevel) {
        unitLevelRepository.save(unitLevel);
    }

    @Override
    public List<KingdomUnitLevelDTO> getKingdomUnitLevel(Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {
        List<KingdomUnitLevelDTO> returnList = new ArrayList<>();

        List<UnitLevel> unitLevelList = unitLevelRepository.findAllByKingdomId(kingdomId);

        for (UnitLevel unit : unitLevelList) {

            String ability = "";
            int attack = 0;
            int defence = 0;

            Integer level = getKingdomUnitLevel(kingdomId, TroopType.fromName(unit.getTroopType().getName()));

            if (unit.getTroopType() == TroopType.PHALANX || unit.getTroopType() == TroopType.KNIGHT ||
                    unit.getTroopType() == TroopType.FOOTMAN) {
                ability = "Troop Attack";
                attack = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultAttack") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                        "combatUnits.increaseAttack"));
                defence = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultDefence") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                        "combatUnits.increaseDefence"));
            } else if (unit.getTroopType() == TroopType.SCOUT) {
                ability = "Spy Attack";
                attack = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultAttack");
                defence = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultDefence");
            } else if (unit.getTroopType() == TroopType.TREBUCHET) {
                ability = "Destroy Attack";
                attack = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultAttack") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                        "combatUnits.increaseAttack"));
                defence = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultDefence") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                        "combatUnits.increaseDefence"));
            } else if (unit.getTroopType() == TroopType.SETTLER) {
                ability = "Create new Kingdom";
                attack = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultAttack");
                defence = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultDefence");
            } else if (unit.getTroopType() == TroopType.DIPLOMAT) {
                ability = "Takeover Attack";
                attack = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultAttack");
                defence = DefaultValueReaderUtil.getInt("troops." + unit.getTroopType().getName().toLowerCase() +
                        ".defaultDefence");
            }
            returnList.add(new KingdomUnitLevelDTO(unit.getTroopType().getName(), level, ability, attack, defence));
        }
        return returnList;
    }
}
