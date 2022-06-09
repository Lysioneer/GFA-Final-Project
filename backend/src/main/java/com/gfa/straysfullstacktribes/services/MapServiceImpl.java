package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomInfoDTO;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MapServiceImpl implements MapService {

    private final KingdomRepository kingdomRepository;

    @Override
    public Optional<Kingdom> getCoordinatesInfo2(Long x, Long y) {
        return kingdomRepository.findByCorXAndCorY(x, y);
    }

    @Override
    public KingdomInfoDTO getKingdomByCoordinates(Long x, Long y) {
        Optional<Kingdom> kingdom = kingdomRepository.findByCorXAndCorY(x, y);
        KingdomInfoDTO kingdomInfo = new KingdomInfoDTO();
        if (kingdom.isPresent()) {
            kingdomInfo.setUsername(kingdom.get().getAppUser().getUsername());
            kingdomInfo.setKingdomName(kingdom.get().getName());
            return kingdomInfo;
        }
        return kingdomInfo;
    }
}