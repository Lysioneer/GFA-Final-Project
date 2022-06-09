package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.models.dtos.AcademyDTO;
import com.gfa.straysfullstacktribes.models.dtos.AddBuildingRequestDTO;
import com.gfa.straysfullstacktribes.models.dtos.BuildingTypesDTO;
import com.gfa.straysfullstacktribes.models.dtos.UpgradeBuildingResponseDTO;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;

import java.util.Optional;

public interface BuildingService {

    void constructNew(Building building);

    Optional<Building> getBuildingByBuildingIdAndKingdomId(Long buildingId, Long kingdomId);

    Optional<ErrorMessage> checkPlannedConstruction(AddBuildingRequestDTO addBuildingRequestDTO, Long kingdomId);

    Optional<ErrorMessage> levelUpBuildingCheck(Long buildingId, Long kingdomId);

    Optional<UpgradeBuildingResponseDTO> levelUpBuilding(Long buildingId, Long kingdomId);

    Optional<ErrorMessage> tearDownBuildingCheck(Long buildingId, Long kingdomId, Boolean instant);

    Optional<UpgradeBuildingResponseDTO> tearDownBuilding(Long buildingId, Long kingdomId, Boolean instant);

    Boolean existsKingdomAcademy(Long kingdomId);

    Boolean existsKingdomBarracks(Long kingdomId);

    Building findKingdomBarracks(Long kingdomId);

    Building findKingdomAcademy(Long kingdomId);

    BuildingTypesDTO getBuildingTypes(Long kingdomId);
}
