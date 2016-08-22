package com.klaymanlei.stocrawler.dataparser;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShanghaiStockExchangeParserTest{

	private ShanghaiStockExchangeParser parser = new ShanghaiStockExchangeParser();
	
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
	public void testCrawlStockList() {
		try {
			parser.crawlStockList();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		fail("Not yet implemented");
	}

}
