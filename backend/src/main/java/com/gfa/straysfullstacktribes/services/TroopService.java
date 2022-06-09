package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.Troop;
import com.gfa.straysfullstacktribes.models.dtos.*;

import java.util.List;

public interface TroopService {

    List<TroopCreateResponseDTO> createTroops(TroopCreateDTO troopCreateDTO, Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException, DefaultValueNotFoundException;

    List<TroopCreateResponseDTO> createNonBattleUnits(TroopCreateDTO troopCreateDTO, Kingdom kingdom,
                                                      Integer troopLevel, Integer barracksLevel)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    List<TroopCreateResponseDTO> createBattleUnits(TroopCreateDTO troopCreateDTO, Kingdom kingdom,
                                                   Integer troopLevel, Integer barracksLevel)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    void troopQueueHandler(TroopCreateDTO troopCreateDTO, Kingdom kingdom, Integer barracksLevel, Troop troop)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    void save(Troop troop);

    List<TroopListDTO> getListOfTroops(Long kingdomId);

    List<TroopQueueDTO> getTrainingList(Long kingdomId);

    void deleteAllInQueue(Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    void deleteByType(Long kingdomId, DeleteTypeDTO deleteTypeDTO)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    void deleteAmount(Long kingdomId,  int amount)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException;

    List<Troop> removeTroopsNotInTraining(List<Troop> troopList);

    List<Troop>listSorter(List<Troop>troopList);
}
