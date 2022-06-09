package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.repositories.BuildingRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private BuildingRepository buildingRepository;
    private KingdomRepository kingdomRepository;

    @Override
    public void constructNew(Building building) {
        buildingRepository.save(building);
    }

    @Override
    public Optional<Building> getBuildingByBuildingIdAndKingdomId(Long buildingId, Long kingdomId) {
        return buildingRepository.findBuildingByIdAndKingdom_Id(buildingId, kingdomId);
    }

    @Override
    public Optional<ErrorMessage> checkPlannedConstruction(AddBuildingRequestDTO addBuildingRequestDTO, Long kingdomId) {
        try {
            int maxBuildings = DefaultValueReaderUtil.getInt("buildings.general.maximumCount");
            if (addBuildingRequestDTO.getBuildingType() == null || addBuildingRequestDTO.getPosition() == null) {
                return Optional.of(new ErrorMessage("Missing values!"));
            } else if (kingdomRepository.findById(kingdomId).isEmpty()) {
                return Optional.of(new ErrorMessage("There is no such kingdom!"));
            } else if (addBuildingRequestDTO.getPosition() < 1 || addBuildingRequestDTO.getPosition() > maxBuildings) {
                return Optional.of(new ErrorMessage("The position is out of range!"));
            } else if (Arrays.stream(BuildingType.values()).noneMatch((t) -> t.equals(BuildingType.fromName(addBuildingRequestDTO.getBuildingType())))) {
                return Optional.of(new ErrorMessage("There is no such building type!"));
            } else if (addBuildingRequestDTO.getBuildingType().equals(BuildingType.TOWN_HALL.getName())) {
                return Optional.of(new ErrorMessage("Your kingdom already has a town hall!"));
            } else if (buildingRepository.findAllByKingdom_Id(kingdomId).size() > maxBuildings) {
                return Optional.of(new ErrorMessage("There is no place for another building in your kingdom!"));
            } else if (buildingRepository.findUnderwayConstruction(kingdomId).isPresent()) {
                return Optional.of(new ErrorMessage("Construction is already underway in your kingdom!"));
            } else if (buildingRepository.findByKingdom_IdAndBuildingPosition(kingdomId, addBuildingRequestDTO.getPosition()).isPresent()) {
                return Optional.of(new ErrorMessage("Position " + addBuildingRequestDTO.getPosition() + " is already taken by another building!"));
            } else if (kingdomRepository.findById(kingdomId).isPresent()) {
                if (kingdomRepository.findById(kingdomId).get().getGoldAmount()
                        < DefaultValueReaderUtil.getInt("buildings." + addBuildingRequestDTO.getBuildingType() + ".price")) {
                    return Optional.of(new ErrorMessage("You don't have enough coins!"));
                }
            }
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<ErrorMessage> levelUpBuildingCheck(Long buildingId, Long kingdomId) {
        Optional<Building> optionalBuilding = buildingRepository.findBuildingByIdAndKingdom_Id(buildingId, kingdomId);
        Building kingdomTownHall = buildingRepository.findBuildingByKingdom_IdAndType(kingdomId, BuildingType.TOWN_HALL);
        try {
            if (optionalBuilding.isEmpty()) {
                return Optional.of(new ErrorMessage("There is no such building in your kingdom!"));
            } else if (optionalBuilding.get().getLevel()
                    >= DefaultValueReaderUtil.getInt("buildings.general.maximumLevel")) {
                return Optional.of(new ErrorMessage("This building already has maximal level!"));
            } else if (!optionalBuilding.get().getType().equals(BuildingType.TOWN_HALL) && optionalBuilding.get().getLevel() >= kingdomTownHall.getLevel()) {
                return Optional.of(new ErrorMessage("You have to upgrade town hall first!"));
            } else if (buildingRepository.findUnderwayConstruction(kingdomId).isPresent()) {
                return Optional.of(new ErrorMessage("Construction is already underway in your kingdom!"));
            } else if (kingdomRepository.findById(kingdomId).isPresent()) {
                if (kingdomRepository.findById(kingdomId).get().getGoldAmount()
                        < DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".price")) {
                    return Optional.of(new ErrorMessage("You don't have enough coins!"));
                }
            }
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<UpgradeBuildingResponseDTO> levelUpBuilding(Long buildingId, Long kingdomId) {
        Optional<Building> optionalBuilding = buildingRepository.findBuildingByIdAndKingdom_Id(buildingId, kingdomId);
        Kingdom owningKingdom = kingdomRepository.findKingdomById(kingdomId);
        try {
            if (optionalBuilding.isPresent() && owningKingdom != null) {
                optionalBuilding.get().setConstructTime(System.currentTimeMillis() / 1000
                        + DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".defaultBuildingTime"));
                owningKingdom.setGoldAmount(owningKingdom.getGoldAmount()
                        - DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".price"));
                kingdomRepository.save(owningKingdom);
                buildingRepository.save(optionalBuilding.get());
                return Optional.of(UpgradeBuildingResponseDTO
                        .builder()
                        .action("upgrade")
                        .instant(false)
                        .buildingId(buildingId)
                        .kingdomId(kingdomId)
                        .type(optionalBuilding.get().getType().getName())
                        .level(optionalBuilding.get().getLevel())
                        .position(optionalBuilding.get().getBuildingPosition())
                        .constructTime(optionalBuilding.get().getConstructTime())
                        .destroyTime(null)
                        .build());
            }
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public Optional<ErrorMessage> tearDownBuildingCheck(Long buildingId, Long kingdomId, Boolean instant) {
        Optional<Building> optionalBuilding = buildingRepository.findBuildingByIdAndKingdom_Id(buildingId, kingdomId);
        try {
            if (optionalBuilding.isEmpty()) {
                return Optional.of(new ErrorMessage("There is no such building in your kingdom!"));
            } else if (buildingRepository.findUnderwayConstruction(kingdomId).isPresent()) {
                return Optional.of(new ErrorMessage("Construction is already underway in your kingdom!"));
            }
            if (!instant) {
                if ((optionalBuilding.get().getBuildingPosition().equals(1)
                        || optionalBuilding.get().getBuildingPosition().equals(2)
                        || optionalBuilding.get().getBuildingPosition().equals(3))
                        && optionalBuilding.get().getLevel() <= 1) {
                    return Optional.of(new ErrorMessage("This building can not be entirely destroyed!"));
                }
            } else {
                if (optionalBuilding.get().getBuildingPosition().equals(1)
                        || optionalBuilding.get().getBuildingPosition().equals(2)
                        || optionalBuilding.get().getBuildingPosition().equals(3)) {
                    return Optional.of(new ErrorMessage("This building can not be instantly torn down!"));
                } else if (kingdomRepository.findById(kingdomId).isPresent()) {
                    if (kingdomRepository.findById(kingdomId).get().getGoldAmount()
                            < DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".price")) {
                        return Optional.of(new ErrorMessage("You don't have enough coins!"));
                    }
                }
            }
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<UpgradeBuildingResponseDTO> tearDownBuilding(Long buildingId, Long kingdomId, Boolean instant) {
        Optional<Building> optionalBuilding = buildingRepository.findBuildingByIdAndKingdom_Id(buildingId, kingdomId);
        Kingdom owningKingdom = kingdomRepository.findKingdomById(kingdomId);
        try {
            if (optionalBuilding.isPresent() && owningKingdom != null) {
                if (!instant) {
                    optionalBuilding.get().setDestroyTime(System.currentTimeMillis() / 1000
                            + DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".defaultDestroyTime"));
                    buildingRepository.save(optionalBuilding.get());
                    return Optional.of(UpgradeBuildingResponseDTO
                                    .builder()
                                    .action("tear-down")
                                    .instant(false)
                                    .buildingId(buildingId)
                                    .kingdomId(kingdomId)
                                    .type(optionalBuilding.get().getType().getName())
                                    .level(optionalBuilding.get().getLevel())
                                    .position(optionalBuilding.get().getBuildingPosition())
                                    .constructTime(null)
                                    .destroyTime(optionalBuilding.get().getDestroyTime())
                                    .build());
                } else {
                    owningKingdom.setGoldAmount(owningKingdom.getGoldAmount()
                            - DefaultValueReaderUtil.getInt("buildings." + optionalBuilding.get().getType().getName() + ".price"));
                    kingdomRepository.save(owningKingdom);
                    buildingRepository.delete(optionalBuilding.get());
                    return Optional.of(UpgradeBuildingResponseDTO
                            .builder()
                            .action("tear-down")
                            .instant(true)
                            .buildingId(buildingId)
                            .kingdomId(kingdomId)
                            .type(optionalBuilding.get().getType().getName())
                            .level(0)
                            .position(optionalBuilding.get().getBuildingPosition())
                            .constructTime(null)
                            .destroyTime(null)
                            .build());
                }
            }
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Boolean existsKingdomAcademy(Long kingdomId) {
        return buildingRepository.existsByKingdomIdAndType(kingdomId, BuildingType.ACADEMY);
    }

    @Override
    public Boolean existsKingdomBarracks(Long kingdomId) {
        return buildingRepository.existsByKingdomIdAndType(kingdomId, BuildingType.BARRACKS);
    }

    @Override
    public Building findKingdomBarracks(Long kingdomId) {
        return buildingRepository.findByKingdomIdAndType(kingdomId, BuildingType.BARRACKS);
    }

    @Override
    public Building findKingdomAcademy(Long kingdomId) {
        return buildingRepository.findByKingdomIdAndType(kingdomId, BuildingType.ACADEMY);
    }

    @Override
    public BuildingTypesDTO getBuildingTypes(Long kingdomId) {
        BuildingType[] buildingTypeList = BuildingType.values();
        List<String> buildingTypeListStrings =
                Arrays.stream(buildingTypeList)
                        .map(BuildingType::getName)
                        .collect(Collectors.toList());
        List<Building> buildingList = buildingRepository.findAllByKingdom_Id(kingdomId);
        List<Building> constructionPlacesList = new ArrayList<>();
        List<Integer> freePlaces = new ArrayList<>();
        try {
            for (int place = 0; place < DefaultValueReaderUtil.getInt("buildings.general.maximumCount"); place++) {
                constructionPlacesList.add(null);
            }
            for (Building building : buildingList) {
                constructionPlacesList.set(building.getBuildingPosition() - 1, building);
            }
            for (int place = 0; place < constructionPlacesList.size(); place++) {
                if (constructionPlacesList.get(place) == null) {
                    freePlaces.add(place + 1);
                }
            }
        }
        catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return BuildingTypesDTO
                .builder()
                .buildingTypes(buildingTypeListStrings.stream().map(TypesDTO::new).collect(Collectors.toList()))
                .freePositions(freePlaces.stream().map(PositionDTO::new).collect(Collectors.toList()))
                .build();
    }
}
