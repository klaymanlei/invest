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
	 * ץȡָ����˾�б���ÿ����˾��ָ��ʱ�������е���ʷ�۸񣬲�����ʷ�۸񱣴������ݿ��С���ץȡ��ʷ�۸�Ϊ��K������
	 * 
	 * @param companys
	 *            ��˾�б�
	 * @param start
	 *            ��ʼ����
	 * @param end
	 *            ��������
	 */
	public void capturePrice(List<Company> companys, Date start, Date end) {
		tool = new PersistenceToolFactory().getPersistenceTool();
		List<Company> failedList = new ArrayList<Company>();
		try {
			for (Company company : companys) {
				boolean success = false;
				// ͨ��whileѭ��ʵ�����Ի���
				// while (!success) {
				// ץȡָ����˾�Ĺ�Ʊ��ʷ�۸�
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
					// ����۸���ʷ���ͷ��ڴ�
					company.getStock().cleanHist();
					// break;
				}
				// try {
				// Thread.sleep(5 * 60 * 1000);
				// } catch (Exception e) {
				// }
				else {
					// ��¼�۸�ץȡʧ�ܵĹ�˾
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
	 * ץȡָ����˾��ĳһʱ����ڵ���ʷ�ɼۣ�ץȡ���Ĺɼ����ݱ�������company������
	 * 
	 * @param company
	 *            ָ���Ĺ�˾
	 * @param start
	 *            ��ʼ����
	 * @param end
	 *            ��������
	 * @return ץȡ�ɼ��Ƿ�ɹ�
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
