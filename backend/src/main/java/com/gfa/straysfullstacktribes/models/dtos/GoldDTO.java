package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Kingdom;
import lombok.Data;

@Data
public class GoldDTO {
    private Long amount;
    private Integer goldProduction;
    private Long updatedAt;

    public GoldDTO(Kingdom kingdom) {
        this.amount = kingdom.getGoldAmount();
        this.goldProduction = kingdom.getGoldProduction();
        this.updatedAt = kingdom.getUpdatedAt();
    }
}
