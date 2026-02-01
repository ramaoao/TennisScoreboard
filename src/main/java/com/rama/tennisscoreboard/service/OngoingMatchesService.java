package com.rama.tennisscoreboard.service;

import com.rama.tennisscoreboard.model.MatchScore;
import com.rama.tennisscoreboard.model.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private final Map<UUID, MatchScore> ongoingMatches = new ConcurrentHashMap<>();

    public UUID createNewMatch(Player firstPlayer, Player secondPlayer) {
        UUID uuid = UUID.randomUUID();

        MatchScore matchScore = new MatchScore(firstPlayer, secondPlayer);
        matchScore.setUuid(uuid);

        ongoingMatches.put(uuid, matchScore);

        return uuid;
    }

    public MatchScore getMatch(UUID uuid) {
        return ongoingMatches.get(uuid);
    }

    public void removeMatch(UUID uuid) {
        ongoingMatches.remove(uuid);
    }
}