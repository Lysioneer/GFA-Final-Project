package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.UserScore;

import java.util.List;

public interface LeaderboardService {
    List<UserScore> leaderboard();
}
