package com.rama.tennisscoreboard.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchScore {
    UUID uuid;
    Player player1;
    Player player2;
    Player winner;
    final PlayerScore playerScore;

    public MatchScore(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.playerScore = new PlayerScore();
    }
}