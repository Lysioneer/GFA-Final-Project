package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcademyDTO {
    Long id;
    BuildingType type;
    Integer level;
    Integer position;
}
