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
public class KingdomDetailsDTO {
    private KingdomInfoForKingdomDetailsDTO kingdom;
    private LocationDTO location;
    private KingdomResourcesDTO resources;
    private List<BuildingDTO> buildings;
    private List<TroopDTO> troops;
}
