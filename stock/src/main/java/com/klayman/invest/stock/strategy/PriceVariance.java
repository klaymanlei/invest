package com.klayman.invest.stock.strategy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;

public class PriceVariance {
	
	private final String ICB;
	private final String CODE;
	private final String OUT_PATH;
	private List<String> codes = null;

	public PriceVariance(String icb, String code) {
		ICB = icb;
		CODE = code;
		OUT_PATH = "data/result/pricevariance/" + ICB + "/";
		Map<String, List<String>> icbCodes = new HashMap<String, List<String>>();
		DataReader.readIcb(icbCodes);
		codes = icbCodes.get(ICB);
		if (CODE != null) {
			if (codes.contains(CODE)) {
				codes.clear();
				codes.add(CODE);
			} else {
				return;
			}
		}
	}

	public PriceVariance(String icb, List<String> codes) {
		ICB = icb;
		CODE = null;
		OUT_PATH = "data/result/pricevariance/" + ICB + "/";
		this.codes = codes;
	}

	public TreeMap<String, List<Operation>> start() {
		TreeMap<String, Map<String, StockRecord>> dateCodePrice = new TreeMap<String, Map<String, StockRecord>>();
		
		TreeMap<String, double[]> dateVariance = new TreeMap<String, double[]>();
		init(codes, dateCodePrice);
		for (String date : dateCodePrice.keySet()) {
			Map<String, StockRecord> codePrice = dateCodePrice.get(date);
			if (codePrice.size() == 0)
				continue;
			double avg = 0.0;
			double sum = 0.0;
			for (String code : codePrice.keySet()) {
				sum += codePrice.get(code).getClose();
			}
			avg = sum / codePrice.size();
			double variance = 0.0;
			StringBuffer data = new StringBuffer();
			data.append(date);
			for (String code : codes) {
				data.append(",");
				if (codePrice.get(code) == null)
					continue;
				data.append(codePrice.get(code).getClose());
				variance += Math.pow((codePrice.get(code).getClose() - avg), 2);
			}
			variance /= codePrice.size();
			double[] rs = {avg, variance};
				data.append("," + avg + "," + variance + "\n");
			try {
				FileUtils.write(new File(OUT_PATH + "out.csv"), data.toString(), true);
			} catch (IOException e) {
			}
			dateVariance.put(date.substring(0, 7), rs);
		}
		
		for (String date: dateVariance.keySet()) {
			System.out.println(date + "\t" + dateVariance.get(date)[0] + "\t" + dateVariance.get(date)[1]);
		}
		return null;
	}
	
	public static void main(String[] args) {
		Map<String, List<String>> icbMap = new HashMap<String, List<String>>();
		DataReader.readIcb(icbMap);

		String icb = "ÒøÐÐÒµ£¨III£©";
		// for (String icb : icbMap.keySet()) {
		PriceVariance strategy = new PriceVariance(icb, (String) null);
		strategy.start();
		// }
	}
	
	private void init(List<String> codes,
			TreeMap<String, Map<String, StockRecord>> dateCodePrice) {
		for (String code : codes) {
			TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
			DataReader.readPrice(DataReader.completeCode(code), datePrice);
			for (String date : datePrice.keySet()) {
				Map<String, StockRecord> codePrice = dateCodePrice.get(date);
				if (codePrice == null) {
					codePrice = new HashMap<String, StockRecord>();
					dateCodePrice.put(date, codePrice);
				}
				codePrice.put(code, datePrice.get(date));
			}
		}
	}
}
