package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UnitLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    private Kingdom kingdom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TroopType troopType;
    @NotNull
    @Column(nullable = false)
    private Integer upgradeLevel;

    private Long upgradeFinishedAt;

    public UnitLevel(Kingdom kingdom, TroopType troopType) {
        this.kingdom = kingdom;
        this.troopType = troopType;
        this.upgradeLevel = 0;
    }

    public void upgradeUnitLevel() {
        if(upgradeLevel < 20) {
            this.upgradeLevel++;
        }
    }

    public void setFinishedAt(Long timeInSeconds) {
        this.upgradeFinishedAt = timeInSeconds;
    }
}
