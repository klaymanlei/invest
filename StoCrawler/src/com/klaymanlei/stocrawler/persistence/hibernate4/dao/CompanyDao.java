package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany;
import com.klaymanlei.utils.Utilities;

public class CompanyDao extends BaseDao {

	@SuppressWarnings("unchecked")
	public List<PoCompany> find() {
		List<PoCompany> list = null;
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		list = (List<PoCompany>) session.createQuery("select distinct c from PoCompany c").list();

		// 计算总数
		// query.setFirstResult(0);
		// query.setMaxResults(10);
		tx.commit();
		closeSession();

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<PoCompany> findBySample(PoCompany company) {
		List<PoCompany> list = null;
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(PoCompany.class);
		if (company != null) {
			if (company.getBookValue() >= 0)
				criteria.add(Restrictions.eq("bookValue", company.getBookValue()));
			if (!Utilities.isEmpty(company.getCode()))
				criteria.add(Restrictions.like("code", company.getCode()));
			if (company.getEps() >= 0)
				criteria.add(Restrictions.eq("eps", company.getEps()));
			if (!Utilities.isEmpty(company.getIndustry()))
				criteria.add(Restrictions.like("industry", company.getIndustry()));
			if (!Utilities.isEmpty(company.getMarket()))
				criteria.add(Restrictions.like("market", company.getMarket()));
			if (company.getMktCap() >= 0)
				criteria.add(Restrictions.eq("mktCap", company.getMktCap()));
			if (!Utilities.isEmpty(company.getName()))
				criteria.add(Restrictions.like("name", company.getName()));
			if (company.getPe() >= 0)
				criteria.add(Restrictions.eq("bookValue", company.getPe()));
			if (company.getShares() >= 0)
				criteria.add(Restrictions.eq("bookValue", company.getShares()));
		}
		list = (List<PoCompany>) criteria.list();

		// 计算总数
		// query.setFirstResult(0);
		// query.setMaxResults(10);
		tx.commit();
		closeSession();

		return list;
	}

}
