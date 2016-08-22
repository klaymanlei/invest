package com.klayman.invest.stock.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockUtils {
	public static final SimpleDateFormat MS_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private static final String SEASONS[] = { "-01-01", "-04-01", "-07-01",
			"-10-01" };

	/**
	 * 输入一个日期，返回所属季度的第一天。日期格式为“yyyy-MM-dd”。如输入"2015-11-20"则返回“2015-10-01”
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getSeason(String dateStr) throws ParseException {
		Date date = MS_FORMAT.parse(dateStr);
		String year = dateStr.substring(0, 4);
		String rs = null;
		for (String seasonStr : SEASONS) {
			String completeStr = year + seasonStr;
			Date season = MS_FORMAT.parse(completeStr);
			if (season.compareTo(date) <= 0) {
				rs = completeStr;
			} else {
				break;
			}
		}
		return rs;
	}

	/**
	 * 输入一个季报日期，返回生效季度的第一天，即之后一个季度的第一天。日期格式为“yyyy-MM-dd”。如输入"2015-09-30"则返回“2015
	 * -10-01”
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getReportSeason(String reportDate)
			throws ParseException {
		Date date = MS_FORMAT.parse(reportDate);
		Calendar tempDate = Calendar.getInstance();
		// 计算季报日期30天之后的日期，返回这个日期所属的季度
		tempDate.setTimeInMillis(date.getTime() + 30l * 24 * 3600 * 1000);
		return getSeason(MS_FORMAT.format(tempDate.getTime()));
	}

	public static List<String> smallAndMedSizedPlate() {
		List<String> list = allStocks();
		int i = 0;
		while (i < list.size()) {
			if (list.get(i).startsWith("6") || list.get(i).startsWith("002")
					|| list.get(i).startsWith("3"))
				list.remove(i);
			else
				i++;
		}
		return list;
	}

	public static List<String> allStocks() {
		File[] stocks = new File("data/stockprice").listFiles();
		List<String> list = new ArrayList<String>();
		for (File f : stocks) {
			list.add(f.getName().substring(2));
		}
		return list;
	}

	public static void main(String[] args) {
		try {
			System.out.println(getReportSeason("2016-12-31"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
