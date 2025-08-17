package com.bassine.metier.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import com.bassine.metier.entity.SectorsEntity;
import com.bassine.metier.entity.ClassesEntity;

import java.util.Properties;

public class HibernateUtil {
    
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                
                Properties settings = new Properties();
                PropertiesReader propertiesReader = new PropertiesReader("database.properties");
                
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, propertiesReader.getUrl());
                settings.put(Environment.USER, propertiesReader.getUsername());
                settings.put(Environment.PASS, propertiesReader.getPassword());
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                
                configuration.setProperties(settings);
                
                // Ajouter les entités
                configuration.addAnnotatedClass(SectorsEntity.class);
                configuration.addAnnotatedClass(ClassesEntity.class);
                
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
                    
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
