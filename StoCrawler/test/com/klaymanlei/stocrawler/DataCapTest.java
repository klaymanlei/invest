package com.klaymanlei.stocrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.FileHandler;

public class DataCapTest {

	private DataCap cap = new DataCap();

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
		// testCaptureShanghaiExchangeCompany();
		// testCaptureShenzhenExchangeCompany();
		// testCaptureFinancialStatements();
		testCapturePrice();
		// fail("Not yet implemented");
	}

	private void testCaptureShanghaiExchangeCompany() {
		cap.captureShanghaiExchangeCompany();
	}

	private void testCaptureShenzhenExchangeCompany() {
		cap.captureShenzhenExchangeCompany();
	}

	private void testCaptureFinancialStatements() {
		int start = 2424;
		int end = 2476;
		
		List<Company> companys = queryCompany();
		System.out.println("Total " + companys.size() + " companys.");
		cap.captureFinancialStatements(companys.subList(start, end));
	}

	/**
	 * 抓取股票价格历史数据
	 */
	private void testCapturePrice() {
		List<Company> companys = queryCompany();
		List<Company> list = new ArrayList<Company>();
		// 过滤符合特定条件的公司
		for (Company c: companys) {
			if (c.getStock().getCode().compareTo("600270") > 0)
			{
				list.add(c);
			}
		}
		// 设定要抓取历史记录的起始和终止时间
		Calendar c = Calendar.getInstance();
		Date end = new Date(c);
		c.set(2012, 9, 6);
		Date start = new Date(c);
		//List<Company> companys = readCompany("C:/Users/Lei Dayu/Desktop/missing price data - 副本.txt");
		cap.capturePrice(list, start, end);
	}

	/**
	 * 从给定文件中读取公司列表
	 * @param path 公司列表文件路径
	 * @return 公司列表
	 */
	private List<Company> readCompany(String path) {
		FileHandler file = new FileHandler(path);
		List<Company> list = new ArrayList<Company>();
		try {
			file.openRead();
			String line;
			while ((line = file.read()) != null) {
				Stock s = new Stock(line);
				Company c = new Company(s);
				list.add(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 从数据库中查询公司列表
	 * @return 公司列表
	 */
	private List<Company> queryCompany() {
		PersistenceTool tool = new PersistenceToolFactory()
				.getPersistenceTool();
		List<Company> list = (List<Company>) tool.query("PoCompany");
		return list;
	}
}
