package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.enums.BattleType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BattleRequestDTO {

    private Long defenderKingdomId;
    private BattleType battleType;
    private List<BattleTroopDTO> troops;

    public BattleRequestDTO() {
        this.troops = new ArrayList<>();
    }

    public BattleRequestDTO(BattleType battleType){
        this.battleType = battleType;
        this.troops = new ArrayList<>();
    }

    public BattleRequestDTO(Long defenderKingdomId) {
        this.defenderKingdomId = defenderKingdomId;
    }

    public void addTroops(BattleTroopDTO battleTroopDTO) {
        troops.add(battleTroopDTO);
    }
}