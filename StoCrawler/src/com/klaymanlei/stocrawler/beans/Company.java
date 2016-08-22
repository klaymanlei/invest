package com.klaymanlei.stocrawler.beans;

import java.util.TreeMap;

import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class Company {

	private final Stock stock;
	private String name;
	private String summary;
	private String industry;
	private final TreeMap<Date, CompHist> history = new TreeMap<Date, CompHist>();

	public Company(Stock stock) {
		super();
		Utilities.verifyParam("stock", stock);
		this.stock = stock;
	}

	public CompHist getSituation(Date date) {
		Utilities.verifyParam("date", date);
		return history.get(date);
	}

	public boolean addSituation(Date date, CompHist hist) {
		Utilities.verifyParam("date", date);
		Utilities.verifyParam("hist", hist);
		return history.put(date, hist) == null;
	}

	public CompHist getLatestSituation() {
		if (Utilities.isEmpty(history))
			return null;
		return history.lastEntry().getValue();
	}

	public TreeMap<Date, CompHist> getHistory() {
		return new TreeMap<Date, CompHist>(history);
	}

	public void cleanHist() {
		this.history.clear();
	}
	
	public Stock getStock() {
		return stock;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Company [name=" + name + ", stock=" + stock.getCode() + ", summary="
				+ summary + ", industry=" + industry + ", history=" + history.size()
				+ "]\n";
	}

}
