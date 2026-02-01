package com.rama.tennisscoreboard.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class MatchScoreViewDto {
    UUID uuid;

    Long player1Id;
    Long player2Id;

    String player1Name;
    String player2Name;

    int player1Sets;
    int player2Sets;

    int player1Games;
    int player2Games;

    String player1Points;
    String player2Points;
}
