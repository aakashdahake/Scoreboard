# Live Football World Cup Scoreboard Library

## Table of Contents

- [Overview](#overview)
- [Use Case](#use-case)
- [Features](#features)
- [Installation](#installation)
- [Running the tests](#running-the-tests)
- [Tools Used](#tools-used)

- [Example](#example)


## Overview

This is a Java library designed to manage live football scores during the World Cup. It allows tracking of ongoing matches, updating scores, and retrieving summaries of matches in progress.

## Use Case

The scoreboard supports the following operations:
1. Start a new match, assuming initial score 0 – 0 and adding it the scoreboard.
   This should capture following parameters:
   a. Home team
   b. Away team
2. Update score. This should receive a pair of absolute scores: home team score and away
   team score.
3. Finish match currently in progress. This removes a match from the scoreboard.
4. Get a summary of matches in progress ordered by their total score. The matches with the
   same total score will be returned ordered by the most recently started match in the
   scoreboard.

For example, if following matches are started in the specified order and their scores
respectively updated:
1. Mexico 0 - Canada 5 
2. Spain 10 - Brazil 2 
3. Germany 2 - France 2
4. Uruguay 6 - Italy 6
5. Argentina 3 - Australia 1

The summary should be as follows:
1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2

## Features

- Start new matches with home and away teams
- Update scores for teams
- Finish completed matches
- Retrieve a summary of active matches ordered by total score

## Installation
- Clone the repository:
    ```bash
    git clone -b assignment https://github.com/aakashdahake/scoreboard.git
    ```

- Branch = **assignment**
 
- Open IDE (IntelliJ or Eclipse) & navigate to the project directory:

- Reload Maven project to install declared dependencies
 
- Alternatively for above step, installing dependencies using CLI:
  ```bash
  mvn install clean
  ```

## Running the tests
- Run the test cases using the following command in IDE terminal or CLI in the project directory:
  ```bash
  mvn test
  ```
- Run the test cases in the IDE by right-clicking on the test folder and selecting 'Run ScoreBoardTests'
    ```bash
  ScoreBoardTests.class
  ```

## Development Approach
- The project is developed using TDD approach.
- Red -> Green -> Refactor cycle is followed.

## Tools Used
- Java 17
- JUnit 5
- Maven
- IntelliJ IDEA
- Git
- Github

## Example

```java
//Create scoreboard
HandleScoreBoard scoreboard = new HandleScoreBoardImpl();

//Create a new match
FootballMatch homeTeam = new Team("Mexico");
FootballMatch awayTeam = new Team("Canada");
FootballMatch match = new FootballMatch(homeTeam, awayTeam);

//Start the match
scoreboard.startMatch(match);

//Update the score - updateScore(FootballMatch match, int homeTeamScore, int awayTeamScore)
scoreboard.updateScore(match, 0, 5);

//Get the summary of matches
List<FootballMatch> matches = scoreboard.getOrderedSummary();

//End the match
scoreboard.endMatch(match);
```

## Authors
- Aakash Dahake


