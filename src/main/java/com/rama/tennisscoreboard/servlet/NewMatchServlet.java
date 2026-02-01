package com.rama.tennisscoreboard.servlet;

import com.rama.tennisscoreboard.dto.PlayerNameDto;
import com.rama.tennisscoreboard.exception.ValidationException;
import com.rama.tennisscoreboard.model.Player;
import com.rama.tennisscoreboard.service.OngoingMatchesService;
import com.rama.tennisscoreboard.service.PlayerService;
import com.rama.tennisscoreboard.util.InputSanitizer;
import com.rama.tennisscoreboard.validation.Validate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "NewMatchServlet", urlPatterns = "/new-match")
public class NewMatchServlet extends HttpServlet {
    private PlayerService playerService;
    private OngoingMatchesService ongoingMatchesService;
    private Validate validate;

    public void init() {
        this.ongoingMatchesService = (OngoingMatchesService) getServletContext().getAttribute("ongoingMatchesService");
        this.playerService = (PlayerService) getServletContext().getAttribute("playerService");
        this.validate = (Validate) getServletContext().getAttribute("validation");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/views/new-match.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name1Parameter = InputSanitizer.sanitizeName(request.getParameter("player1"));
        String name2Parameter = InputSanitizer.sanitizeName(request.getParameter("player2"));

        PlayerNameDto requestDto = new PlayerNameDto(name1Parameter, name2Parameter);

        try {
            validate.validateNames(requestDto);

            Player name1 = playerService.getOrCreatePlayer(requestDto.getPlayer1Name());
            Player name2 = playerService.getOrCreatePlayer(requestDto.getPlayer2Name());

            UUID newMatchId = ongoingMatchesService.createNewMatch(name1, name2);

            response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + newMatchId);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());

            request.setAttribute("prevName1", name1Parameter);
            request.setAttribute("prevName2", name2Parameter);

            request.getRequestDispatcher("WEB-INF/views/new-match.jsp").forward(request, response);
        }
    }
}