package com.gfa.straysfullstacktribes.models.enums;

import lombok.Getter;

@Getter
public enum BuildingType {
    TOWN_HALL("town_hall"),
    BARRACKS("barracks"),
    ACADEMY("academy"),
    FARM("farm"),
    GOLD_MINE("gold_mine"),
    WALLS("walls"),
    MARKETPLACE("marketplace"),
    HIDEOUT("hideout"),
    MERCENARIES_INN("mercenaries_inn");

    private String name;

    BuildingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BuildingType fromName(String name) {
        for (BuildingType type : BuildingType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}