package com.klaymanlei.stocrawler.dataparser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.utils.Utilities;
import com.klaymanlei.utils.WebPageCrawler;

// 从上海证券交易所网站上抓取上证股票编号及名称列表
public class ShanghaiStockExchangeParser {

	public static Logger log = Logger
			.getLogger(ShanghaiStockExchangeParser.class);
	private static final String KEY_FIND_STOCK_LIST = "sseportal/webapp/datapresent/SSEQueryListCmpAct";
	private static final boolean IS_TEST = Constants.IS_TEST; // true: 每页只抓取1家公司; false: 抓取全部公司

	private String url = "http://www.sse.com.cn";
	private String subUrl = "/sseportal/webapp/datapresent/SSEQueryStockInfoAct?keyword=&reportName=BizCompStockInfoRpt&PRODUCTID=&PRODUCTJP=&PRODUCTNAME=&CURSOR=1";

	public List<Company> crawlStockList() throws MalformedURLException,
			IOException {
		List<Company> list = new ArrayList<Company>();
		String nextPageUrl = url + subUrl;
		int i = 0;
		while (nextPageUrl != null) {
			log.debug("parsing page " + ++i);
			String page = new String(WebPageCrawler.DownLoadPages(nextPageUrl));
			parseStockList(page, list);
			nextPageUrl = parseNextPageUrl(page);
			if (IS_TEST)
				break;
		}
		//log.debug(list);
		return list;
	}

	private void parseStockList(String page, List<Company> list)
			throws IOException {
		String line;
		StringBuffer pageBuffer = new StringBuffer(page);
		int i = 1;
		while ((line = Utilities.findLine(pageBuffer.toString(),
				KEY_FIND_STOCK_LIST)) != null) {

			try {
				Thread.sleep(Constants.PARSE_BREAK_TIME);
			} catch (InterruptedException e) {
			}

			pageBuffer.delete(0, pageBuffer.indexOf(line));
			StringBuffer strBuffer = new StringBuffer(line);
			Utilities.cutBefore(strBuffer, ">");
			String stockUrl = Utilities.cutSubString(strBuffer, "<a href=\"",
					"\" >");
			try {
				Company company = parseStockPage(url + stockUrl);
				if (!list.contains(company))
					list.add(company);
				log.debug(i++ + " " + company.getStock().getCode());
			} catch (MalformedURLException e) {
				log.error("Error during parse stock " + stockUrl, e);
			} catch (IOException e) {
				log.error("Error during parse stock " + stockUrl, e);
			}
			pageBuffer.delete(0, line.length());
			if (IS_TEST)
				break;
		}
	}

	private Company parseStockPage(String stockUrl)
			throws MalformedURLException, IOException {
		String page = new String(WebPageCrawler.DownLoadPages(stockUrl));
		//log.debug(page);
		String[] stockName;
		try {
			stockName = parseName(page);
		} catch (NullPointerException e) {
			log.error("NullPointerException occured during parsing " + stockUrl);
			throw e;
		}
		String industry = parseIndustry(page);
		Stock stock = new Stock(stockName[1]);
		stock.setMarket(Constants.EXCHANGE_NAME_SHANGHAI);
		Company company = new Company(stock);
		company.setIndustry(industry);
		company.setName(stockName[0]);
		//log.debug(company);
		return company;
	}

	private String[] parseName(String page) throws IOException {
		String line = Utilities.findLine(page, "<span class=\"pagetitle\">");
		StringBuffer strBuffer = new StringBuffer(line);
		String str = Utilities.cutSubString(strBuffer, "<span class=\"pagetitle\">",
				"<br>");
		String[] stockName = str.split(" ");
		if (stockName == null || stockName.length < 2)
			throw new NullPointerException("StockName is too short: " + str);
		return stockName;
	}

	private String parseIndustry(String page) throws IOException {
		StringBuffer pageBuffer = new StringBuffer(page);
		String line = Utilities.findLine(page, "门类/大类/中类");
		Utilities.cutBefore(pageBuffer, line);
		String industry = Utilities.cutBefore(pageBuffer, "</td>");
		StringBuffer industryBuffer = new StringBuffer(industry.trim());
		Utilities.cutBefore(industryBuffer, "<td >");
		int index;
		while ((index = industryBuffer.indexOf("\r")) >= 0) {
			industryBuffer.deleteCharAt(index);
		}
		while ((index = industryBuffer.indexOf("\n")) >= 0) {
			industryBuffer.deleteCharAt(index);
		}
		//Utilities.cutBefore(industryBuffer, "/-");
		//log.debug(industryBuffer);
		return industryBuffer.toString();
	}
	
	private String parseNextPageUrl(String page) throws IOException {
		String line = Utilities.findLine(page, "下一页");
		if (Utilities.isEmpty(line))
			return null;
		String nextPageSubUrl = Utilities.cutSubString(new StringBuffer(line), "<a href=\"",
				"\">");
		return url + nextPageSubUrl;
	}
}
