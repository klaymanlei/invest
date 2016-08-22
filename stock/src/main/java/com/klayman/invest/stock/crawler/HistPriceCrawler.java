package com.klayman.invest.stock.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.utils.http.HttpCrawler;

public class HistPriceCrawler {
	private static final Logger LOG = LoggerFactory
			.getLogger(HistPriceCrawler.class);
//	private static final int SS_STOCK_START = 600000;
	private static final int SS_STOCK_START = 000000;
	private static final int SS_STOCK_END = 603998;

	public static void main(String[] args) {
		List<String> failedUrl = new ArrayList<String>();
		String path = "data/newprice/";
		
		List<String> filenames = new ArrayList<String>();
		for (File c : new File("data/stockprice/").listFiles()) {
			filenames.add(c.getName());
		}
		
//		for (int code = SS_STOCK_START; code <= SS_STOCK_END; code++) {
		for (String name : filenames) {
			String code = name.substring(2, name.length());
//			URL url = null;
//			BufferedReader reader = null;
//			CSVParser parser = null;
			if (!filenames.contains("SS" + code) && !filenames.contains("SZ" + code)) {
				continue;
			}

			try {
				//http://ichart.yahoo.com/table.csv?s=600016.ss&a=11&b=1&c=2015&d=1&e=30&f=2016&g=d&ignore=.csv
				//String url = "http://table.finance.yahoo.com/table.csv";
				String url = "http://ichart.yahoo.com/table.csv";
				//String param = "s=" + code + ".ss";
				String param = null;
				if (filenames.contains("SS" + code) && !filenames.contains("SZ" + code)) {
					param = "s=" + code + ".ss&a=1&b=1&c=2016&d=1&e=20&f=2016&g=d&ignore=.csv";
				}else {
					param = "s=" + code + ".sz&a=1&b=1&c=2016&d=1&e=20&f=2016&g=d&ignore=.csv";
				}
				String data = null;
				boolean retry = true;
				int retryTimes = 0;
				while (retry) {
					try {
						retryTimes++;
						retry = false;
						data = HttpCrawler.sendGet(url, param, null);
					} catch (Exception e) {
//						LOG.error(e.getMessage());
						if (retryTimes < 3)
							retry = true;
						else 
							throw e;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
				String filepath = path + "SS" + code;
				if (!filenames.contains("SS" + code) && filenames.contains("SZ" + code))
					filepath = path + "SZ" + code;
				File file = new File(filepath);
				if (file.exists())
					file.delete();
				file.createNewFile();

				FileUtils.write(file, data, true);
				LOG.info(code + " OK");

				// url = new URL(
				// "http://table.finance.yahoo.com/table.csv?s=" + code +
				// ".ss");
				// reader = new InputStreamReader(new BOMInputStream(
				// url.openStream()), "UTF-8");
				//
				// parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
				// for (final CSVRecord r : parser) {
				// StockRecord record = new StockRecord();
				// record.setCode("ss" + code);
				// record.setDate(r.get("Date"));
				// record.setOpen(Double.parseDouble(r.get("Open")));
				// record.setHigh(Double.parseDouble(r.get("High")));
				// record.setLow(Double.parseDouble(r.get("Low")));
				// record.setClose(Double.parseDouble(r.get("Close")));
				// record.setVolume(Long.parseLong(r.get("Volume")));
				// JSONObject o = JSONObject.fromObject(record);
				// FileUtils.write(file, o.toString());
				// LOG.info(o.toString());
				// }
			} catch (Exception e) {
				LOG.info(code + " failed because " + e.getMessage());
				failedUrl.add("list.add(\"" + code + "\");");
				
				//LOG.error("", e);
			} finally {
//				try {
//					parser.close();
//				} catch (Exception e) {
//				}
//				try {
//					reader.close();
//				} catch (Exception e) {
//				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		for (String c : failedUrl) {
			System.out.println(c);
		}
	}

}
