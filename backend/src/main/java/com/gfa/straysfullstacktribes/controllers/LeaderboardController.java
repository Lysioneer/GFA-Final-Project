package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.services.LeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@AllArgsConstructor
@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public ResponseEntity leaderboard (){
        return ResponseEntity.status(HttpStatus.OK).body(leaderboardService.leaderboard());
    }
}
