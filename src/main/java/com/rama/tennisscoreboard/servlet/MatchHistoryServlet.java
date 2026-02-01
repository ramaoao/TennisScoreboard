package com.rama.tennisscoreboard.servlet;

import com.rama.tennisscoreboard.dto.FinishedMatchDto;
import com.rama.tennisscoreboard.mapper.MatchMapper;
import com.rama.tennisscoreboard.model.Match;
import com.rama.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.rama.tennisscoreboard.validation.Validate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MatchHistoryServlet", urlPatterns = "/matches")
public class MatchHistoryServlet extends HttpServlet {
    FinishedMatchesPersistenceService finishedMatchesPersistenceService;
    MatchMapper mapper;
    Validate validate;

    @Override
    public void init() {
        this.finishedMatchesPersistenceService = (FinishedMatchesPersistenceService) getServletContext().getAttribute("finishedMatchesPersistenceService");
        this.mapper = (MatchMapper) getServletContext().getAttribute("mapper");
        this.validate = (Validate) getServletContext().getAttribute("validation");
    }

    private final int PAGE_SIZE = 10;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = validate.validatePageParameter(request.getParameter("page"));
        String name = request.getParameter("filter_by_player_name");

        long totalMatchesCount = finishedMatchesPersistenceService.countMatchesByPlayerName(name);

        int totalPages = finishedMatchesPersistenceService.calculateTotalPages(totalMatchesCount, PAGE_SIZE);

        List<Match> matchesEntity = finishedMatchesPersistenceService.getFinishedMatchesByPlayerName(name, page, PAGE_SIZE);

        List<FinishedMatchDto> matchesDtos = mapper.toFinishedMatchDtoList(matchesEntity);

        request.setAttribute("currentPage", page);
        request.setAttribute("filterByPlayerName", name);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("matches", matchesDtos);

        request.getRequestDispatcher("WEB-INF/views/matches.jsp").forward(request, response);
    }
}
