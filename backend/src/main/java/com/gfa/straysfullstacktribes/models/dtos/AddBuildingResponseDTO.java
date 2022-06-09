package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBuildingResponseDTO {
    private Integer position;
    private Long id;
    private Long kingdomId;
    private String type;
    private Integer level;
    private Long constructTime;
    private Long destroyTime;

}
