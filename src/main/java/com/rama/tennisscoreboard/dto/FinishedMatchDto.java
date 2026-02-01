package com.rama.tennisscoreboard.dto;

import lombok.Value;

@Value
public class FinishedMatchDto {
    Long id;
    String player1Name;
    String player2Name;
    String winnerName;
}
