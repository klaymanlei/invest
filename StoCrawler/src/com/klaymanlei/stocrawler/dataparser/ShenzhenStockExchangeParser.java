package com.klaymanlei.stocrawler.dataparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.utils.FileHandler;
import com.klaymanlei.utils.Utilities;

// 从上海证券交易所网站上抓取上证股票编号及名称列表
public class ShenzhenStockExchangeParser {

	public static Logger log = Logger
			.getLogger(ShenzhenStockExchangeParser.class);
	private static final boolean IS_TEST = Constants.IS_TEST; // true: 每页只抓取1家公司; false: 抓取全部公司
	private static final String STOCK_LIST_PATH = "ref/Table.htm";
	
	public List<Company> crawlStockList() throws IOException {
		List<Company> list = new ArrayList<Company>();
		FileHandler file = new FileHandler(STOCK_LIST_PATH);
		file.openRead();
		String line;
		StringBuffer buff = null;
		boolean appendBegin = false;
		while ((line = file.read()) != null) {
			//log.debug(line);
			if (line.trim().startsWith("<tr")) {
				appendBegin = true;
				buff = new StringBuffer(line);
			}
			if (appendBegin)
				buff.append(line);
			if (line.trim().startsWith("</tr>")) {
				Company company = parseStock(buff);
				log.debug(company);
				if (company != null)
					list.add(company);
				appendBegin = false;
				if (IS_TEST)
					break;
			}
		}
		//log.debug(list);
		return list;
	}

	private Company parseStock(StringBuffer buff) {
		String[] data = new String[3];
		while (buff.indexOf("<td") >= 0) {
			Utilities.cutBefore(buff, "<td");
			data[0] = Utilities.cutSubString(buff, ">", "<");
			data[0].replace("\\r", "");
			data[0].replace("\\n", "");
			Utilities.cutBefore(buff, "<td");
			Utilities.cutBefore(buff, "<td");
			data[1] = Utilities.cutSubString(buff, ">", "<");
			data[1].replace("\\r", "");
			data[1].replace("\\n", "");
			Utilities.cutBefore(buff, "<td");
			data[2] = Utilities.cutSubString(buff, ">", "<");
			data[2].replace("\\r", "");
			data[2].replace("\\n", "");
		}
		if (Utilities.isEmpty(data[0]) || Utilities.isEmpty(data[1]))
			return null;
		Stock stock = new Stock(data[0]);
		stock.setMarket(Constants.EXCHANGE_NAME_SHENZHEN);
		Company company = new Company(stock);
		company.setName(data[1]);
		company.setIndustry(data[2]);
		return company;
	}
}
