package com.klayman.invest.stock.bean;

import java.util.HashMap;
import java.util.Map;

public class Asset {
	private final double INIT;
	private double asset = 0;
	private double cash = 0;
	private Map<String, Integer> stocks = new HashMap<String, Integer>();

	public Asset(double init) {
		this.INIT = init;
	}
	
	public double getAsset() {
		return asset;
	}
	public void setAsset(double asset) {
		this.asset = asset;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getINIT() {
		return INIT;
	}
	public Integer removeShares(String code) {
		return stocks.remove(code);
	}
	public void putShares(String code, int shares) {
		stocks.put(code, shares);
	}
}
