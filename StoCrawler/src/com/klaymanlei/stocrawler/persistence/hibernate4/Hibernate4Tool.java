package com.klaymanlei.stocrawler.persistence.hibernate4;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.stocrawler.persistence.PersistenceTool;
import com.klaymanlei.utils.Utilities;

public class Hibernate4Tool implements PersistenceTool {
	private final static Logger log = Logger.getLogger(Hibernate4Tool.class);
	private SessionFactory sessionFactory;
	private ModelFactory factory = new ModelFactory();

	public Hibernate4Tool() {
		// A SessionFactory is set up once for an application
		sessionFactory = new Configuration().configure() // configures settings
															// from
															// hibernate.cfg.xml
				.buildSessionFactory();
	}

	public void close() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	public void saveOrUpdate(Object obj) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		List<Object> models = factory.createModel(obj);

		for (Object model : models) {
			session.saveOrUpdate(model);
		}
		session.getTransaction().commit();
		session.close();
	}

	public List<?> query(String dataKind) {
		List<Object> list = new ArrayList<Object>();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		List<?> result = session.createQuery("from " + dataKind).list();
		if (Utilities.isEmpty(result))
			return list;

		for (Object obj : (List<Object>) result) {
			list.add(factory.createBean(obj));
		}
		session.getTransaction().commit();
		session.close();
		return list;
	}

	public void delete(Object obj) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		List<Object> models = factory.createModel(obj);

		for (Object model : models) {
			session.delete(model);
		}
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Company queryCompany(Stock stock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Company queryCompanyWithAllSituations(Stock stock) {
		// TODO Auto-generated method stub
		return null;
	}
}
