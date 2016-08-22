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
	 * ����һ�����ڣ������������ȵĵ�һ�졣���ڸ�ʽΪ��yyyy-MM-dd����������"2015-11-20"�򷵻ء�2015-10-01��
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
	 * ����һ���������ڣ�������Ч���ȵĵ�һ�죬��֮��һ�����ȵĵ�һ�졣���ڸ�ʽΪ��yyyy-MM-dd����������"2015-09-30"�򷵻ء�2015
	 * -10-01��
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getReportSeason(String reportDate)
			throws ParseException {
		Date date = MS_FORMAT.parse(reportDate);
		Calendar tempDate = Calendar.getInstance();
		// ���㼾������30��֮������ڣ�����������������ļ���
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
