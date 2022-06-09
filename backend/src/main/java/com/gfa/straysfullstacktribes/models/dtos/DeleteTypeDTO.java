package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTypeDTO {
    private int amount = 0;
    private String type;

    public DeleteTypeDTO(int amount) {
        this.amount = amount;
    }

    public DeleteTypeDTO(String type) {
        this.type = type;
    }
}
