package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KingdomBuildingsDTO {
    List<BuildingDTO> kingdomBuildings;
}