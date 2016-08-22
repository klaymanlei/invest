package com.klayman.invest.stock.preproc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Pb;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;

public class DataPreprocessor {
	private static final Logger LOG = LoggerFactory.getLogger(DataPreprocessor.class);
//	private static final String PB_ITEM = "每股净资产";
//	private static final String OUT_PATH = "data/result/pb/";
	private static final String PE_ITEM = "每股收益";
	private static final String OUT_PATH = "data/result/pe/";
	private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		calPb(PE_ITEM);
	}

	public static void calPb(String item) {
		Map<String, List<String>> icbMap = new HashMap<String, List<String>>();
		DataReader.readIcb(icbMap);
		TreeMap<String, Pb> datePb = new TreeMap<String, Pb>();
		int i = 1;
		for (String icb : icbMap.keySet()) {
			LOG.info("正在计算" + icb + " (" + (i++) + "/" + icbMap.size() + ")");
			datePb.clear();
			List<String> codes = icbMap.get(icb);
			calPb(codes, datePb, item);
			LOG.info("开始写入" + icb);
//			int j = 1;
//			for (String dateCode : datePb.keySet()) {
//				/**************************** 输出PE计算结果 ****************************/
//				DataReader
//						.print(OUT_PATH + icb + ".csv",
//								dateCode + "," + datePb.get(dateCode).getPrice() + ","
//										+ datePb.get(dateCode).getBookValue() + "," + datePb.get(dateCode).getPb(),
//						true);
//				/**************************** 输出PE计算结果 ****************************/
//				if (j % 1000 == 0)
//					LOG.info("已写入" + j + "/" + datePb.size() + "行");
//				j++;
//			}
		}
	}

	public static void calPb(List<String> codes, TreeMap<String, Pb> datePb, String item) {
		int i = 1;
		for (String code : codes) {
			datePb.clear();
			LOG.info("正在计算该行业中的" + code + " (" + (i++) + "/" + codes.size() + ")");
			calPb(code, datePb, item);
			int j = 1;
			DataReader
			.print(OUT_PATH + code + ".csv",
					"日期,代码,价格,每股净资产,PB",
							false);
			for (String dateCode : datePb.keySet()) {
				DataReader
						.print(OUT_PATH + code + ".csv",
								dateCode + "," + datePb.get(dateCode).getPrice() + ","
										+ datePb.get(dateCode).getBookValue() + "," + datePb.get(dateCode).getPb(),
						true);
				if (j % 1000 == 0)
					LOG.info("已写入" + j + "/" + datePb.size() + "行");
				j++;
			}
		}
	}

	public static void calPb(String code, TreeMap<String, Pb> datePb, String item) {
		TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
		TreeMap<String, Double> dateValue = new TreeMap<String, Double>();
		init(code, datePrice, dateValue, item);
		if (dateValue == null || dateValue.size() == 0)
			return;
		String reportDate = dateValue.firstKey();
		try {
			long reportDateLong = LONG_DATE_FORMAT.parse(reportDate).getTime();
			long nextReportDateLong = LONG_DATE_FORMAT.parse(dateValue.higherKey(reportDate)).getTime();
			for (String date : datePrice.keySet()) {
				long priceDateLong = LONG_DATE_FORMAT.parse(date).getTime();
				if (priceDateLong < reportDateLong)
					continue;
				while (priceDateLong > nextReportDateLong) {
					reportDate = dateValue.higherKey(reportDate);
					reportDateLong = LONG_DATE_FORMAT.parse(reportDate).getTime();
					String next = dateValue.higherKey(reportDate);
					if (next != null)
						nextReportDateLong = LONG_DATE_FORMAT.parse(dateValue.higherKey(reportDate)).getTime();
					else 
						nextReportDateLong = System.currentTimeMillis();
				}
				double assetPerShare = dateValue.get(reportDate);
				double pbValue = assetPerShare == 0 ? 100000 : datePrice.get(date).getClose() / assetPerShare;
				Pb pb = new Pb();
				pb.setPrice(datePrice.get(date).getClose());
				pb.setBookValue(assetPerShare);
				pb.setPb(pbValue);
				if (pbValue > 0)
					datePb.put(date + "," + code, pb);
			}
		} catch (ParseException e) {
			LOG.error("", e);
		}
	}

	public static void calPb1(String code, TreeMap<String, Pb> datePb, String item) {
		TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
		TreeMap<String, Double> dateValue = new TreeMap<String, Double>();
		init(code, datePrice, dateValue, item);
		if (dateValue == null || dateValue.size() == 0)
			return;
		String datePointer = dateValue.firstKey();
		if (datePointer == null)
			return;

		long timePointer = 0;

		while ((datePointer = dateValue.higherKey(datePointer)) != null) {
			try {
				timePointer = LONG_DATE_FORMAT.parse(datePointer).getTime();
			} catch (ParseException e) {
				continue;
			}
			for (String date : datePrice.keySet()) {
				try {
					long time = LONG_DATE_FORMAT.parse(date).getTime();
					if (((timePointer - time) / 1000 / 3600 / 24) < 30
							&& ((timePointer - time) / 1000 / 3600 / 24) > -30) {
						// 计算最近三个月的每股利润
						// double profitsPerShare = calProfitsPerShare(
						// datePointer, dateValues);
						double assetPerShare = dateValue.get(datePointer);
						double pbValue = assetPerShare == 0 ? 100000 : datePrice.get(date).getClose() / assetPerShare;
						Pb pb = new Pb();
						pb.setPrice(datePrice.get(date).getClose());
						pb.setBookValue(assetPerShare);
						pb.setPb(pbValue);
						if (pbValue > 0)
							datePb.put(date + "," + code, pb);
					}
				} catch (ParseException e) {
					LOG.error("", e);
				}
			}
		}
	}

	private static void init(String code, TreeMap<String, StockRecord> datePrices, TreeMap<String, Double> dateValues, String item) {
		DataReader.readPrice(DataReader.completeCode(code), datePrices);
		DataReader.readReport(DataReader.completeCode(code), item, dateValues);
	}
}
