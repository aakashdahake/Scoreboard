package org.scoreboard.Enums;

public enum FootballTeams {

    MEXICO("Mexico"),
    CANADA("Canada"),
    SPAIN("Spain"),
    BRAZIL("Brazil"),
    GERMANY("Germany"),
    FRANCE("France"),
    URUGUAY("Uruguay"),
    ITALY("Italy"),
    ARGENTINA("Argentina"),
    AUSTRALIA("Australia");

    private final String teamName;

    FootballTeams(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    @Override
    public String toString() {
        return teamName;
    }
}
