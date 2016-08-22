package com.klaymanlei.stocrawler.algorithm;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.stocrawler.persistence.PersistenceTool;
import com.klaymanlei.stocrawler.persistence.PersistenceToolFactory;

public class AlgNetCurrentAssetsTest {

	private AlgNetCurrentAssets alg = new AlgNetCurrentAssets();
	
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

	@Test
	public void test() {
		Stock stock = new Stock("000002");
//		Company company = new Company(stock);
//		String kind = "balance";
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2012, 6 ,30);
//		Date date = calendar.getTime();
		PersistenceTool tool = new PersistenceToolFactory().getPersistenceTool();
		//List list = dao.selectFinancialReportHist(company, kind, date);
		//System.out.println(list);
	}

}
