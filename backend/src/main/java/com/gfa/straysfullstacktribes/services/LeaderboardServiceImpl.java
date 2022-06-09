package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.BuildingRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final AppUserRepository appUserRepository;
    private final KingdomRepository kingdomRepository;
    private final BuildingRepository buildingRepository;
    private final TroopRepository troopRepository;

    @Override
    public List<UserScore> leaderboard() {
        List<UserScore> leaderboard = new ArrayList<>();
        List<AppUser> users = appUserRepository.findAll();
        for (AppUser user : users) {
            leaderboard.add(scoreCount(user));
        }
        return leaderboard;
    }

    public UserScore scoreCount(AppUser user) {
        UserScore userScore = new UserScore();
        int score = 0;
        userScore.setUserName(user.getUsername());
        List<Kingdom> kingdoms = kingdomRepository.findByAppUser(user);
        userScore.setKingdomAmount(kingdoms.size());
        for (Kingdom kingdom : kingdoms) {
            score += kingdomScore(kingdom);
        }
        userScore.setScore(score);
        return userScore;
    }

    public int kingdomScore(Kingdom kingdom) {
        int kingdomScore = 0;
        List<Building> buildings = buildingRepository.findBuildingsInKingdom(kingdom.getId());
        for (Building building : buildings) {
            kingdomScore += building.getLevel() * 10;
        }
        kingdomScore += troopRepository
                .findTroopsInKingdom(System.currentTimeMillis() / 1000, kingdom.getId()).size();
        return kingdomScore;
    }
}
