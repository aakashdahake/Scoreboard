package org.scoreboard.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FootballMatch {


    private Team homeTeam;
    private Team awayTeam;
    private final LocalDateTime startTime;
    private boolean isMatchActive;

    public FootballMatch(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startTime = LocalDateTime.now();
        this.isMatchActive = true;
    }

    public String getMatchKey() {
        return homeTeam.getName() + "-" + awayTeam.getName();
    }

    public int getTotalScore() {
        return homeTeam.getScore().get() + awayTeam.getScore().get();
    }
}
