package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.UnitLevel;
import com.gfa.straysfullstacktribes.models.dtos.KingdomUnitLevelDTO;
import com.gfa.straysfullstacktribes.models.enums.TroopType;

import java.util.List;

public interface UnitLevelService {

    Integer getKingdomUnitLevel(Long kingdomId, TroopType troopType);

    void createUnitLevelForNewKingdom(Kingdom kingdom);

    Boolean isUnitLevelUpgrading (Long kingdomId, TroopType troopType);

    void upgradeTroopType(Long kingdomId, TroopType troopType)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException, DefaultValueNotFoundException;

    void saveUnitLevel(UnitLevel unitLevel);

    List<KingdomUnitLevelDTO>getKingdomUnitLevel(Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException ;
}
