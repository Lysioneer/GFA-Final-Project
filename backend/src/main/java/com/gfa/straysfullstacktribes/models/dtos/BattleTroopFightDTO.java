package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Troop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleTroopFightDTO {

    private Troop troop;
    private Integer hp;
    private Integer attack;
    private Integer defence;
    private boolean alive;

    public BattleTroopFightDTO(Troop troop, Integer hp, Integer attack, Integer defence) {
        this.troop = troop;
        this.hp = hp;
        this.attack = attack;
        this.defence = defence;
        this.alive = true;
    }
}
