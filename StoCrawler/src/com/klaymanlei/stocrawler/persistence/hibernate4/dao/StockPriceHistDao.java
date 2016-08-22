package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.klaymanlei.stocrawler.persistence.hibernate4.model.StockPriceHist;

public class StockPriceHistDao extends BaseDao {

	@SuppressWarnings("unchecked")
	public List<StockPriceHist> findByCode(String code) {
		List<StockPriceHist> prices = null;
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(StockPriceHist.class);
		criteria.add(Restrictions.eq("code", code));
		Order order = Order.asc("date");
		criteria.addOrder(order);
		prices = (List<StockPriceHist>) criteria.list();
		tx.commit();
		closeSession();

		return prices;
	}
}
