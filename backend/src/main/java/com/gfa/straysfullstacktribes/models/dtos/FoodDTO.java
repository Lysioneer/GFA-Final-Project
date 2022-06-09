package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Kingdom;
import lombok.Data;

@Data
public class FoodDTO {
    private Long amount;
    private Integer foodProduction;
    private Long updatedAt;

    public FoodDTO(Kingdom kingdom) {
        this.amount = kingdom.getFoodAmount();
        this.foodProduction = kingdom.getFoodProduction();
        this.updatedAt = kingdom.getUpdatedAt();
    }
}
