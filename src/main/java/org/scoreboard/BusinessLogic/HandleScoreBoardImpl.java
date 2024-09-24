package org.scoreboard.BusinessLogic;

import org.scoreboard.Model.DTO.FootballMatch;
import org.scoreboard.Model.DTO.Team;
import org.scoreboard.Model.Error.ScoreboardException;

import java.util.concurrent.ConcurrentHashMap;

public class HandleScoreBoardImpl implements HandleScoreBoard {

    private final ConcurrentHashMap<String, FootballMatch> matches = new ConcurrentHashMap<>();


    private boolean isTeamAlreadyPlaying(Team homeTeam, Team awayTeam) {
        for (FootballMatch match : matches.values()) {
            if (match.getMatchKey().contains(homeTeam.getName()) || match.getMatchKey().contains(awayTeam.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void startMatch(FootballMatch newMatch) {
        String matchName = newMatch.getMatchKey();

        if (matches.containsKey(matchName)) {
            throw new ScoreboardException("Match already exists");
        }

        if (isTeamAlreadyPlaying(newMatch.getHomeTeam(), newMatch.getAwayTeam())) {
            throw new ScoreboardException("Team already playing in another match");
        }
        matches.put(matchName, newMatch);
    }

    @Override
    public ConcurrentHashMap<String, FootballMatch> getMatches() {
        return matches;
    }


}
