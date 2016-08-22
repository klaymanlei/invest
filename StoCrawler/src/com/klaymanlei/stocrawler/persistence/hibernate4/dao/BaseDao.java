package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import java.io.Serializable;

import org.hibernate.Session;

import com.klaymanlei.stocrawler.persistence.hibernate4.util.HibernateUtil;

public abstract class BaseDao {

	public Session getSession() {
		Session session = HibernateUtil.currentSession();
		return session;
	}

	public void closeSession() {
		HibernateUtil.closeSession();
	}
	
	@SuppressWarnings("rawtypes")
	public Object get(Class clazz, Serializable id) {
		Object obj = this.getSession().get(clazz, id);
		closeSession();
		return obj;
	}

	/**
	 * common add method
	 * 
	 * @param object
	 */
	public void save(Object object) {
		Session session = this.getSession();
		session.save(object);
		closeSession();
	}
	public void saveOrUpdate(Object object) {
		this.getSession().saveOrUpdate(object);
		closeSession();
	}
	public void merge(Object object) {
		this.getSession().merge(object);
		closeSession();
	}

	/**
	 * common update method
	 * 
	 * @param object
	 */
	public void update(Object object) {
		this.getSession().update(object);
		closeSession();
	}

	/**
	 * common delete method
	 * 
	 * @param object
	 */
	public void delete(Object object) {
		this.getSession().delete(object);
		closeSession();
	}
}
