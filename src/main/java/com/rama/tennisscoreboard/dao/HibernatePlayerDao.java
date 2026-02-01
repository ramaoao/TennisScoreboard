package com.rama.tennisscoreboard.dao;

import com.rama.tennisscoreboard.model.Player;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class HibernatePlayerDao implements PlayerDao {
    @Override
    public Optional<Player> findPlayerByName(Session session, String name) {
            Query<Player> query = session.createQuery("from Player where name = :name", Player.class)
                    .setParameter("name", name);
            return Optional.ofNullable(query.uniqueResult());
    }

    @Override
    public Player savePlayer(Session session, Player player) {
            session.persist(player);
            return player;
    }
}
