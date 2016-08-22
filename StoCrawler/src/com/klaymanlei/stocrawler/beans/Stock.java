package com.klaymanlei.stocrawler.beans;

import java.util.TreeMap;

import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class Stock {

	private final String code;
	private String name;
	private String market;
	private final TreeMap<Date, StoHist> history = new TreeMap<Date, StoHist>();
	private long mktCap; //市值
	private double pe;  //市盈率
	private double eps;  //每股收益
	private long shares;  //股本
	private double bookValue; //市净率
	
	public Stock(String code) {
		super();
		Utilities.verifyParam("code", code);
		this.code = code;
	}

	public boolean addSituation (Date date, StoHist hist) {
		Utilities.verifyParam("date", date);
		Utilities.verifyParam("hist", hist);
		return history.put(date, hist) == null;
	}

	public StoHist getLatestSituation () {
		if (Utilities.isEmpty(history))
			return null;
		return history.lastEntry().getValue();
	}

	public void cleanHist() {
		this.history.clear();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public long getMktCap() {
		return mktCap;
	}

	public void setMktCap(long mktCap) {
		this.mktCap = mktCap;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public long getShares() {
		return shares;
	}

	public void setShares(long shares) {
		this.shares = shares;
	}

	public double getBookValue() {
		return bookValue;
	}

	public void setBookValue(double bookValue) {
		this.bookValue = bookValue;
	}

	public TreeMap<Date, StoHist> getHistory() {
		return new TreeMap<Date, StoHist>(history);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		Stock other = (Stock) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Stock [code=" + code + ", name=" + name + ",\n history="
				+ history + ", mktCap=" + mktCap + ", pe=" + pe + ", eps="
				+ eps + ", shares=" + shares + ", bookValue=" + bookValue + "]";
	}
	
}
