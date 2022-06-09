package com.gfa.straysfullstacktribes.models.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KingdomRegistrationRequestModelDTO {

    private String username;
    private String password;
    private String kingdomName;
    private Long coordinateX;
    private Long coordinateY;

}