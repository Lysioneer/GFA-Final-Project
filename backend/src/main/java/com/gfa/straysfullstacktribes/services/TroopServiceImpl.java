package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.Troop;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class TroopServiceImpl implements TroopService {

    private TroopRepository troopRepository;
    private UnitLevelService unitLevelService;
    private KingdomService kingdomService;
    private BuildingService buildingService;

    @Override
    public List<TroopCreateResponseDTO> createTroops(TroopCreateDTO troopCreateDTO, Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        Kingdom kingdom = kingdomService.findKingdomById(kingdomId);
        Integer troopLevel = unitLevelService.getKingdomUnitLevel(kingdomId,
                TroopType.fromName(troopCreateDTO.getType()));
        Integer barracksLevel = buildingService.findKingdomBarracks(kingdomId).getLevel();

        //          This will decrease the gold amount of selected kingdom
        kingdom.setGoldAmount((kingdom.getGoldAmount() - (long) DefaultValueReaderUtil.getInt(
                "troops." + troopCreateDTO.getType().toLowerCase() + ".price")));
        kingdomService.saveKingdom(kingdom);

        if (Objects.equals(troopCreateDTO.getType(), "scout") ||
                Objects.equals(troopCreateDTO.getType(), "diplomat") ||
                Objects.equals(troopCreateDTO.getType(), "settler")) {
            return createNonBattleUnits(troopCreateDTO, kingdom, troopLevel, barracksLevel);
        } else
            return createBattleUnits(troopCreateDTO, kingdom, troopLevel, barracksLevel);
    }

    @Override
    public List<TroopCreateResponseDTO> createNonBattleUnits(TroopCreateDTO troopCreateDTO, Kingdom kingdom,
                                                             Integer level, Integer barracksLevel)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        List<TroopCreateResponseDTO> listOfResponses = new ArrayList<>();

        for (int i = 0; i < troopCreateDTO.getQuantity(); i++) {
            Troop trooper = new Troop(
                    TroopType.fromName(troopCreateDTO.getType()), kingdom);

            troopQueueHandler(troopCreateDTO, kingdom, barracksLevel, trooper);

            save(trooper);

            TroopCreateResponseDTO responseDTO = new TroopCreateResponseDTO(
                    trooper.getId(),
                    kingdom.getId(),
                    unitLevelService.getKingdomUnitLevel(kingdom.getId(), TroopType.fromName(troopCreateDTO.getType())),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultHp"),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultAttack"),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultDefence"),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultSpeed") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                            "nonCombatUnits.increaseSpeed")),
                    System.currentTimeMillis() / 1000,
                    trooper.getEndOfTrainingTime());

            listOfResponses.add(responseDTO);
        }
        return listOfResponses;
    }

    @Override
    public List<TroopCreateResponseDTO> createBattleUnits(TroopCreateDTO troopCreateDTO, Kingdom kingdom,
                                                          Integer level, Integer barracksLevel)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        List<TroopCreateResponseDTO> listOfResponses = new ArrayList<>();

        for (int i = 0; i < troopCreateDTO.getQuantity(); i++) {
            Troop trooper = new Troop(
                    TroopType.fromName(troopCreateDTO.getType()), kingdom);

            troopQueueHandler(troopCreateDTO, kingdom, barracksLevel, trooper);

            save(trooper);

            TroopCreateResponseDTO responseDTO = new TroopCreateResponseDTO(trooper.getId(), kingdom.getId(),
                    unitLevelService.getKingdomUnitLevel(kingdom.getId(), TroopType.fromName(troopCreateDTO.getType())),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultHp") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                            "combatUnits.increaseHp")),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultAttack") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                            "combatUnits.increaseAttack")),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultDefence") + (level * DefaultValueReaderUtil.getInt("unitLevelStatsIncrement." +
                            "combatUnits.increaseDefence")),
                    DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                            ".defaultSpeed"),
                    System.currentTimeMillis() / 1000,
                    trooper.getEndOfTrainingTime());

            listOfResponses.add(responseDTO);
        }
        return listOfResponses;
    }

    @Override
    public void save(Troop troop) {
        troopRepository.save(troop);
    }

    @Override
    public void troopQueueHandler(TroopCreateDTO troopCreateDTO, Kingdom kingdom, Integer barracksLevel, Troop troop)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        Optional<List<Troop>> troopList = troopRepository
                .findAllByKingdomIdAndEndOfTrainingTimeNotNullOrderByEndOfTrainingTimeDesc(kingdom.getId());

        if (troopList.get().isEmpty()) {
            troop.setEndTime(System.currentTimeMillis() / 1000
                    + DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                    ".trainingTimeCoefficient") -
                    ((long) barracksLevel * DefaultValueReaderUtil.getInt(
                            "buildings.barracks.troopTimeTrainingDecreasePerLevel")));

        } else if (troopList.get().get(0).getEndOfTrainingTime() <= (System.currentTimeMillis() / 1000)) {
            troop.setEndTime(System.currentTimeMillis() / 1000
                    + DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                    ".trainingTimeCoefficient") -
                    ((long) barracksLevel * DefaultValueReaderUtil.getInt(
                            "buildings.barracks.troopTimeTrainingDecreasePerLevel")));

        } else {
            Long lastInQueue = troopList.get().get(0).getEndOfTrainingTime();

            troop.setEndTime(lastInQueue + DefaultValueReaderUtil.getInt(
                    "troops." + troopCreateDTO.getType().toLowerCase() + ".trainingTimeCoefficient") -
                    ((long) barracksLevel * DefaultValueReaderUtil.getInt(
                            "buildings.barracks.troopTimeTrainingDecreasePerLevel")));
        }
    }

    @Override
    public List<TroopListDTO> getListOfTroops(Long kingdomId) {
        List<TroopListDTO> returnList = new ArrayList<>();

        List<Troop> troopList = troopRepository
                .findTroopsInKingdom((System.currentTimeMillis() / 1000), kingdomId);

        returnList.add(new TroopListDTO("Phalanx", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("phalanx"))
                .count()));

        returnList.add(new TroopListDTO("Footman", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("footman"))
                .count()));

        returnList.add(new TroopListDTO("Knight", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("knight"))
                .count()));

        returnList.add(new TroopListDTO("Scout", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("scout"))
                .count()));

        returnList.add(new TroopListDTO("Diplomat", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("diplomat"))
                .count()));

        returnList.add(new TroopListDTO("Trebuchet", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("trebuchet"))
                .count()));

        returnList.add(new TroopListDTO("Settler", troopList
                .stream()
                .filter(t -> t.getTroopType() == TroopType.fromName("settler"))
                .count()));

        return returnList;
    }

    @Override
    public List<TroopQueueDTO> getTrainingList(Long kingdomId) {
        List<TroopQueueDTO> returnList = new ArrayList<>();
        List<Troop> troopList = troopRepository
                .findAllByKingdom_Id(kingdomId);

        if (troopList.isEmpty() || troopList.get(0) == null) {
            return null;
        } else {
            troopList.removeIf(troop -> troop.getEndOfTrainingTime() <= (System.currentTimeMillis() / 1000));
        }
        for (Troop troop : troopList) {
            returnList.add(new TroopQueueDTO(troop.getId(), troop.getTroopType().getName(),
                    troop.getEndOfTrainingTime()));
        }
        return returnList;
    }

    @Override
    public void deleteAllInQueue(Long kingdomId)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        List<Troop> troopList = troopRepository
                .findAllByKingdom_Id(kingdomId);

        if (troopList.size() != 0) {

            List<Troop> troopListNoCurrentlyTraining = removeTroopsNotInTraining(troopList);

            Kingdom kingdom = kingdomService.findKingdomById(kingdomId);

            for (Troop troop : troopListNoCurrentlyTraining) {
                if (troop.getEndOfTrainingTime() > (System.currentTimeMillis() / 1000)) {

                    //          This will increase the gold amount of selected kingdom per unit deleted
                    kingdom.setGoldAmount((kingdom.getGoldAmount() + (long) DefaultValueReaderUtil.getInt(
                            "troops." + troop.getTroopType().getName().toLowerCase() + ".price")));
                    kingdomService.saveKingdom(kingdom);

                    troopRepository.delete(troop);
                }
            }
        }
    }

    @Override
    public void deleteByType(Long kingdomId, DeleteTypeDTO deleteTypeDTO)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        List<Troop> troopList = troopRepository
                .findAllByKingdom_Id(kingdomId);

        if (troopList.size() != 0) {

            List<Troop> troopListNoCurrentlyTraining = removeTroopsNotInTraining(troopList);

            Kingdom kingdom = kingdomService.findKingdomById(kingdomId);

            for (Troop troop : troopListNoCurrentlyTraining) {
                if (troop.getEndOfTrainingTime() > (System.currentTimeMillis() / 1000)
                        && Objects.equals(troop.getTroopType().getName(), deleteTypeDTO.getType())) {

                    //          This will increase the gold amount of selected kingdom per unit deleted
                    kingdom.setGoldAmount((kingdom.getGoldAmount() + (long) DefaultValueReaderUtil.getInt(
                            "troops." + troop.getTroopType().getName().toLowerCase() + ".price")));
                    kingdomService.saveKingdom(kingdom);

                    troopRepository.delete(troop);
                }
            }
        }

    }

    @Override
    public void deleteAmount(Long kingdomId, int amount)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        List<Troop> troopList = troopRepository
                .findAllByKingdom_Id(kingdomId);

            List<Troop> troopListNoCurrentlyTraining = removeTroopsNotInTraining(troopList);

        if (troopListNoCurrentlyTraining.size() != 0 && amount <= (troopListNoCurrentlyTraining.size() - 1)) {

            Kingdom kingdom = kingdomService.findKingdomById(kingdomId);

            for (int i = 0; i < amount; i++) {
                if (troopListNoCurrentlyTraining.get(i).getEndOfTrainingTime() > (System.currentTimeMillis() / 1000)) {

                    //          This will increase the gold amount of selected kingdom per unit deleted
                    kingdom.setGoldAmount((kingdom.getGoldAmount() + (long) DefaultValueReaderUtil.getInt(
                            "troops." + troopListNoCurrentlyTraining.get(i).getTroopType().getName().toLowerCase()
                                    + ".price")));

                    kingdomService.saveKingdom(kingdom);

                    troopRepository.delete(troopListNoCurrentlyTraining.get(i));
                }
            }
        } else
            deleteAllInQueue(kingdomId);
    }

    // Delete troops not in training, sort troopList and keeps troop currently in training
    @Override
    public List<Troop> removeTroopsNotInTraining(List<Troop> troopList) {
        troopList.removeIf(troop -> troop.getEndOfTrainingTime() <= (System.currentTimeMillis() / 1000));
        List<Troop> returnList = listSorter(troopList);
        if (returnList.size() > 0) {
            returnList.remove(0);
        }
        return returnList;
    }

    // Sort troops by endOfTrainingTime
    @Override
    public List<Troop> listSorter(List<Troop> troopList) {
        Comparator<Troop> comparator =
                Comparator.comparing(Troop::getEndOfTrainingTime);

        troopList.sort(comparator);
        return troopList;
    }
}