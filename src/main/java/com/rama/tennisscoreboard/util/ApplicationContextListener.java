package com.rama.tennisscoreboard.util;

import com.rama.tennisscoreboard.dao.HibernateMatchDao;
import com.rama.tennisscoreboard.dao.HibernatePlayerDao;
import com.rama.tennisscoreboard.dao.MatchDao;
import com.rama.tennisscoreboard.dao.PlayerDao;
import com.rama.tennisscoreboard.mapper.MatchMapper;
import com.rama.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.rama.tennisscoreboard.service.MatchScoreCalculationService;
import com.rama.tennisscoreboard.service.OngoingMatchesService;
import com.rama.tennisscoreboard.service.PlayerService;
import com.rama.tennisscoreboard.validation.Validate;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.hibernate.SessionFactory;
import org.mapstruct.factory.Mappers;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private SessionFactory sessionFactory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            this.sessionFactory = HibernateUtil.buildSessionFactory();

            TransactionalExecutor transactionalExecutor = new TransactionalExecutor(sessionFactory);
            PlayerDao playerDAO = new HibernatePlayerDao();
            MatchDao matchDao = new HibernateMatchDao();

            OngoingMatchesService ongoingMatchesService = new OngoingMatchesService();
            PlayerService playerService = new PlayerService(playerDAO, transactionalExecutor);
            MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService();
            FinishedMatchesPersistenceService finishedMatchesPersistenceService = new FinishedMatchesPersistenceService(matchDao, transactionalExecutor);

            Validate validate = new Validate();

            MatchMapper mapper = Mappers.getMapper(MatchMapper.class);

            ServletContext context = sce.getServletContext();
            context.setAttribute("sessionFactory", sessionFactory);
            context.setAttribute("transactionExecutor", transactionalExecutor);
            context.setAttribute("playerDAO", playerDAO);
            context.setAttribute("ongoingMatchesService", ongoingMatchesService);
            context.setAttribute("playerService", playerService);
            context.setAttribute("validation", validate);
            context.setAttribute("matchScoreCalculationService", matchScoreCalculationService);
            context.setAttribute("finishedMatchesPersistenceService", finishedMatchesPersistenceService);
            context.setAttribute("mapper", mapper);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize application context", e);
        }
    }
}