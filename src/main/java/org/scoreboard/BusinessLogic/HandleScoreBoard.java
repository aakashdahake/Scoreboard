package org.scoreboard.BusinessLogic;

import org.scoreboard.Model.DTO.FootballMatch;

import java.util.concurrent.ConcurrentHashMap;

public interface HandleScoreBoard {

    void startMatch(FootballMatch newMatch);

    ConcurrentHashMap<String, FootballMatch> getMatches();

}
