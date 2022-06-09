package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.repositories.BattleRepository;
import com.gfa.straysfullstacktribes.services.AppUserService;
import com.gfa.straysfullstacktribes.services.BattleService;
import com.gfa.straysfullstacktribes.services.KingdomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BattleController {

    private final BattleService battleService;
    private final KingdomService kingdomService;
    private final AppUserService appUserService;
    private final BattleRepository battleRepository;

    @GetMapping("/battles/{id}")
    public ResponseEntity<?> allBattles(@PathVariable(name = "id") Long kingdomId,
                                        @RequestHeader(value = "Authorization") String token) {

        if (!kingdomService.appUserOwnsKingdom(kingdomId, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }

        return battleService.listOfBattles(kingdomId);
    }
}
