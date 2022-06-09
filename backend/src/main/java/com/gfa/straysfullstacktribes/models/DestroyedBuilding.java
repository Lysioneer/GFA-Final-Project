package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.models.enums.BuildingType;
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
public class DestroyedBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Battle battle;

    @OneToOne
    private Kingdom kingdom;

    @Enumerated(EnumType.STRING)
    private BuildingType buildingType;

    private Long destroyTime;

    public DestroyedBuilding(Battle battle, Kingdom kingdom, BuildingType buildingType, Long destroyTime) {
        this.battle = battle;
        this.kingdom = kingdom;
        this.buildingType = buildingType;
        this.destroyTime = destroyTime;
    }
}
