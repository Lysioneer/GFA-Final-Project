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
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Troop {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TroopType troopType;

    private Long endOfTrainingTime;
    private Long destroyTime;

    @NotNull
    @ManyToOne
    private Kingdom kingdom;
    @ManyToOne
    private Battle battle;

    public Troop(TroopType troopType, Kingdom kingdom)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        this.troopType = troopType;
        this.endOfTrainingTime = System.currentTimeMillis() / 1000
                + DefaultValueReaderUtil.getInt("troops." + troopType.toString().toLowerCase() +
                ".trainingTimeCoefficient");
        this.destroyTime = null;
        this.kingdom = kingdom;
    }

    public void setEndTime(Long timeInSeconds) {
        this.endOfTrainingTime = timeInSeconds;
    }
}