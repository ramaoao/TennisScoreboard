package com.rama.tennisscoreboard.util;

import com.rama.tennisscoreboard.model.Match;
import com.rama.tennisscoreboard.model.Player;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    public static SessionFactory buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(Player.class)
                    .addAnnotatedClass(Match.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new RuntimeException("Hibernate inizialization erroe." + e.getMessage(), e);
        }
    }
}
