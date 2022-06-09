package com.gfa.straysfullstacktribes.models.enums;

import lombok.Getter;

@Getter
public enum TroopType {
    PHALANX("phalanx"),
    FOOTMAN("footman"),
    SCOUT("scout"),
    KNIGHT("knight"),
    TREBUCHET("trebuchet"),
    DIPLOMAT("diplomat"),
    SETTLER("settler");

    private String name;

    TroopType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TroopType fromName(String name){
        for (TroopType type : TroopType.values()){
            if (type.name.equals(name)){
                return type;
            }
        }
        return null;
    }
}
