package com.klayman.invest.stock.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.utils.http.HttpCrawler;

public class SzHistPriceCrawler {
	private static final Logger LOG = LoggerFactory
			.getLogger(SzHistPriceCrawler.class);
	private static final String SS_STOCK_START = "000000";
	// private static final int SS_STOCK_START = 601767;
//	private static final int SS_STOCK_END = 603998;

	public static void main(String[] args) {
		// Map<Integer, String> failedUrl = new HashMap<Integer, String>();
		String path = "data/stockprice/";

		// List<String> filenames = new ArrayList<String>();
		// for (File c : new File("data/stockprice/").listFiles()) {
		// filenames.add(c.getName());
		// }
		try (Reader ratIn = new FileReader(new File("data/szlist.csv"))) {
		Iterable<CSVRecord> ratios = CSVFormat.EXCEL.parse(ratIn);
		for (CSVRecord item : ratios) {

			// for (int code = SS_STOCK_START; code <= SS_STOCK_END; code++) {
			// URL url = null;
			// BufferedReader reader = null;
			// CSVParser parser = null;
			// if (!filenames.contains("SS" + code))
			// continue;
			String code = item.get(0);
			try {
				if (code == null || "".equals(code)
						|| !StringUtils.isNumeric(code) || code.compareTo(SS_STOCK_START) < 0)
					continue;
				// http://ichart.yahoo.com/table.csv?s=600016.ss&a=11&b=1&c=2015&d=1&e=30&f=2016&g=d&ignore=.csv
				String url = "http://table.finance.yahoo.com/table.csv";
				// String url = "http://ichart.yahoo.com/table.csv";
				String param = "s=" + code + ".sz";
				// String param = "s=" + code +
				// ".ss&a=9&b=17&c=2015&d=1&e=30&f=2016&g=d&ignore=.csv";
				String data = null;
				boolean retry = true;
				int retryTimes = 0;
				while (retry) {
					try {
						retryTimes++;
						retry = false;
						data = HttpCrawler.sendGet(url, param, null);
					} catch (Exception e) {
						LOG.error(e.getMessage());
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
				File file = new File(path + "SZ" + code);
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
				// failedUrl.put(code, e.getMessage());

				// LOG.error("", e);
			} finally {
				// try {
				// parser.close();
				// } catch (Exception e) {
				// }
				// try {
				// reader.close();
				// } catch (Exception e) {
				// }
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		}catch (IOException e) {
			LOG.error("", e);
		}
		// for (int c : failedUrl.keySet()) {
		// LOG.error(c + " failed because " + failedUrl.get(c));
		// }
	}

}
