package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KingdomUnitLevelDTO {
    String type;
    Integer level;
    String ability;
    int attack;
    int defence;
}
