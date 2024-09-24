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
a. Mexico 0 - Canada 5
b. Spain 10 - Brazil 2
c. Germany 2 - France 2
d. Uruguay 6 - Italy 6
e. Argentina 3 - Australia 1

The summary should be as follows:
1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2

## Features

- Start new matches with home and away teams
- Update scores for individual teams
- Finish completed matches
- Retrieve a summary of active matches ordered by total score

## Installation

## Running the tests

## Tools Used

## Example

## Authors
- Aakash Dahae


