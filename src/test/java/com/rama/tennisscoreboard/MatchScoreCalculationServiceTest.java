package com.rama.tennisscoreboard;

import com.rama.tennisscoreboard.model.MatchScore;
import com.rama.tennisscoreboard.model.Player;
import com.rama.tennisscoreboard.model.Point;
import com.rama.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.rama.tennisscoreboard.service.MatchScoreCalculationService;
import com.rama.tennisscoreboard.service.OngoingMatchesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static com.rama.tennisscoreboard.model.Point.*;
import static org.junit.jupiter.api.Assertions.*;

class MatchScoreCalculationServiceTest {
    private FinishedMatchesPersistenceService finishedMatchesPersistenceService;
    private OngoingMatchesService ongoingMatchesService;
    private MatchScoreCalculationService service;
    private MatchScore matchScore;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        service = new MatchScoreCalculationService();
        player1 = new Player(1L, "Roger Federer");
        player2 = new Player(2L, "Rafael Nadal");
        matchScore = new MatchScore(player1, player2);
    }

    @ParameterizedTest(name = "Игрок {2}: {0} очков -> выигрывает мяч -> стало: {1} очков.")
    @CsvSource({
            "1, LOVE, FIFTEEN",
            "1, FIFTEEN, THIRTY",
            "1, THIRTY, FORTY",

            "2, LOVE, FIFTEEN",
            "2, FIFTEEN, THIRTY",
            "2, THIRTY, FORTY"
    })
    @DisplayName("Стандартное начисление очков (0-40).")
    void shouldIncrementPointStandard(Long scorerId, Point initialPoints, Point expectedPoints) {
        setPlayerPoints(scorerId, initialPoints);

        getCalculateMatchScore(scorerId);

        assertEquals(expectedPoints, getPlayerPoints(scorerId));
    }

    @ParameterizedTest(name = "Ситуация 'ровно' (40-40) -> игрок {0} выигрывает мяч -> Advantage (AD).")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Игрок выигравший очко при равном счете 40-40 получает AD.")
    void shouldSetAdvantageWhenDeuceAndScore(Long scorerId, Long opponentId) {
        setPlayerPoints(scorerId, FORTY);
        setPlayerPoints(opponentId, FORTY);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(ADVANTAGE, getPlayerPoints(scorerId)),
                () -> assertEquals(FORTY, getPlayerPoints(opponentId))
        );
    }

    @ParameterizedTest(name = "Счет: Игрок {0} (40) vs Игрок {1} (AD) -> игрок {0} выигрывает мяч -> счет 40-40.")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Возврат к Deuce при прогрыше очка игроком с AD.")
    void shouldReturnToDeuceWhenOpponentScoresFromAdvantage(Long scorerId, Long opponentId) {
        setPlayerPoints(scorerId, FORTY);
        setPlayerPoints(opponentId, ADVANTAGE);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(FORTY, getPlayerPoints(scorerId)),
                () -> assertEquals(FORTY, getPlayerPoints(opponentId))
        );
    }

    @ParameterizedTest(name = "Игрок 1: {0} геймов -> выигрывает гейм -> стало: {1} геймов.")
    @CsvSource({
            "1, 0, 1",
            "1, 1, 2",
            "1, 2, 3",
            "1, 3, 4",
            "1, 4, 5",

            "2, 0, 1",
            "2, 1, 2",
            "2, 2, 3",
            "2, 3, 4",
            "2, 4, 5",
    })
    @DisplayName("Стандартное начисление геймов (0-5).")
    void shouldIncrementGameStandard(Long scorerId, int initialGames, int expectedGames) {
        setPlayerGames(scorerId, initialGames);
        setPlayerPoints(scorerId, FORTY);

        getCalculateMatchScore(scorerId);

        assertEquals(expectedGames, getPlayerGames(scorerId));
    }

    @ParameterizedTest(name = "Игрок {0} с AD выигрывает гейм у игрока {1} с 40 очками.")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Игрок с AD выигрывает мяч и побеждает гейм.")
    void shouldWinGameFromAdvantage(Long scorerId, Long opponentId) {
        setPlayerPoints(scorerId, ADVANTAGE);
        setPlayerPoints(opponentId, FORTY);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(LOVE, getPlayerPoints(scorerId)),
                () -> assertEquals(1, getPlayerGames(scorerId)),
                () -> assertEquals(LOVE, getPlayerPoints(opponentId)),
                () -> assertEquals(0, getPlayerGames(opponentId))
        );
    }

    @ParameterizedTest(name = "Игрок {0} с 6 геймами, игрок {1} 6 геймами -> начался тай-брейк.")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Счет геймов 6-6, начинается тай-брейк")
    void shouldTieBreakBegun(Long scorerId, Long opponentId) {
        setPlayerGames(scorerId, 5);
        setPlayerGames(opponentId, 6);
        setPlayerPoints(scorerId, FORTY);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(6, getPlayerGames(scorerId)),
                () -> assertTrue(isTieBreak()),
                () -> assertEquals(0, getPlayerPointsTieBreak(scorerId)),
                () -> assertEquals(0, getPlayerPointsTieBreak(opponentId))
        );
    }

    @ParameterizedTest(name = "Игрок 1: {0} очков -> выигрывает мяч -> стало: {1} очков.")
    @CsvSource({
            "1, 0, 1",
            "1, 1, 2",
            "1, 2, 3",
            "1, 3, 4",
            "1, 4, 5",
            "1, 5, 6",

            "2, 0, 1",
            "2, 1, 2",
            "2, 2, 3",
            "2, 3, 4",
            "2, 4, 5",
            "2, 5, 6",
    })
    @DisplayName("Стандартное начисление очков тай-брейка (0-6).")
    void shouldIncrementTieBreakPointsStandard(Long scorerId, int initialGames, int expectedGames) {
        setPlayerTieBreakPoints(scorerId, initialGames);
        setTieBreak(true);

        getCalculateMatchScore(scorerId);

        assertEquals(expectedGames, getPlayerPointsTieBreak(scorerId));
    }

    @ParameterizedTest(name = "Игрок {0}: 6 очков vs Игрок {1}: 5 очков -> игрок {0} выигрывает очко -> игрок {0} победил в сете.")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Победа в сете через тай-брейк (7-5).")
    void shouldWinSetWhenTieBreakScoreIs7to5(Long scorerId, Long opponentId) {
        setPlayerGames(scorerId, 6);
        setPlayerGames(opponentId, 6);
        setTieBreak(true);

        setPlayerTieBreakPoints(scorerId, 6);
        setPlayerTieBreakPoints(opponentId, 5);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(0, getPlayerPointsTieBreak(scorerId)),
                () -> assertFalse(isTieBreak()),
                () -> assertEquals(0, getPlayerGames(scorerId)),
                () -> assertEquals(1, getPlayerSets(scorerId))
        );
    }

    @ParameterizedTest(name = "Тай-брейк продолжается {1}-{2} -> игрок {0} выигрывает мяч -> {3}-{2}")
    @CsvSource({
            "1, 6, 6, 7",
            "2, 6, 6, 7",
            "1, 7, 7, 8",
            "2, 8, 8, 9"
    })
    @DisplayName("Тай-брейк продолжается при разнице в 1 очко.")
    void shouldContinueTieBreak(Long scorerId, int player1TieBreakPoints, int player2TieBreakPoints, int expectedScorerPoints) {
        setPlayerTieBreakPoints(player1.getId(), player1TieBreakPoints);
        setPlayerTieBreakPoints(player2.getId(), player2TieBreakPoints);
        setTieBreak(true);


        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(expectedScorerPoints, getPlayerPointsTieBreak(scorerId)),
                () -> assertEquals(0, getPlayerSets(scorerId)),
                () -> assertTrue(isTieBreak())
        );
    }

    @ParameterizedTest(name = "Игрок {0}: 6 очков vs Игрок {1}: 0 очков -> игрок {0} выигрывает очко -> игрок {0} победил в сете.")
    @CsvSource({
            "1", "2"
    })
    @DisplayName("Победа в тай-брейке (7-0).")
    void shouldWinSetOnTieBreak(Long scorerId) {
        setPlayerTieBreakPoints(scorerId, 6);
        setTieBreak(true);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(0, getPlayerPointsTieBreak(scorerId)),
                () -> assertFalse(isTieBreak()),
                () -> assertEquals(0, getPlayerGames(scorerId)),
                () -> assertEquals(1, getPlayerSets(scorerId))
        );
    }

    @ParameterizedTest(name = "Игрок {0}: 6 геймов vs Игрок {1}: 5 геймов -> игрок {0} выигрывает гейм -> игрок {0} победил в сете.")
    @CsvSource({
            "1, 2",
            "2, 1"
    })
    @DisplayName("Победа в сете при счете геймов 7-5.")
    void shouldWinSetWhenGameScoreIs7to5(Long scorerId, Long opponentId) {
        setPlayerGames(scorerId, 6);
        setPlayerGames(opponentId, 5);
        setPlayerPoints(scorerId, FORTY);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(LOVE, getPlayerPoints(scorerId)),
                () -> assertEquals(0, getPlayerGames(scorerId)),
                () -> assertFalse(isTieBreak()),
                () -> assertEquals(1, getPlayerSets(scorerId))
        );
    }

    @ParameterizedTest(name = "5 геймов у игрока {0} -> выигрывает очко -> выигрывает гейм -> победа в сете.")
    @CsvSource({"1", "2"})
    @DisplayName("Победа в сете при счете геймов 6-0.")
    void shouldWinSetOnGame(Long scorerId) {
        setPlayerGames(scorerId, 5);
        setPlayerPoints(scorerId, FORTY);

        getCalculateMatchScore(scorerId);

        assertAll(
                () -> assertEquals(LOVE, getPlayerPoints(scorerId)),
                () -> assertEquals(0, getPlayerGames(scorerId)),
                () -> assertFalse(isTieBreak()),
                () -> assertEquals(1, getPlayerSets(scorerId))
        );
    }

    @ParameterizedTest(name = "Игрок {0} с AD выигрывает гейм у игрока {1} с 40 очками.")
    @CsvSource({"1", "2"})
    @DisplayName("Победа в матче.")
    void shouldWinMatch(Long scorerId) {
        setPlayerSets(scorerId, 1);
        setPlayerGames(scorerId, 5);
        setPlayerPoints(scorerId, FORTY);

        getCalculateMatchScore(scorerId);

        Player expectedWinner = scorerId.equals(matchScore.getPlayer1().getId()) ? matchScore.getPlayer1() : matchScore.getPlayer2();

        assertAll(
                () -> assertEquals(2, getPlayerSets(scorerId)),
                () -> assertEquals(expectedWinner, matchScore.getWinner())
        );
    }

    private void setPlayerSets(Long scorerId, int sets) {
        if (scorerId.equals(player1.getId())) {
            matchScore.getPlayerScore().setPlayer1Sets(sets);
        } else {
            matchScore.getPlayerScore().setPlayer2Sets(sets);
        }
    }

    private int getPlayerSets(Long scorerId) {
        if (scorerId.equals(player1.getId())){
            return matchScore.getPlayerScore().getPlayer1Sets();
        } else {
            return matchScore.getPlayerScore().getPlayer2Sets();
        }
    }

    private void setPlayerGames(Long scorerId, int games) {
        if (scorerId.equals(player1.getId())) {
            matchScore.getPlayerScore().setPlayer1Games(games);
        } else {
            matchScore.getPlayerScore().setPlayer2Games(games);
        }
    }

    private int getPlayerGames(Long scorerId) {
        if (scorerId.equals(player1.getId())){
            return matchScore.getPlayerScore().getPlayer1Games();
        } else {
            return matchScore.getPlayerScore().getPlayer2Games();
        }
    }

    private void setPlayerPoints(Long scorerId, Point points) {
        if (scorerId.equals(player1.getId())) {
            matchScore.getPlayerScore().setPlayer1Points(points);
        } else {
            matchScore.getPlayerScore().setPlayer2Points(points);
        }
    }

    private Point getPlayerPoints(Long scorerId) {
        if (scorerId.equals(player1.getId())){
            return matchScore.getPlayerScore().getPlayer1Points();
        } else {
            return matchScore.getPlayerScore().getPlayer2Points();
        }
    }

    private void setPlayerTieBreakPoints(Long scorerId, int points) {
        if (scorerId.equals(player1.getId())) {
            matchScore.getPlayerScore().setPlayer1TieBreakPoints(points);
        } else {
            matchScore.getPlayerScore().setPlayer2TieBreakPoints(points);
        }
    }

    private int getPlayerPointsTieBreak(Long scorerId) {
        if (scorerId.equals(player1.getId())){
            return matchScore.getPlayerScore().getPlayer1TieBreakPoints();
        } else {
            return matchScore.getPlayerScore().getPlayer2TieBreakPoints();
        }
    }

    private void setTieBreak(boolean tieBreak) {
        matchScore.getPlayerScore().setTieBreak(tieBreak);
    }

    private boolean isTieBreak() {
        return matchScore.getPlayerScore().isTieBreak();
    }

    private Long scorerId(Long id) {
        return Objects.equals(id, player1.getId()) ? player1.getId() : player2.getId();
    }

    private void getCalculateMatchScore(Long id) {
        service.calculateMatchScore(matchScore, scorerId(id));
    }
}