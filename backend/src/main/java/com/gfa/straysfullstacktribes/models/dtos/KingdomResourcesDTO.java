package com.gfa.straysfullstacktribes.models.dtos;

import lombok.Data;

import java.util.List;

@Data
public class KingdomResourcesDTO {
    private GoldDTO gold;
    private FoodDTO food;

    public KingdomResourcesDTO(FoodDTO food, GoldDTO gold) {
        this.food = food;
        this.gold = gold;
    }
}
