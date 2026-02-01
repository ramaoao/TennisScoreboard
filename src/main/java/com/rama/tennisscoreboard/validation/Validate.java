package com.rama.tennisscoreboard.validation;

import com.rama.tennisscoreboard.dto.PlayerNameDto;
import com.rama.tennisscoreboard.exception.ResourceNotFoundException;
import com.rama.tennisscoreboard.exception.ValidationException;
import com.rama.tennisscoreboard.model.MatchScore;

import java.util.Objects;
import java.util.UUID;

public class Validate {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;

    public void validateNames(PlayerNameDto dto) {
        validateSinglePlayerName(dto.getPlayer1Name(), "First player");
        validateSinglePlayerName(dto.getPlayer2Name(), "Second player");

        if (dto.getPlayer1Name().equalsIgnoreCase(dto.getPlayer2Name())) {
            throw new ValidationException("Player names cannot be identical.");
        }
    }

    private void validateSinglePlayerName(String name, String label) {
        validateNotBlank(name, label + " name cannot be empty.");
        validateNameLength(name, label);
    }

    public UUID validateAndParseUuid(String uuidString) {
        validateNotBlank(uuidString, "Match ID is missing.");
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid match ID format.");
        }
    }

    public void validateMatchScore(MatchScore matchScore) {
        if (matchScore == null) {
            throw new ResourceNotFoundException("Match not found or already finished.");
        }
    }

    public Long validateAndParseScorerId(String scorerIdString) {
        validateNotBlank(scorerIdString, "Scorer ID is missing.");

        try {
            return Long.parseLong(scorerIdString);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid scorer ID format.");
        }
    }

    public void validateScorerBelongsToPlayers(MatchScore matchScore, Long scorerId) {
        Long player1Id = matchScore.getPlayer1().getId();
        Long player2Id = matchScore.getPlayer2().getId();

        if (!Objects.equals(scorerId, player1Id) && !Objects.equals(scorerId, player2Id)) {
            throw new ValidationException("Scorer ID '" + scorerId + "' does not belong to this match.");
        }
    }

    public int validatePageParameter(String pageString) {
        int defaultPage = 1;

        if (pageString == null) {
                return defaultPage;
        } else {
            try {
                return Integer.parseInt(pageString);
            } catch (NumberFormatException e) {
                throw new ValidationException("Page number must be a positive integer and must not contain spaces or other characters.");
            }
        }
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(message);
        }
    }

    private void validateNameLength(String name, String label) {
        if (name.length() < MIN_NAME_LENGTH) {
            throw new ValidationException(label + " name is too short (min " + MIN_NAME_LENGTH + " characters).");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(label + " name is too long (max " + MAX_NAME_LENGTH + " characters).");
        }
    }
}
