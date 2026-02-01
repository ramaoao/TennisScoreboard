package com.rama.tennisscoreboard.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static com.rama.tennisscoreboard.model.Point.LOVE;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerScore {
    int player1Sets;
    int player2Sets;

    int player1Games;
    int player2Games;

    Point player1Points = LOVE;
    Point player2Points = LOVE;

    int player1TieBreakPoints;
    int player2TieBreakPoints;

    boolean isTieBreak;

    static final int SETS_TO_WIN_MATCH = 2;
    static final int GAMES_TO_WIN_SET = 6;
    static final int MIN_GAME_DIFF_TO_WIN_SET = 2;
    static final int TIEBREAK_POINTS_TO_WIN = 7;
    static final int MIN_TB_POINT_DIFF_TO_WIN = 2;

    public boolean hasWinnerPlayer() {
        return (player1Sets == SETS_TO_WIN_MATCH || player2Sets == SETS_TO_WIN_MATCH);
    }

    public void setPoint(boolean isFirstPlayer, Point newPoint) {
        if (isFirstPlayer) {
            this.player1Points = newPoint;
        } else {
            this.player2Points = newPoint;
        }
    }

    public void addWinSet(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.player1Sets++;
        } else {
            this.player2Sets++;
        }
    }

    public void addWinGame(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.player1Games++;
        } else {
            this.player2Games++;
        }

        if(isTieBreakBegun()) {
            this.isTieBreak = true;
            resetTieBreakPoints();
        }
    }

    public boolean isWinGame() {
        return (player1Games >= GAMES_TO_WIN_SET || player2Games >= GAMES_TO_WIN_SET) && Math.abs(player1Games - player2Games) >= 2;
    }

    public void addTieBreakPoint(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.player1TieBreakPoints++;
        } else {
            this.player2TieBreakPoints++;
        }
    }

    public boolean isTieBreakFinished() {
        return (player1TieBreakPoints >= TIEBREAK_POINTS_TO_WIN || player2TieBreakPoints >= TIEBREAK_POINTS_TO_WIN) && Math.abs(player1TieBreakPoints - player2TieBreakPoints) >= MIN_TB_POINT_DIFF_TO_WIN;
    }

    public boolean isTieBreakBegun() {
        return (player1Games == GAMES_TO_WIN_SET && player2Games == GAMES_TO_WIN_SET);
    }

    public void resetGames() {
        this.player1Games = 0;
        this.player2Games = 0;
    }

    public void resetPoints() {
        this.player1Points = LOVE;
        this.player2Points = LOVE;
    }

    public void resetTieBreakPoints() {
        this.player1TieBreakPoints = 0;
        this.player2TieBreakPoints = 0;
    }
}
