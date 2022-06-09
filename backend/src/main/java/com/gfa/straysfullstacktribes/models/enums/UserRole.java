package com.gfa.straysfullstacktribes.models.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    PLAYER("player");

    private String name;

    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserRole fromName(String name){
        for (UserRole type : UserRole.values()){
            if (type.name.equals(name)){
                return type;
            }
        }
        return null;
    }
}