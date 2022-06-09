package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomBuildingsDTO;
import com.gfa.straysfullstacktribes.models.dtos.KingdomDetailsDTO;
import com.gfa.straysfullstacktribes.models.dtos.KingdomTroopsDTO;

import java.util.List;
import java.util.Optional;

public interface KingdomService {

    void renameKingdom(Long id, String kingdomName);

    Kingdom findKingdomById(Long id);

    Boolean kingdomExistsById(Long id);

    Boolean kingdomExistsByKingdomName(String kingdomName);

    Integer kingdomRepoSize();

    Boolean appUserOwnsKingdom(Long kingdomId, Long appUserId);

    void saveKingdom(Kingdom kingdom);

    Optional<Kingdom> getKingdomById(Long kingdomId);

    KingdomDetailsDTO getKingdomDetailsDTOById(Long kingdomId);

    KingdomDetailsDTO convertKingdomToKingdomDetailsDTO(Kingdom kingdom);

    KingdomBuildingsDTO getKingdomBuildingsDTOById(Long kingdomId);

    KingdomBuildingsDTO convertKingdomToKingdomBuildingsDTO(Kingdom kingdom);

    KingdomTroopsDTO getKingdomTroopsDTOById(Long kingdomId);

    KingdomTroopsDTO convertKingdomToKingdomTroopsDTO(Kingdom kingdom);

    KingdomBuildingsDTO getConstructionPlacesList(Long kingdomId);

}