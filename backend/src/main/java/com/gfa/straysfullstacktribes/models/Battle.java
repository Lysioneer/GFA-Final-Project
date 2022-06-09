package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.models.enums.BattleType;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BattleType battleType;

    @NotNull
    @Column(nullable = false)
    private Long armyLeftKingdomTime;

    @NotNull
    @Column(nullable = false)
    private Long battleTime;

    @NotNull
    @Column(nullable = false)
    private Long returnTime;

    @ManyToOne
    private Kingdom attackerKingdom;

    @ManyToOne
    private Kingdom defenderKingdom;

    private Boolean attackerWins;

    private Long goldStolen;

    private Long foodStolen;

    public Battle() {
        this.armyLeftKingdomTime = System.currentTimeMillis() / 1000;
        this.battleTime = System.currentTimeMillis() / 1000;
        this.returnTime = System.currentTimeMillis() / 1000;
        this.attackerWins = null;
        this.goldStolen = 0L;
        this.foodStolen= 0L;
    }

    public Battle(Kingdom attackerKingdom, Kingdom defenderKingdom) {
        this.attackerKingdom = attackerKingdom;
        this.defenderKingdom = defenderKingdom;
        this.armyLeftKingdomTime = System.currentTimeMillis() / 1000;
        this.battleTime = System.currentTimeMillis() / 1000;
        this.returnTime = System.currentTimeMillis() / 1000;
        this.attackerWins = null;
        this.goldStolen = 0L;
        this.foodStolen= 0L;
    }

}
