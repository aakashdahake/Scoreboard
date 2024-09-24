package org.scoreboard;


import org.junit.Before;
import org.junit.Test;
import org.scoreboard.BusinessLogic.HandleScoreBoard;
import org.scoreboard.BusinessLogic.HandleScoreBoardImpl;
import org.scoreboard.Enums.FootballTeams;
import org.scoreboard.Model.DTO.FootballMatch;
import org.scoreboard.Model.DTO.Team;
import org.scoreboard.Model.Error.ScoreboardException;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ScoreBoardTests {

    private HandleScoreBoard scoreboard;

    // Helper method to interrupt threads
    private void threadInterrupt(Thread... threads) throws InterruptedException {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
                thread.join();
            }
        }
    }

    @Before
    public void setUp() {
        scoreboard = new HandleScoreBoardImpl();
    }

    @Test
    public void testStartMatch() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        assertEquals(1, matches.size());
    }

    @Test
    public void testAssertZeroScoreWhenMatchStarts() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        assertEquals(0, matches.get(newMatch.getMatchKey()).getHomeTeam().getScore().intValue());
        assertEquals(0, matches.get(newMatch.getMatchKey()).getAwayTeam().getScore().intValue());
    }

    @Test
    public void testStartTryingAlreadyRunningMatch() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.startMatch(new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()))));
        assertEquals("Match already exists", exception.getMessage());
    }

    @Test
    public void testMultipleMatchesIncludingActiveTeam() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.startMatch(new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()))));
        assertEquals("Match already exists", exception.getMessage());
    }

    @Test
    public void testUpdateScore() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());
        scoreboard.updateScore(currentMatch, 0, 1);
        assertEquals(0, matches.get(currentMatch.getMatchKey()).getHomeTeam().getScore().intValue());
        assertEquals(1, matches.get(currentMatch.getMatchKey()).getAwayTeam().getScore().intValue());
    }

    @Test
    public void testUpdateScoreWithInvalidMatch() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get("Mexico-Spain");
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.updateScore(currentMatch, 1, 0));
        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    public void testUpdateScoreWithNegativeScoreForHomeTeam() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.updateScore(currentMatch, -1, 0));
        assertEquals("Score can not be negative for team :: Mexico", exception.getMessage());
    }

    @Test
    public void testUpdateScoreWithNegativeScoreForAwayTeam() {


        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.updateScore(currentMatch, 1, -2));
        assertEquals("Score can not be negative for team :: Canada", exception.getMessage());
    }

    @Test
    public void testUpdateScoreWithMultipleThreadsTryingToSetSameScoreAtSameTime() throws InterruptedException {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());

        Thread thread1 = new Thread(() -> scoreboard.updateScore(currentMatch, 1, 0));

        Thread thread2 = new Thread(() -> scoreboard.updateScore(currentMatch, 1, 0));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new ScoreboardException(e.getMessage());
        } finally {
            threadInterrupt(thread1, thread2);
        }

        assertEquals(1, matches.get(currentMatch.getMatchKey()).getHomeTeam().getScore().intValue());
        assertEquals(0, matches.get(currentMatch.getMatchKey()).getAwayTeam().getScore().intValue());
    }


}
