package com.klayman.invest.stock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Dividend;
import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;

public class TradeSimulator {

	private static final Logger LOG = LoggerFactory.getLogger(TradeSimulator.class);
	private final double INIT;
	private double asset = 0;
	private double cash = 0;
	private double perBuy = 0;
	private Map<String, Integer> stocks = new HashMap<String, Integer>();

	// 输入起始资金
	public TradeSimulator(double init) {
		INIT = init;
		init();
	}

	public double getINIT() {
		return INIT;
	}

	public double getAsset() {
		return asset;
	}

	public double getCash() {
		return cash;
	}

	public void setPerBuy(Double perBuy) {
		this.perBuy = perBuy;
	}

	public Map<String, Integer> getStocks() {
		return stocks;
	}

	public void trade(Map<String, TreeMap<String, StockRecord>> prices,
			TreeMap<String, List<Operation>> ops, String icb) {

		if (ops == null || prices == null)
			return;
		if (ops.size() == 0 || prices.size() == 0)
			return;
		TreeMap<String, List<Dividend>> divMap = new TreeMap<String, List<Dividend>>();

		for (String code : prices.keySet()) {
			List<Dividend> divList = new ArrayList<Dividend>();
			String completedCode = code;
			if (code.startsWith("6")) {
				completedCode = "SS" + completedCode;
			} else {
				completedCode = "SZ" + completedCode;
			}
			DataReader.readDividend(completedCode, divList);
			for (Dividend d : divList) {
				List<Dividend> divs = divMap.get(d.getDate());
				if (divs == null) {
					divs = new ArrayList<Dividend>();
					divMap.put(d.getDate(), divs);
				}
				divs.add(d);
			}
		}

		// prices是Map<股票代码，TreeMap<时间，价格>>。priceMap是TreeMap<时间，Map<股票代码，价格>>
		TreeMap<String, Map<String, Double>> priceMap = new TreeMap<String, Map<String, Double>>();
		trans(prices, priceMap);
		String divDate = null;
		if (divMap.size() > 0)
			divDate = divMap.firstKey();

		double minAsset = Double.MAX_VALUE;
		double maxAsset = Double.MIN_VALUE;
		TreeMap<String, Double> assetHists = new TreeMap<String, Double>();
		TreeMap<String, Double> cashHists = new TreeMap<String, Double>();
		Map<String, Double> prevPrices = new HashMap<String, Double>();
		
		for (String date : priceMap.keySet()) {
			if (stocks.size() > 0) {
				if (divDate != null) {
					String prevDate = priceMap.lowerKey(date);
					dividend(prevDate, date, divMap);
				}
			}
			Map<String, Double> codePrice = priceMap.get(date);

			// 更新总资产
			asset = cash;
			for (String code : stocks.keySet()) {
				if (codePrice.get(code) == null) {
					//System.out.println(date + "," + code);
					asset += prevPrices.get(code) * stocks.get(code);
				} else {
					prevPrices.put(code, codePrice.get(code));
					asset += codePrice.get(code) * stocks.get(code);
				}
			}
			assetHists.put(date.substring(0, 7), asset);
			cashHists.put(date.substring(0, 7), cash);
			if (asset < minAsset)
				minAsset = asset;
			if (asset > maxAsset)
				maxAsset = asset;

			List<Operation> opList = ops.get(date);
			if (opList == null)
				continue;

			double prevCash = cash;
			for (Operation op : opList) {
				String code = op.getCode();
				Double price = codePrice.get(code);
				prevPrices.put(code, price);
				if (price == null) {
					continue;
				}
				if (op.isBuy()) {
					buy(code, price, date);
				} else {
					sale(code, price, date);
				}
			}
//			if (prevCash < cash) {
//				System.out.println(date + "\tbuy\t1\t" + (cash - prevCash) + "\t" + cash);				
//			}else if (prevCash > cash){
//				System.out.println(date + "\tsell\t-1\t" + (prevCash - cash) + "\t" + cash);
//			}
		}

//		System.out.println(icb + "\t" + prices.size() + "\t" + minAsset + "\t" + asset + "\t" + maxAsset + "\t" + (ops.size() > 0 ? ops.firstKey() : ""));
//		for (String date : assetHists.keySet()) {
//				System.out.println(date + "\t" + assetHists.get(date));
//			
//		}
		
		System.out.print(icb + "\t" + prices.size() + "\t" + minAsset + "\t" + asset + "\t" + maxAsset + "\t" + (ops.size() > 0 ? ops.firstKey() : ""));
		for (int i = 2003; i < 2016; i++) {
			for (int j = 1; j < 13; j++) {
				System.out.print("\t" + assetHists.get(i + "-" + (j < 10 ? "0" : "") + j));
			}
		}
		System.out.println();
		
		System.out.print("\t\t\t\t\t");
		for (int i = 2003; i < 2016; i++) {
			for (int j = 1; j < 13; j++) {
				System.out.print("\t" + cashHists.get(i + "-" + (j < 10 ? "0" : "") + j));
			}
		}
		System.out.println();
	}

	private void buy(String code, double price, String date) {
		int stockIn = (int) (perBuy / price) / 100 * 100;
		double moneyOut = stockIn * price;
		if (cash < moneyOut)
			return;
		cash -= moneyOut;
		Integer shareCount = stocks.remove(code);
		if (shareCount == null) {
			shareCount = 0;
		}
		shareCount += stockIn;
		stocks.put(code, shareCount);
		//System.out.println(date + "\t1\t" + stockIn + "\t" + code + "\t" + price + "\t" + shareCount);
	}

	private void sale(String code, double price, String date) {
		Integer shareCount = stocks.remove(code);
		if (shareCount == null || shareCount < 100) {
			return;
		}
		int stockOut = shareCount == 100 ? 100 : (int) (shareCount / 2) / 100 * 100;
		//int stockOut = shareCount;
		double moneyIn = stockOut * price;
		cash += moneyIn;
		shareCount -= stockOut;
		stocks.put(code, shareCount);
		//System.out.println(date + "\t-1\t" + stockOut + "\t" + code + "\t" + price + "\t" + shareCount);
	}

	// 计算分红配股
	private void dividend(String prevDate, String date,
			TreeMap<String, List<Dividend>> divs) {
		for (String divDate : divs.keySet()) {
			if (divDate.compareTo(prevDate) > 0 && divDate.compareTo(date) <= 0) {
				for (Dividend d : divs.get(divDate)) {
					Integer shareCount = stocks.get(d.getCode());
					if (shareCount == null || shareCount == 0) {
						continue;
					}
					cash += d.getMoney() * shareCount;
					double share1 = d.getShare1() * shareCount;
					double share2 = d.getShare2() * shareCount;
					shareCount += (int) (share1 + share2);
					stocks.put(d.getCode(), shareCount);
				}
			}
		}
	}

	private static void trans(Map<String, TreeMap<String, StockRecord>> prices,
			TreeMap<String, Map<String, Double>> priceMap) {
		if (prices == null || priceMap == null)
			return;
		for (String code : prices.keySet()) {
			TreeMap<String, StockRecord> p = prices.get(code);
			for (String date : p.keySet()) {
				Map<String, Double> codePrice = priceMap.get(date);
				if (codePrice == null) {
					codePrice = new HashMap<String, Double>();
					priceMap.put(date, codePrice);
				}
				codePrice.put(code, p.get(date).getClose());
			}
		}
	}

	public void init() {
		cash = INIT;
		asset = INIT;
		perBuy = INIT / 10;
	}

	@Override
	public String toString() {
		return "TradeSimulator [asset=" + asset + ", cash=" + cash
				+ ", stocks=" + stocks + "]";
	}
}
