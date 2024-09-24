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

    public static void validateScores(int homeTeamScore, int awayTeamScore, FootballMatch currentMatch) throws ScoreboardException {
        if (currentMatch.isMatchActive() && currentMatch.getHomeTeam() != null && homeTeamScore < 0) {
            throw new ScoreboardException("Score can not be negative for team :: " + currentMatch.getHomeTeam().getName());
        }

        if (currentMatch.isMatchActive() && currentMatch.getAwayTeam() != null && awayTeamScore < 0) {
            throw new ScoreboardException("Score can not be negative for team :: " + currentMatch.getAwayTeam().getName());
        }
    }

    @Override
    public void updateScore(FootballMatch currentMatch, int homeTeamScore, int awayTeamScore) {

        if (currentMatch == null || !matches.containsKey(currentMatch.getMatchKey())) {
            throw new ScoreboardException("Match not found");
        }

        validateScores(homeTeamScore, awayTeamScore, currentMatch);

        FootballMatch match = matches.get(currentMatch.getMatchKey());
        synchronized (match) {
            match.getHomeTeam().setScore(homeTeamScore);
            match.getAwayTeam().setScore(awayTeamScore);
        }
    }

}
