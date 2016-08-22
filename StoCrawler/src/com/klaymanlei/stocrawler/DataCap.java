package com.klaymanlei.stocrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.beans.CompHist;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.dataparser.HexunParser;
import com.klaymanlei.stocrawler.dataparser.ShanghaiStockExchangeParser;
import com.klaymanlei.stocrawler.dataparser.ShenzhenStockExchangeParser;
import com.klaymanlei.stocrawler.dataparser.StockHistParser;
import com.klaymanlei.stocrawler.persistence.PersistenceTool;
import com.klaymanlei.stocrawler.persistence.PersistenceToolFactory;
import com.klaymanlei.utils.Date;

public class DataCap {

	private final static Logger log = Logger.getLogger(DataCap.class);
	private static final boolean IS_TEST = Constants.IS_TEST;

	private PersistenceTool tool;

	public void captureShanghaiExchangeCompany() {
		tool = new PersistenceToolFactory().getPersistenceTool();
		try {
			ShanghaiStockExchangeParser parser = new ShanghaiStockExchangeParser();
			List<Company> list = parser.crawlStockList();
			for (Company company : list) {
				// parseFinancialStatements(company);
				// parseStockPrice(company);
				log.debug(company.getName() + "("
						+ company.getStock().getCode() + "): "
						+ list.indexOf(company) + " of " + list.size());
				saveCompany(company);
				// company.cleanHist();
				// company.getStock().cleanHist();
				if (IS_TEST)
					break;
			}
		} catch (MalformedURLException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			tool.close();
		}
	}

	public void captureShenzhenExchangeCompany() {
		tool = new PersistenceToolFactory().getPersistenceTool();
		try {
			ShenzhenStockExchangeParser parser = new ShenzhenStockExchangeParser();
			List<Company> list = parser.crawlStockList();
			for (Company company : list) {
				// parseFinancialStatements(company);
				// parseStockPrice(company);
				log.debug(company.getName() + "("
						+ company.getStock().getCode() + "): "
						+ list.indexOf(company) + " of " + list.size());
				saveCompany(company);
				// company.cleanHist();
				// company.getStock().cleanHist();
				if (IS_TEST)
					break;
			}
		} catch (MalformedURLException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			tool.close();
		}
	}

	public void captureFinancialStatements(List<Company> companys) {
		tool = new PersistenceToolFactory().getPersistenceTool();
		List<Company> failedList = new ArrayList<Company>();
		try {
			for (Company company : companys) {
				boolean success = parseFinancialStatements(company);
				if (success) {
					log.debug(company.getName() + "("
							+ company.getStock().getCode() + "): "
							+ (companys.indexOf(company) + 1) + " of "
							+ companys.size());
					saveFinancialStatements(company);
					company.cleanHist();
				} else {
					failedList.add(company);
				}
				if (IS_TEST)
					break;
			}
			for (Company company : failedList) {
				log.debug("failed: " + company);
			}
		} finally {
			tool.close();
		}
	}

	/**
	 * 抓取指定公司列表中每个公司在指定时间区间中的历史价格，并将历史价格保存在数据库中。所抓取历史价格为日K线数据
	 * 
	 * @param companys
	 *            公司列表
	 * @param start
	 *            起始日期
	 * @param end
	 *            结束日期
	 */
	public void capturePrice(List<Company> companys, Date start, Date end) {
		tool = new PersistenceToolFactory().getPersistenceTool();
		List<Company> failedList = new ArrayList<Company>();
		try {
			for (Company company : companys) {
				boolean success = false;
				// 通过while循环实现重试机制
				// while (!success) {
				// 抓取指定公司的股票历史价格
				success = parseStockPrice(company, start, end);
				if (success) {
					log.debug(company.getName() + "("
							+ company.getStock().getCode() + "): "
							+ (companys.indexOf(company) + 1) + " of "
							+ companys.size());
					log.debug("price count: "
							+ company.getStock().getHistory().size());
					TreeMap<Date, StoHist> hists = company.getStock()
							.getHistory();
					// for (StoHist hist: hists.values()) {
					// log.debug("Date: " + hist.getDate());
					// log.debug("Close: " + hist.getClose());
					// log.debug("Diff: " + hist.getDiff());
					// log.debug("Dea: " + hist.getDea());
					// log.debug("MACD: " + hist.getMacd());
					// }

					savePrice(company);
					// 清除价格历史，释放内存
					company.getStock().cleanHist();
					// break;
				}
				// try {
				// Thread.sleep(5 * 60 * 1000);
				// } catch (Exception e) {
				// }
				else {
					// 记录价格抓取失败的公司
					failedList.add(company);
				}
				// }
				if (IS_TEST)
					break;
			}
			for (Company company : failedList) {
				log.debug("failed: " + company);
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			tool.close();
		}
	}

	public boolean parseFinancialStatements(Company company) {
		HexunParser parser = new HexunParser(company.getStock().getCode());
		try {
			parser.parse(company);
			return true;
		} catch (MalformedURLException e) {
			log.error(company.getStock().getCode() + e);
		} catch (IOException e) {
			log.error(company.getStock().getCode() + e);
		}
		return false;
	}

	/**
	 * 抓取指定公司在某一时间段内的历史股价，抓取到的股价数据被保存在company对象中
	 * 
	 * @param company
	 *            指定的公司
	 * @param start
	 *            起始日期
	 * @param end
	 *            结束日期
	 * @return 抓取股价是否成功
	 */
	public boolean parseStockPrice(Company company, Date start, Date end) {
		StockHistParser parser = new StockHistParser(company.getStock()
				.getCode());
		try {
			if (start != null && end != null)
				parser.parse(company, start, end);
			else
				parser.parse(company);
			return true;
		} catch (MalformedURLException e) {
			log.error(company.getStock().getCode() + e);
		} catch (IOException e) {
			log.error(company.getStock().getCode() + e);
		} catch (IllegalArgumentException e) {
			log.error(company.getStock().getCode() + e);
		}
		return false;
	}

	private void saveCompany(Company company) {
		tool.saveOrUpdate(company);
	}

	private void saveFinancialStatements(Company company) {
		Map<Date, CompHist> compHist = company.getHistory();
		for (CompHist temp : compHist.values()) {
			tool.saveOrUpdate(temp.getBalance());
			tool.saveOrUpdate(temp.getIncomeStatement());
			tool.saveOrUpdate(temp.getCashFlow());
		}
	}

	private void savePrice(Company company) {
		Map<Date, StoHist> stoHist = company.getStock().getHistory();
		for (StoHist temp : stoHist.values()) {
			tool.saveOrUpdate(temp);
		}
	}
}
