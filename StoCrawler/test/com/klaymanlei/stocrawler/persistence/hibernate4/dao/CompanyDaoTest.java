package com.klaymanlei.stocrawler.persistence.hibernate4.dao;

import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany;

public class CompanyDaoTest {
	public static Logger log = Logger.getLogger(CompanyDaoTest.class);

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
		CompanyDao dao = new CompanyDao();
		List<PoCompany> list = dao.find();
		log.info(list);
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindBySample() {
		CompanyDao dao = new CompanyDao();
		PoCompany company = new PoCompany();
		company.setCode("600016");
		List<PoCompany> list = dao.findBySample(company);
		log.info(list);
		fail("Not yet implemented");
	}
}
