package com.klaymanlei.stocrawler.beans;

import java.util.Map;

import com.klaymanlei.utils.Date;

public interface FinancialStatements {

	public int getReportKind();

	public void put(String item, double value);

	public Map<String, Double> get();

	public String getCode();

	public Date getDate();

}
