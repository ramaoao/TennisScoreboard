package com.rama.tennisscoreboard.service;

import com.rama.tennisscoreboard.model.*;

import java.util.Objects;

import static com.rama.tennisscoreboard.model.Point.*;

public class MatchScoreCalculationService {
    public void calculateMatchScore(MatchScore matchScore, Long scorerId) {
        synchronized (matchScore) {
            if (matchScore.getWinner() != null) return;

            PlayerScore playerScore = matchScore.getPlayerScore();

            boolean isFirstPlayer = Objects.equals(scorerId, matchScore.getPlayer1().getId());

            if (playerScore.isTieBreak()) {
                handleTieBreak(playerScore, isFirstPlayer);
            } else {
                handlePoint(playerScore, isFirstPlayer);
            }

            updateMatchStatus(matchScore, playerScore, isFirstPlayer);
        }
    }

    private void handlePoint(PlayerScore playerScore, boolean isFirstPlayer) {
        Point scorerPlayer = isFirstPlayer ? playerScore.getPlayer1Points() : playerScore.getPlayer2Points();
        Point opponentPlayer = isFirstPlayer ? playerScore.getPlayer2Points() : playerScore.getPlayer1Points();

        if (scorerPlayer.isStandardPoint()) {
            playerScore.setPoint(isFirstPlayer, scorerPlayer.next());
        } else if (scorerPlayer == FORTY) {
            if (opponentPlayer == FORTY) {
                playerScore.setPoint(isFirstPlayer, ADVANTAGE);
            } else if (opponentPlayer == ADVANTAGE) {
                playerScore.setPoint(!isFirstPlayer, FORTY);
            } else {
                winGame(playerScore, isFirstPlayer);
            }
        } else if (scorerPlayer == ADVANTAGE) {
            winGame(playerScore, isFirstPlayer);
        }
    }


    private void handleTieBreak(PlayerScore playerScore, boolean isFirstPlayer) {
        playerScore.addTieBreakPoint(isFirstPlayer);

        if (playerScore.isTieBreakFinished()) {
            playerScore.addWinGame(isFirstPlayer);
            winSet(playerScore, isFirstPlayer);
        }
    }

    private void winGame(PlayerScore playerScore, boolean isFirstPlayer) {
        playerScore.resetPoints();

        playerScore.addWinGame(isFirstPlayer);

        if (playerScore.isWinGame()) {
            winSet(playerScore, isFirstPlayer);
        }
    }

    private void winSet(PlayerScore playerScore, boolean isFirstPlayer) {
        playerScore.resetPoints();
        playerScore.resetGames();
        playerScore.resetTieBreakPoints();
        playerScore.setTieBreak(false);

        playerScore.addWinSet(isFirstPlayer);
    }

    public void updateMatchStatus(MatchScore match, PlayerScore playerScore, boolean isFirstPlayer) {
        if (playerScore.hasWinnerPlayer()) {
            Player winnerPlayer = isFirstPlayer ? match.getPlayer1() : match.getPlayer2();
            match.setWinner(winnerPlayer);
        }
    }
}