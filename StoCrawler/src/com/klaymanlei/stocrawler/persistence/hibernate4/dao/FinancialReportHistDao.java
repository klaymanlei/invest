package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.FinancialReportHist;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany;
import com.klaymanlei.utils.Utilities;

public class FinancialReportHistDao extends BaseDao {

	public static Logger log = Logger.getLogger(FinancialReportHistDao.class);

	public List<FinancialReportHist> find(Company company, String kind, Date date){
		Utilities.verifyParam("Company", company);
		Utilities.verifyParam("Kind", kind);
		Utilities.verifyParam("Date", date);
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from financial_report_hist");
		sql.append(" where code='" + company.getStock().getCode() + "'");
		sql.append(" and kind='" + kind + "'");
		sql.append(" and date='2012-06-30'");

		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Query query  = getSession().createSQLQuery(sql.toString()).addEntity(FinancialReportHist.class);
//		query.setParameter("code", company.getStock().getCode());
//		query.setParameter("kind", kind);
//		query.setParameter("date", date);
 		
		//计算总数
		//query.setFirstResult(0);
		//query.setMaxResults(10);
		List list = query.list();
		tx.commit();
		closeSession();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<FinancialReportHist> findBySample(FinancialReportHist hist) {
		List<FinancialReportHist> list = null;
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(FinancialReportHist.class);
		if (hist != null) {
			if (!Utilities.isEmpty(hist.getCode()))
				criteria.add(Restrictions.like("code", hist.getCode()));
			if (!Utilities.isEmpty(hist.getKind()))
				criteria.add(Restrictions.like("kind", hist.getKind()));
			if (hist.getDate() != null)
				criteria.add(Restrictions.eq("date", hist.getDate()));
			if (!Utilities.isEmpty(hist.getItem()))
				criteria.add(Restrictions.like("item", hist.getItem()));
			if (hist.getValue() >= 0)
				criteria.add(Restrictions.eq("mktCap", hist.getValue()));
		}
		list = (List<FinancialReportHist>) criteria.list();

		// 计算总数
		// query.setFirstResult(0);
		// query.setMaxResults(10);
		tx.commit();
		closeSession();

		return list;
	}

}
