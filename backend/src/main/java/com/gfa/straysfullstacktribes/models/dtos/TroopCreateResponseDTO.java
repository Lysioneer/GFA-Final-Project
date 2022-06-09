package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TroopCreateResponseDTO {

    private Long id;
    private Long kingdomId;
    private Integer level;
    private Integer hp;
    private Integer attack;
    private Integer defence;
    private Integer speed;
    private Long startedAt;
    private Long finishedAt;
}
