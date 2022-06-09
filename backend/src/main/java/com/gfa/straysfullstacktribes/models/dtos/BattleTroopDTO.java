package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.enums.TroopType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleTroopDTO {

    private TroopType type;
    private Long quantity;

}