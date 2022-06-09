package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private Long coordinateX;
    private Long coordinateY;

    public LocationDTO(Kingdom kingdom) {
        this.coordinateX = kingdom.getCorX();
        this.coordinateY = kingdom.getCorY();
    }
}
