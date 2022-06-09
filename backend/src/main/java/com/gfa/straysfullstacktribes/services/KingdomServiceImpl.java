package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.repositories.BuildingRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KingdomServiceImpl implements KingdomService {

    private final KingdomRepository kingdomRepository;
    private final BuildingRepository buildingRepository;
    private final TroopRepository troopRepository;

    @Override
    public Kingdom findKingdomById(Long id) {
        return kingdomRepository.findKingdomById(id);
    }

    @Override
    public Boolean kingdomExistsById(Long id) {
        return kingdomRepository.existsKingdomById(id);
    }

    @Override
    public Boolean kingdomExistsByKingdomName(String kingdomName) {
        return kingdomRepository.existsKingdomByName(kingdomName);
    }

    @Override
    public void renameKingdom(Long id, String kingdomName) {
        Kingdom kingdom = findKingdomById(id);
        kingdom.setName(kingdomName);
        kingdomRepository.save(kingdom);
    }

    @Override
    public void saveKingdom(Kingdom kingdom) {
        kingdomRepository.save(kingdom);
    }

    public Integer kingdomRepoSize() {
        return kingdomRepository.findAll().size();
    }

    @Override
    public Boolean appUserOwnsKingdom(Long kingdomId, Long appUserId) {
        return kingdomRepository.findKingdomByIdAndAppUserId(kingdomId, appUserId).isPresent();
    }

    public Optional<Kingdom> getKingdomById(Long kingdomId) {
        return kingdomRepository.findById(kingdomId);
    }

    @Override
    public KingdomDetailsDTO getKingdomDetailsDTOById(Long kingdomId) {
        Kingdom kingdom = findKingdomById(kingdomId);
        return convertKingdomToKingdomDetailsDTO(kingdom);
    }

    @Override
    public KingdomDetailsDTO convertKingdomToKingdomDetailsDTO(Kingdom kingdom) {
        return KingdomDetailsDTO
                .builder()
                .kingdom(new KingdomInfoForKingdomDetailsDTO(kingdom))
                .location(new LocationDTO(kingdom))
                .resources(new KingdomResourcesDTO(new FoodDTO(kingdom), new GoldDTO(kingdom)))
                .buildings(buildingRepository
                        .findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .map(BuildingDTO::new)
                        .collect(Collectors.toList()))
                .troops(troopRepository
                        .findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .map(TroopDTO::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public KingdomBuildingsDTO getKingdomBuildingsDTOById(Long kingdomId) {
        Kingdom kingdom = findKingdomById(kingdomId);
        return convertKingdomToKingdomBuildingsDTO(kingdom);
    }

    @Override
    public KingdomBuildingsDTO convertKingdomToKingdomBuildingsDTO(Kingdom kingdom) {
        return KingdomBuildingsDTO
                .builder()
                .kingdomBuildings(buildingRepository
                        .findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .sorted(Comparator.comparing(Building::getBuildingPosition))
                        .map(BuildingDTO::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public KingdomTroopsDTO getKingdomTroopsDTOById(Long kingdomId) {
        Kingdom kingdom = findKingdomById(kingdomId);
        return convertKingdomToKingdomTroopsDTO(kingdom);
    }

    @Override
    public KingdomTroopsDTO convertKingdomToKingdomTroopsDTO(Kingdom kingdom) {
        return KingdomTroopsDTO
                .builder()
                .kingdomTroops(troopRepository.findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .map(TroopDTO::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public KingdomBuildingsDTO getConstructionPlacesList(Long kingdomId) {
        Kingdom kingdom = findKingdomById(kingdomId);
        List<BuildingDTO> buildingDTOList =
                buildingRepository
                        .findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .map(BuildingDTO::new)
                        .collect(Collectors.toList());
        List<BuildingDTO> constructionPlacesList = new ArrayList<>();

        try {
            for (int place = 0; place < DefaultValueReaderUtil.getInt("buildings.general.maximumCount"); place++) {
                constructionPlacesList.add(null);
            }
            for (BuildingDTO building : buildingDTOList) {
                constructionPlacesList.set(building.getPosition() - 1, building);
            }
        }
        catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        return KingdomBuildingsDTO
                .builder()
                .kingdomBuildings(constructionPlacesList)
                .build();
    }
}