package com.klayman.invest.stock.strategy.threeline;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.klayman.invest.stock.bean.Dividend;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;
import com.klayman.invest.stock.util.DateUtility;

public class MainClass {

	public static void main(String[] args) throws IOException, ParseException {
		File dataFile = new File("data/stockprice");
		Map<String, TreeMap<String, Double>> rsmap = new HashMap<String, TreeMap<String, Double>>();
		int i = 0;
		for (File child : dataFile.listFiles()) {
			i++;
			 if (!child.getName().equals("SZ159907"))
			 continue;
			TreeMap<String, StockRecord> map = new TreeMap<String, StockRecord>();
			DataReader.readPrice(child.getName(), map);

			Date start = DateUtility.FORMAT.parse(map.firstKey());
			Date end = DateUtility.FORMAT.parse(map.lastKey());

			File dividend = new File("data/dividend/" + child.getName());

			TreeMap<String, Dividend> divMap = null;
			if (dividend.exists()) {
				divMap = dividend(FileUtils.readLines(dividend),
						child.getName());
			} else {
				divMap = new TreeMap<String, Dividend>();
			}

			TreeMap<String, String[]> prices = new TreeMap<String, String[]>();
			for (String date : map.keySet()) {
				String[] r = new String[4];
				r[0] = map.get(date).getDate();
				r[1] = String.valueOf(map.get(date).getOpen());
				r[2] = map.get(date).getDate();
				r[3] = String.valueOf(map.get(date).getClose());
				prices.put(date, r);
			}

			TreeMap<String, double[]> threeLineDay = nLineBreak(prices, 3);
			TreeMap<String, Double> rsDay = trade(threeLineDay, divMap, 10000);
			TreeMap<String, Double> growthRate = growthRate(rsDay);
//			printHist(growthRate);
			double avg = avg(growthRate);
			double variance = variance(growthRate, avg);
			
			List<String> lines = FileUtils.readLines(child);
			TreeMap<String, String[]> monthData = byMonth(lines);
			TreeMap<String, double[]> threeLineMonth = nLineBreak(monthData, 2);

			TreeMap<String, Double> rsMonth = trade(threeLineMonth, divMap,
					10000);

			TreeMap<String, String[]> weekData = byWeek(lines);
			TreeMap<String, double[]> threeLineWeek = nLineBreak(weekData, 3);

			TreeMap<String, Double> rsWeek = trade(threeLineWeek, divMap, 10000);

			TreeMap<String, Double> rsWeekMonth = trade(threeLineWeek,
					threeLineMonth, divMap, 10000);
			TreeMap<String, Double> rsDayWeek = trade(threeLineDay,
					threeLineWeek, divMap, 10000);
//			printHist(rsWeekMonth);
			print(threeLineDay);
			// TreeMap<String, Double> rsMonthWeek = trade(threeLine, divMap,
			// initMoney);

			// rsmap.put(child.getName(), rsMonth);
//			System.out.println(i
//					+ "\t"
//					+ child.getName()
//					+ "\t"
//					+ rsMonth.lastEntry().getValue()
//					+ "\t"
//					+ rsWeek.lastEntry().getValue()
//					+ "\t"
//					+ rsDay.lastEntry().getValue()
//					+ "\t"
//					+ (rsWeekMonth.size() == 0 ? 10000 : rsWeekMonth
//							.lastEntry().getValue())
//					+ "\t"
//					+ (rsDayWeek.size() == 0 ? 10000 : rsDayWeek.lastEntry()
//							.getValue())
//					+ "\t"
//					+ (double)(end.getTime() - start.getTime()) / 1000 / 3600 / 24 / 365
//					+ "\t" + variance);
//			break;
		}
	}

	private static void printHist(TreeMap<String, Double> hist) {
		for (String month : hist.keySet()) {
			System.out.println(month
					+ "\t"
					+ hist.get(month));
		}
	}

	private static void print(TreeMap<String, double[]> threeLine) {
		for (String month : threeLine.keySet()) {
			System.out.println(month
					+ "\t"
					+ threeLine.get(month)[0]
					+ "\t"
					+ (threeLine.get(month)[0] > threeLine
							.get(month)[1] ? threeLine.get(month)[0]
							+ "\t" + threeLine.get(month)[1]
							: threeLine.get(month)[1] + "\t"
									+ threeLine.get(month)[0]) + "\t"
					+ threeLine.get(month)[1]);
		}
	}
	
	private static TreeMap<String, Double> growthRate(TreeMap<String, Double> hist) throws ParseException {
		TreeMap<String, Double> ratio = new TreeMap<String, Double>();
		for (String date : hist.keySet()) {
			Calendar c = Calendar.getInstance();
			c.setTime(DateUtility.FORMAT.parse(date));
			c.roll(Calendar.YEAR, -1);
			String lastYear = DateUtility.FORMAT.format(c.getTime());
			if (hist.lowerKey(lastYear) == null) 
				continue;
			double now = hist.get(date);
			double start = hist.lowerEntry(lastYear).getValue();
			ratio.put(date, now / start - 1);
		}
		return ratio;
	}
	
	private static double variance(TreeMap<String, Double> growth, double avg) {
		double sum = 0;
		for (String date: growth.keySet()) {
			sum += Math.pow(growth.get(date) - avg, 2);
		}
		return sum / growth.size();
	}
	
	private static double avg(TreeMap<String, Double> growth) {
		double sum = 0;
		for (String date: growth.keySet()) {
			sum += growth.get(date);
		}
		return sum / growth.size();
	}
	
	private static TreeMap<String, Double> trade(
			TreeMap<String, double[]> small, TreeMap<String, double[]> big,
			TreeMap<String, Dividend> divMap, double initMoney) {
		TreeMap<String, Double> hist = new TreeMap<String, Double>();
		String divDate = divMap != null && divMap.size() > 0 ? divMap
				.firstKey() : null;
		double m = initMoney;
		int hold = 0;
		String prevSmallKey = null;
		for (String month : big.keySet()) {
			boolean allow = true;
			if (month == big.firstKey())
				continue;
			double[] prev = big.lowerEntry(month).getValue();
			double[] brick = big.get(month);
			// 如果大周期的三线反转处于下降通道，任何情况不进行买入操作
			if (brick[1] < brick[0]) {
				allow = false;
			}
			// 如果大周期方向由下跌转为上涨，且空仓则买入
			if (hold == 0 && brick[1] > brick[0] && prev[1] < prev[0]) {
				double buy = (int) (m / brick[1]) / 100 * 100;
				hold += buy;
				m -= buy * brick[1];
//				System.out.println(month + "\tbuy\t" + buy + "\t" + brick[1] + "\t" + m);
			}
			String nextMonth = big.higherKey(month);
			// 对小周期进行判断，获得当前大周期之后的第一个小周期
			String key = small.higherKey(month);
			// 遍历当前大周期和下一个大周期之间的每一个小周期
			while (key != null
					&& (nextMonth == null || key.compareTo(nextMonth) <= 0)) {
				if (divDate != null && prevSmallKey != null) {
					while (key.compareTo(divDate) >= 0) {
						if (prevSmallKey.compareTo(divDate) < 0) {
							Dividend div = divMap.get(divDate);
							m += hold * div.getMoney();
							hold = (int) ((div.getShare1() + div.getShare2() + 1) * hold);
//							System.out.println(key + "\tdivided\t" + divDate + "\t" + m + "\t" + hold);
						}
						divDate = divMap.higherKey(divDate);
						if (divDate == null)
							break;
					}
				}
				if (prevSmallKey == null) {
					prevSmallKey = key;
					key = small.higherKey(key);
					continue;
				}
				double[] smallBrick = small.get(key);
				if (smallBrick[1] < smallBrick[0] && hold > 0) {
					m += hold * smallBrick[1];
//					System.out.println(key + "\tsell\t" + (-hold) + "\t" + smallBrick[1] + "\t" + m);
					hold = 0;
				}
				if (smallBrick[1] > smallBrick[0] && hold == 0 && allow) {
					double buy = (int) (m / smallBrick[1]) / 100 * 100;
					hold += buy;
					m -= buy * smallBrick[1];
//					System.out.println(key + "\tbuy\t" + buy + "\t" + smallBrick[1] + "\t" + m);
				}
				hist.put(key, m + brick[1] * hold);
				prevSmallKey = key;
				key = small.higherKey(key);
			}
		}
		return hist;
	}

	private static TreeMap<String, Dividend> dividend(List<String> lines,
			String code) {
		TreeMap<String, Dividend> divMap = new TreeMap<String, Dividend>();
		for (String line : lines) {
			String[] args = line.split("\\,");
			Dividend div = new Dividend();
			div.setCode(code);
			div.setDate(args[0]);
			div.setMoney(Double.parseDouble(args[1]));
			div.setShare1(Double.parseDouble(args[2]));
			div.setShare2(Double.parseDouble(args[3]));
			divMap.put(div.getDate(), div);
		}
		return divMap;
	}

	private static TreeMap<String, Double> trade(
			TreeMap<String, double[]> nline, TreeMap<String, Dividend> divMap,
			double init) {
		TreeMap<String, Double> hist = new TreeMap<String, Double>();
		double m = init;
		int hold = 0;
		double[] delta = { 0, 0 };
		String divDate = divMap != null && divMap.size() > 0 ? divMap
				.firstKey() : null;
		String prevKey = null;
		for (String key : nline.keySet()) {
			if (divDate != null && prevKey != null) {
				while (key.compareTo(divDate) >= 0) {
					if (prevKey.compareTo(divDate) < 0) {
						Dividend div = divMap.get(divDate);
						m += hold * div.getMoney();
						hold = (int) ((div.getShare1() + div.getShare2() + 1) * hold);
					}
					divDate = divMap.higherKey(divDate);
					if (divDate == null)
						break;
				}
			}
			double[] brick = nline.get(key);
			hist.put(key, m + brick[1] * hold);
			double newDelta = brick[1] - brick[0];
			if (newDelta < 0 && hold > 0) {
				m += hold * brick[1];
//				 System.out.println(key + "\tsell\t" + hold + "\treward\t"
//				 + hold * brick[1] + "\tleft\t" + m);
				hold = 0;
			}
			if (newDelta > 0 && delta[1] < 0) {
				double buy = (int) (m / brick[1]) / 100 * 100;
				hold += buy;
				m -= buy * brick[1];
//				 System.out.println(key + "\tbuy\t" + buy + "\tspend\t"
//				 + buy * brick[1] + "\tleft\t" + m);
			}
			delta[0] = delta[1];
			delta[1] = newDelta;
			prevKey = key;
		}
		return hist;
	}

	private static TreeMap<String, double[]> nLineBreak(
			TreeMap<String, String[]> data, int nline) {
		TreeMap<String, double[]> ops = new TreeMap<String, double[]>();
		double[] top = null;
		for (String month : data.keySet()) {
			double open = Double.parseDouble(data.get(month)[1]);
			double close = Double.parseDouble(data.get(month)[3]);
			if (top == null) {
				if (open == close)
					continue;
				top = new double[nline + 1];
				for (int i = 0; i < top.length - 1; i++) {
					top[i] = open;
				}
				top[top.length - 1] = close;
				double[] brick = new double[2];
				brick[0] = open;
				brick[1] = close;
				ops.put(month, brick);
			} else {
				if ((close < top[top.length - 1] && top[top.length - 1] < top[0])
						|| (close > top[top.length - 1] && top[top.length - 1] > top[0])) {
					double[] brick = new double[2];
					brick[0] = top[top.length - 1];
					brick[1] = close;
					ops.put(month, brick);
					for (int i = 0; i < top.length - 1; i++) {
						top[i] = top[i + 1];
					}
					top[top.length - 1] = close;
				} else if ((close > top[0] && top[0] > top[top.length - 1])
						|| (close < top[0] && top[0] < top[top.length - 1])) {
					double[] brick = new double[2];
					brick[0] = top[top.length - 2];
					brick[1] = close;
					ops.put(month, brick);
					for (int i = 0; i < top.length - 1; i++) {
						top[i] = top[top.length - 2];
					}
					top[top.length - 1] = close;
				}
			}
		}
		return ops;
	}

	private static TreeMap<String, String[]> byWeek(List<String> lines)
			throws ParseException {
		TreeMap<String, String[]> weekData = new TreeMap<String, String[]>();
		String dateKey = null;
		int prevWeek = -1;
		for (String line : lines) {
			if (StringUtils.isEmpty(line) || line.startsWith("Date"))
				continue;
			String[] args = line.split("\\,");
			Date date = DateUtility.FORMAT.parse(args[0]);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int week = c.get(Calendar.WEEK_OF_YEAR);

			// String dateKey = c.get(Calendar.YEAR) + "-"
			// + (week >= 10 ? week : ("0" + week));
			if (dateKey == null || week != prevWeek) {
				prevWeek = week;
				dateKey = DateUtility.lastDayOfWeek(c);
			}
			String[] r = weekData.get(dateKey);
			if (r == null) {
				r = new String[4];
				r[0] = args[0];
				r[1] = args[1];
				r[2] = args[0];
				r[3] = args[4];
				weekData.put(dateKey, r);
			} else {
				if (r[0].compareTo(args[0]) > 0) {
					r[1] = args[1];
					r[0] = args[0];
				} else if (r[2].compareTo(args[0]) < 0) {
					r[3] = args[4];
					r[2] = args[0];
				}
			}
		}
		TreeMap<String, String[]> rs = new TreeMap<String, String[]>();
		for (String key : weekData.keySet()) {
			rs.put(weekData.get(key)[2], weekData.get(key));
		}
		return rs;
	}

	private static TreeMap<String, String[]> byMonth(List<String> lines)
			throws ParseException {
		TreeMap<String, String[]> monthData = new TreeMap<String, String[]>();
		for (String line : lines) {
			if (StringUtils.isEmpty(line) || line.startsWith("Date"))
				continue;
			String[] args = line.split("\\,");
			Date date = DateUtility.FORMAT.parse(args[0]);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			String month = DateUtility.lastDayOfMonth(c);
			String[] r = monthData.get(month);
			if (r == null) {
				r = new String[4];
				r[0] = args[0];
				r[1] = args[1];
				r[2] = args[0];
				r[3] = args[4];
				monthData.put(month, r);
			} else {
				if (r[0].compareTo(args[0]) > 0) {
					r[1] = args[1];
					r[0] = args[0];
				} else if (r[2].compareTo(args[0]) < 0) {
					r[3] = args[4];
					r[2] = args[0];
				}
			}
		}
		return monthData;
	}

}
