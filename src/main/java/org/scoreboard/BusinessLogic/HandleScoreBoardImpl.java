package org.scoreboard.BusinessLogic;

import org.scoreboard.Model.DTO.FootballMatch;

import java.util.concurrent.ConcurrentHashMap;

public class HandleScoreBoardImpl implements HandleScoreBoard {

    private final ConcurrentHashMap<String, FootballMatch> matches = new ConcurrentHashMap<>();


    @Override
    public void startMatch(FootballMatch newMatch) {

    }

    @Override
    public ConcurrentHashMap<String, FootballMatch> getMatches() {
        return matches;
    }


}
