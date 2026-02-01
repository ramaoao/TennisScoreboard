package com.rama.tennisscoreboard.filter;

import com.rama.tennisscoreboard.exception.DataAccessException;
import com.rama.tennisscoreboard.exception.ResourceNotFoundException;
import com.rama.tennisscoreboard.exception.ValidationException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(urlPatterns = "/*")
public class ErrorHandlingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleException((HttpServletRequest) request, (HttpServletResponse) response, e);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException, ServletException {
        Throwable rootCause = e;
        while (rootCause instanceof ServletException && rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        if (rootCause instanceof ValidationException) {
            sendError(request, response, SC_BAD_REQUEST, e.getMessage());
        } else if (rootCause instanceof ResourceNotFoundException) {
            sendError(request, response, SC_NOT_FOUND, e.getMessage());
        } else if (rootCause instanceof DataAccessException) {
            sendError(request, response, SC_INTERNAL_SERVER_ERROR, "An error has occurred with the database.");
            rootCause.printStackTrace();
        } else {
            sendError(request, response, SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            rootCause.printStackTrace();
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, int status, String message) throws IOException, ServletException {
        if(!response.isCommitted()) {
            response.setStatus(status);
            response.setContentType("text/html; charset=UTF-8");
            request.setAttribute("errorMessage", message);
            request.getRequestDispatcher("WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
