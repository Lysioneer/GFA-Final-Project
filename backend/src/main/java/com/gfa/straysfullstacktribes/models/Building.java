package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Kingdom kingdom;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BuildingType type;

    @NotNull
    @Column(nullable = false)
    private Integer level;

    @NotNull
    @Column(nullable = false)
    private Integer buildingPosition;

    private Long constructTime;

    private Long destroyTime;

    @ManyToOne
    private Battle battle;
    
    public Building(Kingdom kingdom, BuildingType type, Integer buildingPosition) {
        this.kingdom = kingdom;
        this.type = type;
        this.level = 0;
        this.buildingPosition = buildingPosition;
        try {
            this.constructTime =  System.currentTimeMillis()/1000
                    + DefaultValueReaderUtil.getInt("buildings." + type.getName() + ".defaultBuildingTime");
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
    }

    public Building(Kingdom kingdom, BuildingType type, Integer buildingPosition, Integer level) {
        this.kingdom = kingdom;
        this.type = type;
        this.level = level;
        this.buildingPosition = buildingPosition;
    }

    public void setDestroyTime() throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        this.destroyTime = System.currentTimeMillis()/1000
                + Math.round(DefaultValueReaderUtil.getInt("buildings." + type.getName() + ".defaultBuildingTime") * (1 + 0.1 * (level)));
    }
}