package org.scoreboard;


import org.junit.Before;
import org.junit.Test;
import org.scoreboard.BusinessLogic.HandleScoreBoard;
import org.scoreboard.BusinessLogic.HandleScoreBoardImpl;
import org.scoreboard.Enums.FootballTeams;
import org.scoreboard.Model.DTO.FootballMatch;
import org.scoreboard.Model.DTO.Team;
import org.scoreboard.Model.Error.ScoreboardException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

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
        assertTrue(matches.get(newMatch.getMatchKey()).isMatchActive());
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
            assertEquals(1, matches.get(currentMatch.getMatchKey()).getHomeTeam().getScore().intValue());
            assertEquals(0, matches.get(currentMatch.getMatchKey()).getAwayTeam().getScore().intValue());
        } catch (InterruptedException e) {
            throw new ScoreboardException(e.getMessage());
        } finally {
            threadInterrupt(thread1, thread2);
        }

        assertEquals(1, matches.get(currentMatch.getMatchKey()).getHomeTeam().getScore().intValue());
        assertEquals(0, matches.get(currentMatch.getMatchKey()).getAwayTeam().getScore().intValue());
    }

    @Test
    public void testEndMatch() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());
        scoreboard.endMatch(currentMatch);
        assertEquals(0, matches.size());
    }

    @Test
    public void testEndMatchWithInvalidMatch() {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.endMatch(new FootballMatch(new Team(FootballTeams.SPAIN.getTeamName()), new Team(FootballTeams.ITALY.getTeamName()))));
        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    public void testEndMatchThatIsNotActive(){
        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());
        scoreboard.endMatch(currentMatch);
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.endMatch(currentMatch));
        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    public void testEndMatchWithMultipleThreadsTryingToEndSameMatchAtSameTime() throws InterruptedException {

        FootballMatch newMatch = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        scoreboard.startMatch(newMatch);
        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();
        FootballMatch currentMatch = matches.get(newMatch.getMatchKey());

        Thread thread1 = new Thread(() -> scoreboard.endMatch(currentMatch));
        Thread thread2 = new Thread(() -> scoreboard.endMatch(currentMatch));

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

        assertEquals(0, matches.size());
    }

    @Test
    public void testEndMatchWithMultipleThreadsTryingToEndDifferentMatchesAtSameTime() throws InterruptedException {

        FootballMatch match1 = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        FootballMatch match2 = new FootballMatch(new Team(FootballTeams.SPAIN.getTeamName()), new Team(FootballTeams.ITALY.getTeamName()));

        scoreboard.startMatch(match1);
        scoreboard.startMatch(match2);

        ConcurrentHashMap<String, FootballMatch> matches = scoreboard.getMatches();

        FootballMatch currentMatch1 = matches.get("Mexico-Canada");
        FootballMatch currentMatch2 = matches.get("Spain-Italy");

        Thread thread1 = new Thread(() -> scoreboard.endMatch(currentMatch1));
        Thread thread2 = new Thread(() -> scoreboard.endMatch(currentMatch2));

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

        assertEquals(0, matches.size());
    }

    @Test
    public void testGetOrderedSummary() {

        FootballMatch match1 = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        FootballMatch match2 = new FootballMatch(new Team(FootballTeams.SPAIN.getTeamName()), new Team(FootballTeams.BRAZIL.getTeamName()));
        FootballMatch match3 = new FootballMatch(new Team(FootballTeams.GERMANY.getTeamName()), new Team(FootballTeams.FRANCE.getTeamName()));
        FootballMatch match4 = new FootballMatch(new Team(FootballTeams.URUGUAY.getTeamName()), new Team(FootballTeams.ITALY.getTeamName()));
        FootballMatch match5 = new FootballMatch(new Team(FootballTeams.ARGENTINA.getTeamName()), new Team(FootballTeams.AUSTRALIA.getTeamName()));

        scoreboard.startMatch(match1);
        scoreboard.startMatch(match2);
        scoreboard.startMatch(match3);
        scoreboard.startMatch(match4);
        scoreboard.startMatch(match5);

        scoreboard.updateScore(match1, 0, 5);
        scoreboard.updateScore(match2, 10, 2);
        scoreboard.updateScore(match3, 2, 2);
        scoreboard.updateScore(match4, 6, 6);
        scoreboard.updateScore(match5, 3, 1);

        List<FootballMatch> orderedMatches = scoreboard.getOrderedSummary();

        assertNotNull(orderedMatches);
        assertFalse(orderedMatches.isEmpty());

        //1. Uruguay 6 - Italy 6
        assertEquals(match4, orderedMatches.get(0));
        //2. Spain 10 - Brazil 2
        assertEquals(match2, orderedMatches.get(1));
        //3. Mexico 0 - Canada 5
        assertEquals(match1, orderedMatches.get(2));
        //4. Argentina 3 - Australia 1
        assertEquals(match5, orderedMatches.get(3));
        //5. Germany 2 - France 2
        assertEquals(match3, orderedMatches.get(4));

        orderedMatches.forEach(match ->
                System.out.println(match.getHomeTeam().getName() + " "
                        + match.getHomeTeam().getScore() + " - "
                        + match.getAwayTeam().getName() + " "
                        + match.getAwayTeam().getScore()));
    }

    @Test
    public void testGetOrderedSummaryWithMultipleThreadsTryingToGetSummaryAtSameTime() throws InterruptedException {

        FootballMatch match1 = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        FootballMatch match2 = new FootballMatch(new Team(FootballTeams.SPAIN.getTeamName()), new Team(FootballTeams.BRAZIL.getTeamName()));
        FootballMatch match3 = new FootballMatch(new Team(FootballTeams.GERMANY.getTeamName()), new Team(FootballTeams.FRANCE.getTeamName()));
        FootballMatch match4 = new FootballMatch(new Team(FootballTeams.URUGUAY.getTeamName()), new Team(FootballTeams.ITALY.getTeamName()));
        FootballMatch match5 = new FootballMatch(new Team(FootballTeams.ARGENTINA.getTeamName()), new Team(FootballTeams.AUSTRALIA.getTeamName()));

        scoreboard.startMatch(match1);
        scoreboard.startMatch(match2);
        scoreboard.startMatch(match3);
        scoreboard.startMatch(match4);
        scoreboard.startMatch(match5);

        scoreboard.updateScore(match1, 0, 5);
        scoreboard.updateScore(match2, 10, 2);
        scoreboard.updateScore(match3, 2, 2);
        scoreboard.updateScore(match4, 6, 6);
        scoreboard.updateScore(match5, 3, 1);

        Thread thread1 = new Thread(() -> scoreboard.getOrderedSummary());

        Thread thread2 = new Thread(() -> scoreboard.getOrderedSummary());

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

        List<FootballMatch> orderedMatches = scoreboard.getOrderedSummary();

        assertNotNull(orderedMatches);
        assertFalse(orderedMatches.isEmpty());

        //1. Uruguay 6 - Italy 6
        assertEquals(match4, orderedMatches.get(0));
        //2. Spain 10 - Brazil 2
        assertEquals(match2, orderedMatches.get(1));
        //3. Mexico 0 - Canada 5
        assertEquals(match1, orderedMatches.get(2));
        //4. Argentina 3 - Australia 1
        assertEquals(match5, orderedMatches.get(3));
        //5. Germany 2 - France 2
        assertEquals(match3, orderedMatches.get(4));

        orderedMatches.forEach(match ->
                System.out.println(match.getHomeTeam().getName() + " "
                        + match.getHomeTeam().getScore() + " - "
                        + match.getAwayTeam().getName() + " "
                        + match.getAwayTeam().getScore()));
    }

    @Test
    public void testExceptionWhenThereAreNotFinishedMatches() {
        ScoreboardException exception = assertThrows(ScoreboardException.class, () -> scoreboard.getOrderedSummary());
        assertEquals("No active matches found", exception.getMessage());
    }

    @Test
    public void testSummaryDoesNotIncludeRemovedMatches() {
        FootballMatch match1 = new FootballMatch(new Team(FootballTeams.MEXICO.getTeamName()), new Team(FootballTeams.CANADA.getTeamName()));
        FootballMatch match2 = new FootballMatch(new Team(FootballTeams.SPAIN.getTeamName()), new Team(FootballTeams.BRAZIL.getTeamName()));
        FootballMatch match3 = new FootballMatch(new Team(FootballTeams.GERMANY.getTeamName()), new Team(FootballTeams.FRANCE.getTeamName()));
        FootballMatch match4 = new FootballMatch(new Team(FootballTeams.URUGUAY.getTeamName()), new Team(FootballTeams.ITALY.getTeamName()));
        FootballMatch match5 = new FootballMatch(new Team(FootballTeams.ARGENTINA.getTeamName()), new Team(FootballTeams.AUSTRALIA.getTeamName()));

        scoreboard.startMatch(match1);
        scoreboard.startMatch(match2);
        scoreboard.startMatch(match3);
        scoreboard.startMatch(match4);
        scoreboard.startMatch(match5);

        scoreboard.updateScore(match1, 0, 5);
        scoreboard.updateScore(match2, 10, 2);
        scoreboard.updateScore(match3, 2, 2);
        scoreboard.updateScore(match4, 6, 6);
        scoreboard.updateScore(match5, 3, 1);

        scoreboard.endMatch(match1);
        scoreboard.endMatch(match2);
        scoreboard.endMatch(match3);

        List<FootballMatch> orderedMatches = scoreboard.getOrderedSummary();

        assertNotNull(orderedMatches);
        assertFalse(orderedMatches.isEmpty());
        assertEquals(2, orderedMatches.size());
        assertEquals(match4, orderedMatches.get(0));
        assertEquals(match5, orderedMatches.get(1));

        orderedMatches.forEach(match ->
                System.out.println(match.getHomeTeam().getName() + " "
                        + match.getHomeTeam().getScore() + " - "
                        + match.getAwayTeam().getName() + " "
                        + match.getAwayTeam().getScore()));
    }

}