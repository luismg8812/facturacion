package com.fact.utils;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

	private static HibernateUtil instance = null;
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static SessionFactory sessionFactory2;
	private static ServiceRegistry serviceRegistry2;

	private HibernateUtil() {
	}

	static {
		try {
			Configuration configuration = new Configuration().configure("/META-INF/hibernate/hibernate.cfg.xml");
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
					.buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (HibernateException he) {
			System.err.println("Erro ao criar a conexao com o base de dados: " + he);
			throw new ExceptionInInitializerError(he);
		}
	}
//	static {
//		try {
//			Configuration configuration2 = new Configuration().configure("/META-INF/hibernate/hibernate2.cfg.xml");
//			serviceRegistry2 = new ServiceRegistryBuilder().applySettings(configuration2.getProperties())
//					.buildServiceRegistry();
//			sessionFactory2 = configuration2.buildSessionFactory(serviceRegistry2);
//		} catch (HibernateException e) {
//			System.out.println("Error server 2");
//			throw new ExceptionInInitializerError(e);
//		}
//	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory2() {
		return sessionFactory2;
	}

	public Session getSession() {		
			return getSessionFactory().openSession();		
	}
	public Session getSession2() {		
		return getSessionFactory2().openSession();		
}

	public static HibernateUtil getInstance() {
		if (instance == null) {
			instance = new HibernateUtil();
		}
		return instance;
	}

	public static void testConnection() throws Exception {
		getSessionFactory().openSession();
	}
}