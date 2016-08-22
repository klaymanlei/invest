package com.klayman.invest.stock.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.klayman.invest.stock.bean.Dividend;
import com.klayman.invest.stock.bean.StockRecord;

public class DivSim {

	public static void main(String[] args) throws IOException, ParseException {
		File dataFile = new File("data/stockprice");
		Map<String, TreeMap<String, Double>> rsmap = new HashMap<String, TreeMap<String, Double>>();
		int i = 0;
		for (File child : dataFile.listFiles()) {
			i++;
			TreeMap<String, StockRecord> map = new TreeMap<String, StockRecord>();
			DataReader.readPrice(child.getName(), map);

			Date start = DateUtility.FORMAT.parse(map.firstKey());
			Date end = DateUtility.FORMAT.parse(map.lastKey());

			File dividend = new File("data/dividend/" + child.getName());

			TreeMap<String, Dividend> divMap = null;
			if (dividend.exists()) {
				divMap = dividend(FileUtils.readLines(dividend),
						child.getName());
			} else {
				continue;
			}
			double rs = sim("2010-01-01", 100000, map, divMap);
		}
	}
	
	private static double sim(String date, double initMoney, TreeMap<String, StockRecord> prices, TreeMap<String, Dividend> divMap) {
		double rs = 0;
		StockRecord start = prices.higherEntry(date).getValue();
		if (start == null)
			return initMoney;
		int hold = (int)((initMoney - initMoney % (start.getOpen() * 100)) / start.getOpen());
		int initHold = hold;
		double money = initMoney - start.getOpen() * hold;
		double profit = 0;
		String d = divMap.higherKey(date);
		while (d != null) {
			Dividend div = divMap.get(d);
			profit += hold * div.getMoney();
			hold = (int) ((div.getShare1() + div.getShare2() + 1) * hold);
			d = divMap.higherKey(d);
		}
		StockRecord end = prices.lastEntry().getValue();
		rs = end.getClose() * hold;
		System.out.println(start.getCode() + "\t" + start.getDate() + "\t" + start.getOpen() + "\t" + initHold + "\t" + profit + "\t" + (hold - initHold) + "\t" + (double)(hold - initHold) / initHold + "\t" + end.getClose());
		return rs + money + profit;
	}
	
	private static TreeMap<String, Dividend> dividend(List<String> lines,
			String code) {
		TreeMap<String, Dividend> divMap = new TreeMap<String, Dividend>();
		for (String line : lines) {
			String[] args = line.split("\\,");
			Dividend div = new Dividend();
			div.setCode(code);
			div.setDate(args[0]);
			div.setMoney(Double.parseDouble(args[1]));
			div.setShare1(Double.parseDouble(args[2]));
			div.setShare2(Double.parseDouble(args[3]));
			divMap.put(div.getDate(), div);
		}
		return divMap;
	}

}
