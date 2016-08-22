package com.klayman.invest.stock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtility {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) throws ParseException {
		Calendar c = Calendar.getInstance();
		System.out.println(lastDayOfWeek(c));
		System.out.println(c.get(Calendar.WEEK_OF_YEAR));
		c.set(2016, 1, 29);
		c.roll(Calendar.YEAR, -1);
		System.out.println(FORMAT.format(c.getTime()));
	}

	public static String lastDayOfWeek(Calendar c) {
		c.setWeekDate(c.getWeekYear(), c.get(Calendar.WEEK_OF_YEAR), Calendar.SATURDAY);
		return FORMAT.format(c.getTime());
	}

	public static String lastDayOfMonth(Calendar c) {
//		c.setWeekDate(c.getWeekYear(), c.get(Calendar.WEEK_OF_YEAR), Calendar.SATURDAY);
		c.add(Calendar.MONTH, 1);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return FORMAT.format(c.getTime());
	}
}
