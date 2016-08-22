package com.klaymanlei.stocrawler.dataparser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShenzhenStockExchangeParserTest {

	public static Logger log = Logger
			.getLogger(ShenzhenStockExchangeParserTest.class);

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
		ShenzhenStockExchangeParser parser = new ShenzhenStockExchangeParser();
		try {
			log.debug(parser.crawlStockList().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//fail("Not yet implemented");
	}

}
