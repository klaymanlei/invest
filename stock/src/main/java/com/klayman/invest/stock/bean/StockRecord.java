package com.klayman.invest.stock.bean;

public class StockRecord {
	private String code;
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	private long volume;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		return "StockRecord [code=" + code + ", date=" + date + ", open="
				+ open + ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + "]";
	}
	
}
