package com.rama.tennisscoreboard.dao;

import com.rama.tennisscoreboard.model.Match;
import org.hibernate.Session;

public interface MatchDao {
    Match saveMatch(Session session, Match match);
}
