package com.rama.tennisscoreboard.dao;

import com.rama.tennisscoreboard.exception.DataAccessException;
import com.rama.tennisscoreboard.model.Match;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;

public class HibernateMatchDao implements MatchDao {
    @Override
    public Match saveMatch(Session session, Match match) {
        try {
            session.persist(match);
            return match;
        } catch (PersistenceException e) {
            throw new DataAccessException("Error when saving a match to the database.", e);
        }
    }
}
