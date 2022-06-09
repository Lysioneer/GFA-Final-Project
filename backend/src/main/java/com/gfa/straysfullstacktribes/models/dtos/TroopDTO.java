package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Troop;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TroopDTO {
    private Long troopId;
    private TroopType type;
    private Long endOfTrainingTime;

    public TroopDTO(Troop troop) {
        this.troopId = troop.getId();
        this.type = troop.getTroopType();
        this.endOfTrainingTime = troop.getEndOfTrainingTime();
    }
}
