package com.rama.tennisscoreboard.dao;

import com.rama.tennisscoreboard.model.Player;
import org.hibernate.Session;

import java.util.Optional;

public interface PlayerDao {
    Optional<Player> findPlayerByName(Session session, String name);

    Player savePlayer(Session session, Player player);
}
