package com.klayman.invest.stock.crawler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.StockUtils;
import com.klayman.utils.http.HttpCrawler;

public class DailyPriceCrawler {

	private static final String URL = "http://hq.sinajs.cn/list=";
	private static final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss SSS");

	public void start() throws IOException, ParseException {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String today = StockUtils.MS_FORMAT.format(now.getTime());
		String currentSeason = StockUtils.getSeason(today);
		System.out.println("current season is " + currentSeason);
		File out = new File("/home/hadoop/data/seasondata/" + currentSeason);
		File[] stocks = new File("/home/hadoop/data/stockprice").listFiles();
		List<String> outLines = new ArrayList<String>();
		int count = 0;
		for (File file : stocks) {
			count++;
			String name = file.getName();
			String code = name.substring(2);
			String url = null;
			if (name.startsWith("SS")) {
				url = URL + "sh" + code;
			} else if (name.startsWith("SZ")) {
				url = URL + "sz" + code;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			System.out.println(f.format(Calendar.getInstance().getTime()) + " Get (" + count + "/" + stocks.length + ")" + url);
			String data = HttpCrawler.sendGet(url, "", null);
			StockRecord p = parse(code, data);
			if (p == null)
				continue;
//			List<String> lines = FileUtils.readLines(file);
//			if (lines.get(lines.size() - 1).startsWith(p.getDate())) {
//				continue;
//			}
			// Date,Open,High,Low,Close,Volume,Adj Close

			String str = code + "," + p.getDate() + "," + p.getOpen() + "," + p.getHigh() + "," + p.getLow() + "," + p.getClose()
					+ "," + p.getVolume() + "," + p.getClose();
			outLines.add(str);
		}
		FileUtils.writeLines(out, outLines, true);
	}

	private StockRecord parse(String code, String data) {
		String str = data.substring(data.indexOf("\"") + 1, data.lastIndexOf("\""));
		String[] props = str.split(",");
		if (props.length < 31)
		{
			System.out.println(code + ": " + data);
			return null;
		}
		StockRecord record = new StockRecord();
		record.setCode(code);
		record.setOpen(Double.parseDouble(props[1]));
		record.setHigh(Double.parseDouble(props[4]));
		record.setLow(Double.parseDouble(props[5]));
		record.setClose(Double.parseDouble(props[3]));
		record.setDate(props[30]);
		record.setVolume(Long.parseLong(props[8]));
		return record;
	}

	public static void main(String[] args) {
		DailyPriceCrawler crawler = new DailyPriceCrawler();
		try {
			crawler.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
