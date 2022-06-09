package com.gfa.straysfullstacktribes.models.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UpgradeBuildingRequestDTO {
    private String action;
    private Boolean instant;

    public UpgradeBuildingRequestDTO() {
        this.instant = false;
    }

    public UpgradeBuildingRequestDTO(String action) {
        this.action = action;
        this.instant = false;
    }
}
