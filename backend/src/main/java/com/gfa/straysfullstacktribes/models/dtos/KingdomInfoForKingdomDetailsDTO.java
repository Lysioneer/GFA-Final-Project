package com.gfa.straysfullstacktribes.models.dtos;

import com.gfa.straysfullstacktribes.models.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KingdomInfoForKingdomDetailsDTO {
    private Long kingdomId;
    private String kingdomName;
    private String ruler;

    public KingdomInfoForKingdomDetailsDTO(Kingdom kingdom) {
        this.kingdomId = kingdom.getId();
        this.kingdomName = kingdom.getName();
        this.ruler = kingdom.getAppUser().getUsername();
    }
}
