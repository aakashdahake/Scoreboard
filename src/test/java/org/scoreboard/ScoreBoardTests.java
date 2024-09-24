package org.scoreboard;


import org.junit.Before;
import org.junit.Test;
import org.scoreboard.BusinessLogic.HandleScoreBoard;
import org.scoreboard.BusinessLogic.HandleScoreBoardImpl;
import org.scoreboard.Model.DTO.FootballMatch;
import org.scoreboard.Model.DTO.Team;
import org.scoreboard.Model.Error.ScoreboardException;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ScoreBoardTests {

    private HandleScoreBoard scoreboard;

    @Before
    public void setUp() {
        scoreboard = new HandleScoreBoardImpl();
    }

    @Test
    public void testStartMatch() {

        FootballMatch newMatch = new FootballMatch(new Team("Mexico"), new Team("Canada"));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        assertEquals(1, matches.size());
    }

    @Test
    public void testAssertZeroScoreWhenMatchStarts() {

        FootballMatch newMatch = new FootballMatch(new Team("Mexico"), new Team("Canada"));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        assertEquals(0, matches.get(newMatch.getMatchKey()).getHomeTeam().getScore().intValue());
        assertEquals(0, matches.get(newMatch.getMatchKey()).getAwayTeam().getScore().intValue());
    }

    @Test
    public void testStartTryingAlreadyRunningMatch() {

        FootballMatch newMatch = new FootballMatch(new Team("Mexico"), new Team("Canada"));
        scoreboard.startMatch(newMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.startMatch(new FootballMatch(new Team("Mexico"), new Team("Canada"))));
        assertEquals("Match already exists", exception.getMessage());
    }

    @Test
    public void testMultipleMatchesIncludingActiveTeam() {

        FootballMatch newMatch = new FootballMatch(new Team("Mexico"), new Team("Canada"));
        scoreboard.startMatch(newMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.startMatch(new FootballMatch(new Team("Mexico"), new Team("Canada"))));
        assertEquals("Match already exists", exception.getMessage());
    }


}
