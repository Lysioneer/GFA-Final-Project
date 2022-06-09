package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpgradeBuildingResponseDTO {

    private String action;
    private Boolean instant;
    private Long buildingId;
    private Long kingdomId;
    private String type;
    private Integer level;
    private Integer position;
    private Long constructTime;
    private Long destroyTime;
}
