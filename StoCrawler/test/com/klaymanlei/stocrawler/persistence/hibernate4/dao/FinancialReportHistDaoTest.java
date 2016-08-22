package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.algorithm.Algorithm;
import com.klaymanlei.stocrawler.algorithm.AlgorithmManager;
import com.klaymanlei.stocrawler.beans.CompHist;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.FinancialStatements;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.stocrawler.persistence.hibernate4.ModelFactory;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.FinancialReportHist;

public class FinancialReportHistDaoTest {

	public static Logger log = Logger.getLogger(FinancialReportHistDaoTest.class);
	private FinancialReportHistDao dao = new FinancialReportHistDao();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testFind() {
		Stock stock = new Stock("000002");
		Company company = new Company(stock);
		String kind = "balance";
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 6 ,30);
		java.util.Date date = calendar.getTime();
		List hists = dao.find(company, kind, date);
		ModelFactory factory = new ModelFactory();
//		FinancialStatements sheet = factory.createFinancialReport(stock.getCode(), kind, hists);
		
		CompHist hist = new CompHist(company, new com.klaymanlei.utils.Date(calendar));
//		hist.setCurrentAssets(sheet.get().get(Constants.CURRENT_ASSETS));
//		hist.setCurrentLiabilities(sheet.get().get(Constants.CURRENT_LIABILITIES));
		log.info(hist);
		
		company.addSituation(new com.klaymanlei.utils.Date(calendar), hist);
		Algorithm a = AlgorithmManager.getAlgorithm(AlgorithmManager.NET_CURRENT_ASSETS);
		log.info(a.upToStandard(company));
		//fail("Not yet implemented");
	}

	@Test
	public void testFindBySample() {
		Stock stock = new Stock("000001");
		Company company = new Company(stock);
		String kind = "balance";
		Calendar calendar = Calendar.getInstance();
		calendar.set(1997, 5 ,30);
		java.util.Date date = calendar.getTime();
		System.out.println(date);
		FinancialReportHist sampleHist = new FinancialReportHist(stock.getCode(), kind, null);
		List hists = dao.findBySample(sampleHist);
		ModelFactory factory = new ModelFactory();
//		FinancialStatements sheet = factory.createFinancialReport(stock.getCode(), kind, hists);
		
		CompHist hist = new CompHist(company, new com.klaymanlei.utils.Date(calendar));
//		hist.setCurrentAssets(sheet.get().get(Constants.CURRENT_ASSETS));
//		hist.setCurrentLiabilities(sheet.get().get(Constants.CURRENT_LIABILITIES));
		log.info(hist);
		
		company.addSituation(new com.klaymanlei.utils.Date(calendar), hist);
		Algorithm a = AlgorithmManager.getAlgorithm(AlgorithmManager.NET_CURRENT_ASSETS);
//		log.info(a.upToStandard(company));
		//fail("Not yet implemented");
	}

}
