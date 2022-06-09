package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomInfoDTO;

import java.util.Optional;

public interface MapService {

    Optional<Kingdom> getCoordinatesInfo2(Long x, Long y);

    KingdomInfoDTO getKingdomByCoordinates(Long x, Long y);
}
