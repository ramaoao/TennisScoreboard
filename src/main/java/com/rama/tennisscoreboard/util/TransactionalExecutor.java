package com.rama.tennisscoreboard.util;

import com.rama.tennisscoreboard.exception.DataAccessException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

public class TransactionalExecutor {
    private final SessionFactory sessionFactory;

    public TransactionalExecutor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(Function<Session, T> work) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            T result = work.apply(session);

            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Error when executing a transaction in the database.", e);
        }
    }
}
