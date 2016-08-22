package com.klaymanlei.stocrawler.persistence.hibernate4.model;

import java.util.Date;

public class StockPriceHist {
	
	private int id = -1;
	private String code;
	private Date date;
	private double open = -1;
	private double close = -1;
	private double high = -1;
	private double low = -1;
	private long volume = -1;
	private long mktCap = -1;
	private double pe = -1;
	private double eps = -1;
	private long shares = -1;
	private double bookValue = -1;
	
	public StockPriceHist() {
	}
	
	public StockPriceHist(String code, Date date) {
		this.code = code;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
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

	@Override
	public String toString() {
		return "StockPriceHist [id=" + id + ", code=" + code + ", date=" + date
				+ ", open=" + open + ", close=" + close + ", high=" + high
				+ ", low=" + low + ", volume=" + volume + ", mktCap=" + mktCap
				+ ", pe=" + pe + ", eps=" + eps + ", shares=" + shares
				+ ", bookValue=" + bookValue + "]";
	}
	
}
