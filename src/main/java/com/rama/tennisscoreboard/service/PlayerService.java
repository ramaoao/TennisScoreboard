package com.rama.tennisscoreboard.service;

import com.rama.tennisscoreboard.dao.PlayerDao;
import com.rama.tennisscoreboard.exception.DataAccessException;
import com.rama.tennisscoreboard.model.Player;
import com.rama.tennisscoreboard.util.TransactionalExecutor;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;

public class PlayerService {
    private final PlayerDao playerDAO;
    private final TransactionalExecutor transactionalExecutor;

    public PlayerService(PlayerDao playerDAO, TransactionalExecutor transactionalExecutor) {
        this.playerDAO = playerDAO;
        this.transactionalExecutor = transactionalExecutor;
    }

    public Player getOrCreatePlayer(String name) {
        return transactionalExecutor.executeInTransaction(session -> {
            Optional<Player> found = playerDAO.findPlayerByName(session, name);
            if (found.isPresent()) {
                return found.get();
            }

            try {
                Player newPlayer = new Player(name);
                playerDAO.savePlayer(session, newPlayer);

                session.flush();
                return newPlayer;
            } catch (PersistenceException e) {
                if (e.getCause() instanceof ConstraintViolationException) {
                    session.clear();

                    return playerDAO.findPlayerByName(session, name)
                            .orElseThrow(() -> new DataAccessException("Couldn't get or create player with the name: " + name, e));
                }
                throw e;
            }
        });
    }
}
