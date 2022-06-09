package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildingDTO {
    private Long buildingId;
    private String type;
    private Integer level;
    private Integer position;
    private Long constructTime;
    private Long destroyTime;

    public BuildingDTO(Building building) {
        this.buildingId = building.getId();
        this.type = building.getType().getName();
        this.level = building.getLevel();
        this.position = building.getBuildingPosition();
        this.constructTime = building.getConstructTime();
        this.destroyTime = building.getDestroyTime();
    }
}
