package com.klayman.invest.stock.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;
import com.klayman.invest.stock.util.TradeSimulator;

public class PeAndPb4Ss50 {
	private static final Logger LOG = LoggerFactory
			.getLogger(PeAndPb4Ss50.class);

	public static void main(String[] args) {
		System.out.print("\tStock Count\tMin Asset\tAsset\tMax Asset\tFirst Trade");
		for (int i = 2003; i < 2016; i++) {
			for (int j = 1; j < 13; j++) {
				System.out.print("\t" + i + "-" + (j < 10 ? "0" : "") + j);
			}
		}
		
		System.out.println();
		List<String> codes = DataReader.initSs50();
		Map<String, TreeMap<String, StockRecord>> codeDatePrice = new HashMap<String, TreeMap<String, StockRecord>>();
		Map<String, List<String>> icbMap = new HashMap<String, List<String>>();
		TreeMap<String, List<Operation>> dateOp = new TreeMap<String, List<Operation>>();
		DataReader.readIcb(icbMap);
		for (String code : codes) {
			TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
			DataReader.readPrice(DataReader.completeCode(code), datePrice);
			codeDatePrice.put(code, datePrice);
		}

		for (String icb : icbMap.keySet()) {
			List<String> icbCodes = icbMap.get(icb);
			for (String code : codes) {
				if (icbCodes.contains(code)) {
					TreeMap<String, List<Operation>> pbOps = pb(icb, code);
					for (String date : pbOps.keySet()) {
						List<Operation> opList = dateOp.get(date);
						if (opList == null) {
							opList = new ArrayList<Operation>();
							dateOp.put(date, opList);
						}
						opList.addAll(pbOps.get(date));
					}
				}
			}
		}

		print("pb", dateOp, codeDatePrice);

//		dateOp.clear();
//
//		for (String icb : icbMap.keySet()) {
//			List<String> icbCodes = icbMap.get(icb);
//			for (String code : codes) {
//				if (icbCodes.contains(code)) {
//					TreeMap<String, List<Operation>> peOps = pe(icb, code);
//					for (String date : peOps.keySet()) {
//						List<Operation> opList = dateOp.get(date);
//						if (opList == null) {
//							opList = new ArrayList<Operation>();
//							dateOp.put(date, opList);
//						}
//						opList.addAll(peOps.get(date));
//					}
//				}
//			}
//		}
//
//		print("pe", dateOp, codeDatePrice);
	}

	public static void print(String type,
			TreeMap<String, List<Operation>> dateOp,
			Map<String, TreeMap<String, StockRecord>> codeDatePrice) {
		TradeSimulator s = new TradeSimulator(300000);
		s.setPerBuy(10000d);
		s.trade(codeDatePrice, dateOp, "上证50");
//		LOG.info("上证50\t" + type + "\t"
//				+ (dateOp.size() > 0 ? dateOp.firstKey() : "") + "\t"
//				+ s.getINIT() + "\t" + s.getAsset());
	}

	public static TreeMap<String, List<Operation>> pb(String icb, String code) {
		IcbPbStrategy strategy = new IcbPbStrategy(icb, code);
		TreeMap<String, List<Operation>> dateOp = strategy.start();
		return dateOp;
	}

	public static TreeMap<String, List<Operation>> pe(String icb, String code) {
		IcbPeStrategy strategy = new IcbPeStrategy(icb, code);
		TreeMap<String, List<Operation>> dateOp = strategy.start();
		return dateOp;
	}

	public static TreeMap<String, List<Operation>> pb(List<String> codes) {
		IcbPbStrategy strategy = new IcbPbStrategy("上证50", codes);
		TreeMap<String, List<Operation>> dateOp = strategy.start();
		return dateOp;
	}

	public static TreeMap<String, List<Operation>> pe(List<String> codes) {
		IcbPeStrategy strategy = new IcbPeStrategy("上证50", codes);
		TreeMap<String, List<Operation>> dateOp = strategy.start();
		return dateOp;
	}

}
