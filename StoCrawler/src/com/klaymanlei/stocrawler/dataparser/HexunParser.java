package com.klaymanlei.stocrawler.dataparser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.CompHist;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.FinancialStatements;
import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;
import com.klaymanlei.utils.WebPageCrawler;

// 和讯网站解析
public class HexunParser {

	private final static Logger log = Logger.getLogger(HexunParser.class);

	private static final boolean IS_TEST = Constants.IS_TEST; // true: 只抓取最近的财务报表; false: 抓取全部历史财务报表
	
	private static final long BREAK_TIME = Constants.PARSE_BREAK_TIME;
	private static final String PLACEHOLDER_STOCK_CODE = Constants.PLACEHOLDER_STOCK_CODE;
	private static final int REPORT_BALANCE_SHEET = Constants.REPORT_BALANCE_SHEET;
	private static final int REPORT_INCOME_STATEMENT = Constants.REPORT_INCOME_STATEMENT;
	private static final int REPORT_CASH_FLOW = Constants.REPORT_CASH_FLOW;

	private static final String BALANCE_SHEET = "资产负债";
	private static final String INCOME_STATEMENT = "利润表";
	private static final String CASH_FLOW = "现金流量";

	private static final String FISCAL_YEAR = "会计年度";
	private static final String FISCAL_YEAR_CASH_FLOW = "报告年度";

	private String stockCode;
	private String stockUrl = "http://stockdata.stock.hexun.com/"
			+ PLACEHOLDER_STOCK_CODE + ".shtml";

	public HexunParser(String stockCode) {
		super();
		Utilities.verifyParam("stockCode", stockCode);
		this.stockCode = stockCode;
	}

	public void parse(Company company) throws MalformedURLException,
			IOException {
		// TODO 初始化Company对象
		String page = parseSummaryPage(company);

		String reportUrl = parseReportUrl(page, BALANCE_SHEET);
		parseReportPage(company, REPORT_BALANCE_SHEET, reportUrl);
		// log.debug(company);

		reportUrl = parseReportUrl(page, INCOME_STATEMENT);
		parseReportPage(company, REPORT_INCOME_STATEMENT, reportUrl);
		// log.debug(company);

		reportUrl = parseReportUrl(page, CASH_FLOW);
		parseReportPage(company, REPORT_CASH_FLOW, reportUrl);

		//log.debug(company.getLatestSituation().getBalance());
		//log.debug(company.getLatestSituation().getIncomeStatement());
		//log.debug(company.getLatestSituation().getCashFlow());
	}

	private String parseSummaryPage(Company company)
			throws MalformedURLException, IOException {
		String url = stockUrl.replaceAll(PLACEHOLDER_STOCK_CODE, stockCode);
		byte[] temp = WebPageCrawler.DownLoadPages(url);
		String page = new String(temp);
		// balanceUrl = parseBalanceUrl(page, "资产负债");
		String name = company.getName();
		if (Utilities.isEmpty(name))
			name = parseCompanyName(page);
		Utilities.verifyParam("Company name", name);
		company.setName(name);
		return page;
		// log.debug(page);
	}

	private String parseCompanyName(String page) throws IOException {
		String name = null;
		String line = Utilities.findLine(page, "<title>");
		if (Utilities.isEmpty(line)) {
			log.error("Company name not found, code = " + stockCode);
			return null;
		}
		name = Utilities.cutSubString(new StringBuffer(page), "<title>", "(");
		return name;
	}

	// 从股票详细页面解析资产负债表URL
	private String parseReportUrl(String page, String keyword)
			throws IOException {
		String line = Utilities.findLine(page, keyword);
		if (Utilities.isEmpty(line))
			return null;
		StringBuffer strBuffer = new StringBuffer(line);
		String url = Utilities.cutSubString(strBuffer, "<a href=\"", "\" id=");
		if (Utilities.isEmpty(url))
			return null;
		return url;
	}

	// 解析资产负债表页面，取出历史资产负债记录列表，并对历史记录逐条下载
	private void parseReportPage(Company company, int reportKind,
			String reportUrl) throws MalformedURLException, IOException {
		Utilities.verifyParam("reportUrl", reportUrl);
		String url = reportUrl.replaceAll(PLACEHOLDER_STOCK_CODE, stockCode);
		String page = new String(WebPageCrawler.DownLoadPages(url));
		//log.debug(page);
		String line = Utilities.findLine(page, "年年度");
		if (Utilities.isEmpty(line))
			return;
		line = line.trim();
		StringBuffer strBuffer = new StringBuffer(line);
		// cut prefix
		Utilities.cutBefore(strBuffer, "dateurl=\"");
		String reportHistUrl = Utilities.cutBefore(strBuffer, "\"");
		// 遍历历史记录中每个季度的资产负债表页面
		int i = 0;
		while (IS_TEST ? i++ < 1 : true) {
			String dateStr = Utilities.cutSubString(strBuffer, "['", "',");
			// log.debug(dateStr);
			if (Utilities.isEmpty(dateStr))
				break;
			Calendar calendar = Calendar.getInstance();
			String[] dateElements = dateStr.split("\\.");
			// log.debug(" Array: " + Arrays.toString(dateElements));
			if (dateElements == null || dateElements.length < 3)
				continue;
			try {
				// log.debug("company: " + company);
				calendar.set(Integer.parseInt(dateElements[0]),
						Integer.parseInt(dateElements[1]) - 1,
						Integer.parseInt(dateElements[2]));
				Date date = new Date(calendar);
				CompHist compHist = company.getSituation(date);
				if (compHist == null) {
					// log.debug("compHist == null date = " + date);
					compHist = new CompHist(company, date);
					company.addSituation(date, compHist);
				}
				parseReportHist(reportKind, reportHistUrl + dateStr, compHist);
			} catch (NumberFormatException e) {
				log.error("Calendar number format error. ", e);
			}
			try {
				Thread.sleep(BREAK_TIME);
			} catch (InterruptedException e) {
				log.debug(e.getMessage());
			}
		}
	}

	// 从资产负债页面中搜索包含资产负债表信息的数据
	private void parseReportHist(int reportKind, String reportHistUrl,
			CompHist compHist) throws MalformedURLException, IOException {
		Utilities.verifyParam("reportHistUrl", reportHistUrl);
		String page = new String(WebPageCrawler.DownLoadPages(reportHistUrl));
		String line = Utilities.findLine(page,
				reportKind == REPORT_CASH_FLOW ? FISCAL_YEAR_CASH_FLOW
						: FISCAL_YEAR);
		if (Utilities.isEmpty(line))
			return;
		StringBuffer strBuffer = new StringBuffer(line.trim());
		FinancialStatements report = null;
		switch (reportKind) {
		case REPORT_BALANCE_SHEET:
			report = compHist.getBalance();
			break;
		case REPORT_CASH_FLOW:
			report = compHist.getCashFlow();
			break;
		case REPORT_INCOME_STATEMENT:
			report = compHist.getIncomeStatement();
			break;
		default:
			throw new IllegalArgumentException("Illegal report kind: "
					+ reportKind);
		}
		parseReportSheet(strBuffer, report);
		// log.debug(report);
	}

	// 解析资产负债表
	private void parseReportSheet(StringBuffer strBuffer,
			FinancialStatements report) {
		while (true) {
			String name = Utilities.cutSubString(strBuffer,
					"<div class='tishi'><strong>", "</strong></div>");
			if (Utilities.isEmpty(name))
				break;
			if (strBuffer.indexOf("<div class='tishi'><strong>") == strBuffer
					.indexOf("<div class='tishi'>")) {
				report.put(name, 0);
				continue;
			}
			String value = Utilities.cutSubString(strBuffer,
					"<div class='tishi'>", "</div>");
			if (Utilities.isEmpty(value))
				break;
			value = value.replace(",", "");
			double doubleValue = 0;
			try {
				doubleValue = Double.valueOf(value);
			} catch (NumberFormatException e) {
				// log.debug(name + " number format error： " + value);
			}
			report.put(name, doubleValue);
			// log.debug("put " + name + " : " + value);
		}
	}

	public String getStockUrl() {
		return stockUrl;
	}

	public void setStockUrl(String stockUrl) {
		Utilities.verifyParam("stockUrl", stockUrl);
		this.stockUrl = stockUrl;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		Utilities.verifyParam("stockCode", stockCode);
		this.stockCode = stockCode;
	}

}
