package com.klaymanlei.stocrawler;

import java.util.Calendar;

public class Constants {

	public static final long PARSE_BREAK_TIME = 1000;
	public static final String PLACEHOLDER_STOCK_CODE = "<STOCK_CODE>";
	public static final boolean IS_TEST = false; // 被HexunParser,
													// ShanghaiStockExchangeParser,
													// StockHistParser,
													// DataCap使用

	public static final int REPORT_BALANCE_SHEET = 0;
	public static final int REPORT_INCOME_STATEMENT = 1;
	public static final int REPORT_CASH_FLOW = 2;

	public static final String KIND_BALANCE_SHEET = "balance";
	public static final String KIND_INCOME_STATEMENT = "income_statement";
	public static final String KIND_CASH_FLOW = "cash_flow";

	public static final String EXCHANGE_NAME_SHANGHAI = "上海证券交易所";
	public static final String EXCHANGE_NAME_SHENZHEN = "深圳证券交易所";

	public static final String CURRENT_ASSETS = "流动资产合计";
	public static final String CURRENT_LIABILITIES = "流动负债合计";

	public static final int MACD_SHORT_PERIOD = 12;
	public static final int MACD_LONG_PERIOD = 26;	
	public static final int MACD_SIGNAL_PERIOD = 9;
	
	private Constants() {
	}
}
