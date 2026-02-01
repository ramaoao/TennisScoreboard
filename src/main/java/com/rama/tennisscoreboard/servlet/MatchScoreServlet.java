package com.rama.tennisscoreboard.servlet;

import com.rama.tennisscoreboard.dto.MatchScoreViewDto;
import com.rama.tennisscoreboard.mapper.MatchMapper;
import com.rama.tennisscoreboard.model.MatchScore;
import com.rama.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.rama.tennisscoreboard.service.MatchScoreCalculationService;
import com.rama.tennisscoreboard.service.OngoingMatchesService;
import com.rama.tennisscoreboard.validation.Validate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "MatchScoreServlet", urlPatterns = "/match-score")
public class MatchScoreServlet extends HttpServlet {
    OngoingMatchesService ongoingMatchesService;
    MatchScoreCalculationService matchScoreCalculationService;
    FinishedMatchesPersistenceService finishedMatchesPersistenceService;
    MatchMapper mapper;
    Validate validate;

    @Override
    public void init() {
        this.ongoingMatchesService = (OngoingMatchesService) getServletContext().getAttribute("ongoingMatchesService");
        this.matchScoreCalculationService = (MatchScoreCalculationService) getServletContext().getAttribute("matchScoreCalculationService");
        this.finishedMatchesPersistenceService = (FinishedMatchesPersistenceService) getServletContext().getAttribute("finishedMatchesPersistenceService");
        this.validate = (Validate) getServletContext().getAttribute("validation");
        this.mapper = (MatchMapper) getServletContext().getAttribute("mapper");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = validate.validateAndParseUuid(request.getParameter("uuid"));

        MatchScore matchScore = ongoingMatchesService.getMatch(uuid);
        validate.validateMatchScore(matchScore);

        MatchScoreViewDto dto = mapper.toScoreDto(matchScore);

        request.setAttribute("match", dto);

        request.getRequestDispatcher("WEB-INF/views/match-score.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uuidString = request.getParameter("uuid");
        String scorerIdString = request.getParameter("scorerId");

        UUID uuid = validate.validateAndParseUuid(uuidString);
        Long scorerId = validate.validateAndParseScorerId(scorerIdString);

        MatchScore matchScore = ongoingMatchesService.getMatch(uuid);
        validate.validateMatchScore(matchScore);

        validate.validateScorerBelongsToPlayers(matchScore, scorerId);

        matchScoreCalculationService.calculateMatchScore(matchScore, scorerId);

        if (matchScore.getWinner() != null) {
            finishedMatchesPersistenceService.saveMatch(matchScore);

            ongoingMatchesService.removeMatch(uuid);

            response.sendRedirect(request.getContextPath() + "/matches?page=1&filter_by_player_name=" + matchScore.getPlayer1().getName());
        } else {
            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + uuid);
        }
    }
}
