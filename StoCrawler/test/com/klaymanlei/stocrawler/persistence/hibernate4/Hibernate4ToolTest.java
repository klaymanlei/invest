package com.klaymanlei.stocrawler.persistence.hibernate4;

import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.klaymanlei.stocrawler.beans.BalanceSheet;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.beans.Stock;

public class Hibernate4ToolTest {

	private Hibernate4Tool tool;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tool = new Hibernate4Tool();
	}

	@After
	public void tearDown() throws Exception {
		tool.close();
	}

	@Test
	public void testCompany() {
		
		// create a couple of events...
		Stock stock = new Stock("600016");
		stock.setBookValue(1.1);
		stock.setEps(2.2);
		stock.setMarket("上海证券交易所");
		stock.setMktCap(10000000000l);
		stock.setName("民生银行");
		stock.setPe(3.3);
		stock.setShares(20000000000l);
		
		Company comp = new Company(stock);
		comp.setIndustry("银行业");
		comp.setName("民生银行");
		tool.saveOrUpdate(comp);

		// now lets pull events from the database and list them
        List<?> result = tool.query("Company");
		for ( Company company : (List<Company>) result ) {
			System.out.println(company);
		}
        
		tool.delete(comp);
	}
	
	@Test
	public void testStockPriceHist() {
		
		// create a couple of events...
		StoHist hist = new StoHist("600016", new com.klaymanlei.utils.Date(Calendar.getInstance()));
		
		hist.setOpen(1.1);
		hist.setClose(2.2);
		hist.setHigh(3.3);
		hist.setLow(4.4);
		hist.setVolume(10000000000l);
		hist.setMktCap(20000000000l);
		hist.setPe(5.5);
		hist.setEps(6.6);
		hist.setShares(30000000000l);
		hist.setBookValue(7.7);
		
		tool.saveOrUpdate(hist);

		// now lets pull events from the database and list them
        List<?> result = tool.query("StockPriceHist");
		for ( StoHist price : (List<StoHist>) result ) {
			System.out.println(price);
		}
	}
	
	@Test
	public void testFinancialReportHist() {
		
		// create a couple of events...
		BalanceSheet hist = new BalanceSheet("600016", new com.klaymanlei.utils.Date(Calendar.getInstance()));
		
		hist.put("test", 1111);
		hist.put("test1", 2222);
		
		tool.saveOrUpdate(hist);

		// now lets pull events from the database and list them
        List<?> result = tool.query("StockPriceHist");
		for ( StoHist price : (List<StoHist>) result ) {
			System.out.println(price);
		}
	}

}
