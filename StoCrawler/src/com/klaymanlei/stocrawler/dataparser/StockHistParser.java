package com.klaymanlei.stocrawler.dataparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;
import com.klaymanlei.utils.WebPageCrawler;

public class StockHistParser {

	private final static Logger log = Logger.getLogger(StockHistParser.class);
	
	public static final long BREAK_TIME = Constants.PARSE_BREAK_TIME;
	public static final String PLACEHOLDER_STOCK_CODE = Constants.PLACEHOLDER_STOCK_CODE;
	private static final boolean IS_TEST = Constants.IS_TEST; // true: 只抓取1条数据; false: 抓取全部数据

	private String stockCode;
	private String stockUrl = "http://ichart.finance.yahoo.com/table.csv?s="
			+ PLACEHOLDER_STOCK_CODE;
	private String stockDateRange = "http://ichart.yahoo.com/table.csv?s=<code>&a=<startMonth>&b=<startDay>&c=<startYear>&d=<endMonth>&e=<endDay>&f=<endYear>&g=d&ignore=.csv";
//	private String stockUrl = "http://table.finance.yahoo.com/table.csv?s=" + PLACEHOLDER_STOCK_CODE;

	/*
	 * 查询历史数据
	 * http://ichart.yahoo.com/table.csv?s=<string>&a=<int>&b=<int>&c=<int>&d=<int>&e=<int>&f=<int>&g=d&ignore=.csv
     * s — 股票名称
     * a — 起始时间，月
     * b — 起始时间，日
     * c — 起始时间，年
     * d — 结束时间，月
     * e — 结束时间，日
     * f — 结束时间，年
     * g — 时间周期。Example: g=w, 表示周期是‘周’。d->‘日’(day), w->‘周’(week)，m->‘月’(mouth)，v->‘dividends only’
     * 一定注意月份参数，其值比真实数据-1。如需要9月数据，则写为08。
     * 
     * 示例
     * 查询浦发银行2010.09.25 – 2010.10.8之间日线数据
     * http://ichart.yahoo.com/table.csv?s=600000.SS&a=08&b=25&c=2010&d=09&e=8&f=2010&g=d
	 */
	public StockHistParser(String stockCode) {
		super();
		Utilities.verifyParam("stockCode", stockCode);
		this.stockCode = stockCode;		
		if (stockCode.startsWith("6"))
			stockUrl = stockUrl.replace(PLACEHOLDER_STOCK_CODE, stockCode + ".ss");
		else if (stockCode.startsWith("0") || stockCode.startsWith("3"))
			stockUrl = stockUrl.replace(PLACEHOLDER_STOCK_CODE, stockCode + ".sz");
		else
			throw new IllegalArgumentException("Illegal stock code: " + stockCode);
	}

	public void parse(Company company, Date start, Date end) throws MalformedURLException,
			IOException {
		if (!stockCode.equals(company.getStock().getCode()))
			throw new IllegalArgumentException("Illegal company (" + company.getStock().getCode() + ") input for stock " + stockCode);
		try {
			Thread.sleep(BREAK_TIME);
		} catch (InterruptedException e) {
		}
//		http://ichart.yahoo.com/table.csv?s=<code>&a=<startMonth>&b=<startDay>&c=<startYear>&d=<endMonth>&e=<endDay>&f=<endYear>&g=d&ignore=.csv
		String url = stockDateRange;
		if (company.getStock().getCode().startsWith("6"))
			url = url.replace("<code>", company.getStock().getCode() + ".ss");
		else if (company.getStock().getCode().startsWith("0") || stockCode.startsWith("3"))
			url = url.replace("<code>", company.getStock().getCode() + ".sz");
		url = url.replace("<startMonth>", Integer.toString(start.getMonth()));
		url = url.replace("<startDay>", Integer.toString(start.getDay()));
		url = url.replace("<startYear>", Integer.toString(start.getYear()));
		url = url.replace("<endMonth>", Integer.toString(end.getMonth()));
		url = url.replace("<endDay>", Integer.toString(end.getDay()));
		url = url.replace("<endYear>", Integer.toString(end.getYear()));
		
		String page = new String(WebPageCrawler.DownLoadPages(url));
		Stock stock = company.getStock();
		Utilities.verifyParam("stock", stock);
		parsePage(page, stock);
//		log.debug(company.getLatestSituation().getBalance());
//		log.debug(stock);
	}

	public void parse(Company company) throws MalformedURLException,
			IOException {
		if (!stockCode.equals(company.getStock().getCode()))
			throw new IllegalArgumentException("Illegal company (" + company.getStock().getCode() + ") input for stock " + stockCode);
		try {
			Thread.sleep(BREAK_TIME);
		} catch (InterruptedException e) {
		}
		String page = new String(WebPageCrawler.DownLoadPages(stockUrl));
		Stock stock = company.getStock();
		Utilities.verifyParam("stock", stock);
		parsePage(page, stock);
//		log.debug(company.getLatestSituation().getBalance());
//		log.debug(stock);
	}

	private void parsePage(String page, Stock stock) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(page));
		try {
			String line = reader.readLine();
			// 忽略表頭行
			while ((line = reader.readLine()) != null) {
				String[] histStrings = line.split(",");
				if (histStrings == null || histStrings.length < 6)
					continue;
				String[] dateStrs = histStrings[0].split("-");
				if (dateStrs == null || dateStrs.length < 3)
					continue;
				Calendar calendar = Calendar.getInstance();
				calendar.set(Integer.valueOf(dateStrs[0]), Integer.valueOf(dateStrs[1]) - 1, Integer.valueOf(dateStrs[2]));
				Date date = new Date(calendar);
				double open = Double.valueOf(histStrings[1]);
				double high = Double.valueOf(histStrings[2]);
				double low = Double.valueOf(histStrings[3]);
				double close = Double.valueOf(histStrings[4]);
				long volume = Long.valueOf(histStrings[5]);
				StoHist hist = new StoHist(stockCode, date);
				hist.setOpen(open);
				hist.setClose(close);
				hist.setHigh(high);
				hist.setLow(low);
				hist.setVolume(volume);
				stock.addSituation(date, hist);
				//log.debug(hist);
				if (IS_TEST)
					break;
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

}
