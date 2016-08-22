package com.klaymanlei.utils;

import java.util.Calendar;


public class Date implements Comparable<Date> {

	private final int year;
	private final int month;
	private final int day;
	private final Calendar calendar;

	public Date(Calendar calendar) {
		super();
		Utilities.verifyParam("calendar", calendar);
		this.calendar = calendar;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Date other = (Date) obj;
		if (day != other.day)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Date [" + year + "." + month + "." + day + "]";
	}

	@Override
	public int compareTo(Date other) {
		if (this == other)
			return 0;
		if (other == null)
			throw new NullPointerException("obj is null");
//		if (getClass() != obj.getClass())
//			throw new IllegalArgumentException("Inllegal Argument: " + obj);
//		Date other = (Date) obj;
		int dif = year - other.year;
		if (dif != 0)
			return dif;
		dif = month - other.month;
		if (dif != 0)
			return dif;
		return day - other.day;
	}

}
