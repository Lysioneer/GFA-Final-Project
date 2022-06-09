package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.enums.BattleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleDTO {

    private Long id;
    private BattleType battleType;
    private Long armyLeftKingdomTime;
    private Long battleTime;
    private Long returnTime;
    private Boolean attackerWins;
    private Long goldStolen;
    private Long foodStolen;
    private Long attackerKingdomId;
    private Long defenderKingdomId;

}
