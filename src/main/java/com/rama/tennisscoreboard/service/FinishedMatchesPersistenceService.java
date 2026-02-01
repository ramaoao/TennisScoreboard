package com.rama.tennisscoreboard.service;

import com.rama.tennisscoreboard.dao.MatchDao;
import com.rama.tennisscoreboard.model.Match;
import com.rama.tennisscoreboard.model.MatchScore;
import com.rama.tennisscoreboard.util.TransactionalExecutor;

import java.util.List;

public class FinishedMatchesPersistenceService {
    private final MatchDao matchDao;
    private final TransactionalExecutor transactionalExecutor;

    public FinishedMatchesPersistenceService(MatchDao matchDao, TransactionalExecutor transactionalExecutor) {
        this.matchDao = matchDao;
        this.transactionalExecutor = transactionalExecutor;
    }

    public void saveMatch(MatchScore matchScore) {
        transactionalExecutor.executeInTransaction(session -> {
            Match newMatch = new Match(
                    session.merge(matchScore.getPlayer1()),
                    session.merge(matchScore.getPlayer2()),
                    session.merge(matchScore.getWinner())
            );

            matchDao.saveMatch(session, newMatch);

            session.flush();

            return null;
        });
    }

    public long countMatchesByPlayerName(String name) {
        if (name == null || name.isBlank()) {
            return countAllMatches();
        }

        String hql = "SELECT COUNT(m) FROM Match m " +
                     "WHERE m.player1.name = :name OR m.player2.name = :name";

        return transactionalExecutor.executeInTransaction(session -> {
            return session.createQuery(hql, Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
        });
    }

    public int calculateTotalPages(long currentPage, int PAGE_SIZE) {
        if (currentPage == 0) {
            return 1;
        }
        return (int) Math.ceil((double) currentPage / PAGE_SIZE);
    }

    private long countAllMatches() {
        return transactionalExecutor.executeInTransaction(session -> {
            return session.createQuery("SELECT COUNT(m) FROM Match m", Long.class)
                    .getSingleResult();
        });
    }

    public List<Match> getFinishedMatchesByPlayerName(String name, int currentPage, int PAGE_SIZE) {
        int offset = (currentPage - 1) * PAGE_SIZE;

        boolean hasFilter = name != null && !name.isBlank();

        StringBuilder hql = new StringBuilder();
        hql.append("SELECT m FROM Match m ")
                .append("JOIN FETCH m.player1 ")
                .append("JOIN FETCH m.player2 ")
                .append("JOIN FETCH m.winner ");

        if (hasFilter) {
            hql.append("WHERE m.player1.name = :name OR m.player2.name = :name ");
        }

        hql.append("ORDER BY m.id DESC ");

        return transactionalExecutor.executeInTransaction(session -> {
            var query = session.createQuery(hql.toString(), Match.class);

            if (hasFilter) {
                query.setParameter("name", name);
            }

            return query.setFirstResult(offset)
                        .setMaxResults(PAGE_SIZE)
                        .getResultList();
        });
    }
}