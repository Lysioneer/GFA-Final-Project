package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.enums.TroopType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TroopCreateDTO {
    private String type;
    private Integer quantity;

    public TroopCreateDTO(String type) {
        this.type = type;
    }

    public TroopCreateDTO(Integer quantity) {
        this.quantity = quantity;
    }

    public TroopType getCustomType(String name) {
        return TroopType.valueOf(name);
    }
}
