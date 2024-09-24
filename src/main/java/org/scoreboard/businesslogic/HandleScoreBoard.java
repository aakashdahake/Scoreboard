package org.scoreboard.businesslogic;

import org.scoreboard.models.dto.FootballMatch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface HandleScoreBoard {

    void startMatch(FootballMatch newMatch);

    ConcurrentHashMap<String, FootballMatch> getMatches();

    void updateScore(FootballMatch currentMatch, int homeTeamScore, int awayTeamScore);

    void endMatch(FootballMatch currentMatch);

    List<FootballMatch> getOrderedSummary();
}
