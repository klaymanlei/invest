package com.klaymanlei.stocrawler.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class IncomeStatement implements FinancialStatements {

	private final Map<String, Double> map = new LinkedHashMap<String, Double>();
	private final String code;
	private final Date date;

	public IncomeStatement(String code, Date date) {
		super();
		Utilities.verifyParam("code", code);
		Utilities.verifyParam("date", date);
		this.code = code;
		this.date = date;
	}

	public void put(String item, double value) {
		Utilities.verifyParam("item", item);
		if (map.containsKey(item))
			return;
		map.put(item, value);
	}

	public Map<String, Double> get() {
		return new LinkedHashMap<String, Double>(map);
	}

	public String getCode() {
		return code;
	}

	public Date getDate() {
		return date;
	}

	public int getReportKind() {
		return Constants.REPORT_INCOME_STATEMENT;
	}

	@Override
	public String toString() {
		return "IncomeStatement [code=" + code + ", calendar=" + date
				+ ", map=" + map + "]";
	}

}
