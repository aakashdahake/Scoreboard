package org.scoreboard.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Team {
    private String name;
    private AtomicInteger score;

    public Team(String name) {
        this.name = name;
        this.score = new AtomicInteger(0);
    }

    public void setScore(Integer score) {
        this.score.set(score);
    }
}
