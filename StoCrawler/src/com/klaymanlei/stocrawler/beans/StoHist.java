package com.klaymanlei.stocrawler.beans;

import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class StoHist {

	private final String code;
	private final Date date;
	private double close; // 收盘
	private double open; // 开盘
	private double high; // 最高
	private double low; // 最低
	private long volume; // 成交
	private long mktCap; // 市值
	private double pe; // 市盈率
	private double eps; // 每股收益
	private long shares; // 股本
	private double bookValue; // 市净率

	// MACD

	private double emaShort;
	private double emaLong;
	private double diff;
	private double dea;
	private double macd;

	public StoHist(String code, Date date) {
		super();
		Utilities.verifyParam("No.", code);
		Utilities.verifyParam("date", date);
		this.code = code;
		this.date = date;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
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

	public String getCode() {
		return code;
	}

	public Date getDate() {
		return date;
	}

	public double getBookValue() {
		return bookValue;
	}

	public void setBookValue(double bookValue) {
		this.bookValue = bookValue;
	}

	public double getEmaShort() {
		return emaShort;
	}

	public void setEmaShort(double emaShort) {
		this.emaShort = emaShort;
	}

	public double getEmaLong() {
		return emaLong;
	}

	public void setEmaLong(double emaLong) {
		this.emaLong = emaLong;
	}

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public double getDea() {
		return dea;
	}

	public void setDea(double dea) {
		this.dea = dea;
	}

	public double getMacd() {
		return macd;
	}

	public void setMacd(double macd) {
		this.macd = macd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		StoHist other = (StoHist) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StoHist [no=" + code + ", date=" + date + ", close=" + close
				+ ", open=" + open + ", high=" + high + ", low=" + low
				+ ", volume=" + volume + ", mktCap=" + mktCap + ", pe=" + pe
				+ ", eps=" + eps + ", shares=" + shares + ", bookValue="
				+ bookValue + "]\n";
	}

}
