package com.klayman.invest.stock;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;
import com.klayman.invest.stock.util.TradeSimulator;

/**
 * 测试用的算法，只计算市盈率，将每一个上证指数的股票的所有财报取出来，取每一次发布财报最近三个月的每股收益，用发布财报之前和之后各30天的收盘价格除以3个月的每股收益得到平均市盈率。
 * 如果平均市盈率低于全部历史纪录当中90%的记录则买入，如果高于50%的记录则卖出。
 * 由于测试时不论任何一年的市盈率都是使用全记录计算PE上下限，如即使计算2008年的价格，可能也是用从1999-2015年的所有价格计算出的PE上下限来对比，所以不具备实际操作的可行性。
 * 
 * @author Dayu
 *
 */
public class PeSim {
	private static final Logger LOG = LoggerFactory.getLogger(PeSim.class);
	private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	// private static final SimpleDateFormat SHORT_DATE_FORMAT = new
	// SimpleDateFormat("yyyy-MM");
	private static final Calendar CALENDAR = Calendar.getInstance();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		PeSim pepb = new PeSim();
		// pepb.averagePe();
		pepb.simTrade();
	}

	public void simTrade() {
		Map<String, List<String>> icbMap = loadIcb();
		for (String icb : icbMap.keySet()) {
			/************** for test ****************/
//			if (!icb.equals("银行业（III）"))
//				continue;
			/************** for test ****************/
			// LOG.info("Proc " + icb + "...");
			// File icbFile = new File("data/" + icb + ".csv");
			List<String> codes = icbMap.get(icb);
			TreeMap<String, List<Operation>> results = new TreeMap<String, List<Operation>>();
			Map<String, TreeMap<String, StockRecord>> prices = new HashMap<String, TreeMap<String, StockRecord>>();
			String firstEntry = null;
			String from = null;
			String to = null;

			double[] baseLine = calBaseLine(icb);

			for (String code : codes) {
				
				/************** for test ****************/
//				if (!code.equals("600000"))
//					continue;
				/************** for test ****************/
				
				// LOG.info(code);
				Map<String, TreeMap<String, Double>> reportMap = new HashMap<String, TreeMap<String, Double>>();
				TreeMap<String, StockRecord> priceMap = new TreeMap<String, StockRecord>();
				String completedCode = code;
				if (code.startsWith("6")) {
					completedCode = "SS" + completedCode;
				} else {
					completedCode = "SZ" + completedCode;
				}
				DataReader.readReport(completedCode, reportMap);
				DataReader.readPrice(completedCode, priceMap);
				prices.put(code, priceMap);

				/************** 从第一次买入开始计算时间 ****************/
				if (firstEntry == null || firstEntry.compareTo(priceMap.firstKey()) > 0)
					firstEntry = priceMap.firstKey();
				/************** 从第一次买入开始计算时间 ****************/
				if (to == null || to.compareTo(priceMap.lastKey()) < 0)
					to = priceMap.lastKey();

				for (String item : reportMap.keySet()) {
					TreeMap<String, Double> valueMap = reportMap.get(item);
					if (item.equals("每股收益")) {

						for (String date : valueMap.keySet()) {
							double value = valueMap.get(date);

							/************** 只计算最近三个月的每股收益 ****************/
							try {
								if (!date.contains("-03-")) {
									String key = valueMap.lowerKey(date);
									if (key == null)
										continue;
									long prev = format.parse(key).getTime();
									long now = format.parse(date).getTime();
									if ((now - prev) > 1000l * 3600 * 24 * 120)
										continue;
									value -= valueMap.get(key);
								}
							} catch (ParseException e1) {
							}
							/************** 只计算最近三个月的每股收益 ****************/

							try {
								CALENDAR.setTime(LONG_DATE_FORMAT.parse(date));
								CALENDAR.add(Calendar.DAY_OF_MONTH, 30);
								String endDate = LONG_DATE_FORMAT
										.format(CALENDAR.getTime());
								CALENDAR.add(Calendar.DAY_OF_MONTH, -60);
								String startDate = LONG_DATE_FORMAT
										.format(CALENDAR.getTime());
								int count = 0;
								double sum = 0;
								for (String priceDate : priceMap.keySet()) {
									if (priceDate.compareTo(endDate) > 0)
										break;
									if (priceDate.compareTo(startDate) < 0)
										continue;
									double price = priceMap.get(priceDate)
											.getClose();
									sum += price;
									count++;
								}
								double rate = sum / count / value;
								
								Operation tradeObj = null;
								if (rate < baseLine[0] && rate > 0) {
									CALENDAR.setTime(LONG_DATE_FORMAT
											.parse(date));
									CALENDAR.add(Calendar.DAY_OF_MONTH, 31);
									String tradeDate = LONG_DATE_FORMAT
											.format(CALENDAR.getTime());
									tradeObj = new Operation();
									tradeObj.setCode(code);
									tradeObj.setBuy(true);
									StockRecord t = priceMap.get(tradeDate);
									if (t == null) {
										for (int i = 1; i < 10; i++) {
											CALENDAR.add(Calendar.DAY_OF_MONTH,
													1);
											tradeDate = LONG_DATE_FORMAT
													.format(CALENDAR.getTime());
											t = priceMap.get(tradeDate);
											if (t != null)
												break;
										}
										if (t == null)
											continue;
									}
									List<Operation> opList = results
											.get(tradeDate);
									if (opList == null) {
										opList = new ArrayList<Operation>();
										results.put(tradeDate, opList);
									}
									opList.add(tradeObj);
									/************** 从第一次买入开始计算时间 ****************/
									if (from == null)
										from = tradeDate;
									/************** 从第一次买入开始计算时间 ****************/
								} else if (rate > baseLine[1]) {
									CALENDAR.setTime(LONG_DATE_FORMAT
											.parse(date));
									CALENDAR.add(Calendar.DAY_OF_MONTH, 31);
									String tradeDate = LONG_DATE_FORMAT
											.format(CALENDAR.getTime());
									tradeObj = new Operation();
									tradeObj.setCode(code);
									tradeObj.setBuy(false);
									StockRecord t = priceMap.get(tradeDate);
									if (t == null) {
										for (int i = 1; i < 10; i++) {
											CALENDAR.add(Calendar.DAY_OF_MONTH,
													1);
											tradeDate = LONG_DATE_FORMAT
													.format(CALENDAR.getTime());
											t = priceMap.get(tradeDate);
											if (t != null)
												break;
										}
										if (t == null)
											continue;
									}
									List<Operation> opList = results
											.get(tradeDate);
									if (opList == null) {
										opList = new ArrayList<Operation>();
										results.put(tradeDate, opList);
									}
									opList.add(tradeObj);
								}
							} catch (ParseException e) {
								LOG.error("", e);
							}
						}
					}
				}
			}

			TradeSimulator s = new TradeSimulator(100000);
			s.trade(prices, results, icb);
			if (!StringUtils.isEmpty(from) && !StringUtils.isEmpty(to))
				try {
					Date start = format.parse(from);
					Date end = format.parse(to);
					double years = (double) (end.getTime() - start.getTime())
							/ 1000 / 3600 / 24 / 365;
					LOG.info(icb
							+ "\t"
							+ baseLine[0]
							+ "\t"
							+ baseLine[1]
							+ "\t"
							+ s.getINIT()
							+ "\t"
							+ s.getAsset()
							+ "\t"
							+ firstEntry
							+ "\t"
							+ from
							+ "\t"
							+ to
							+ "\t"
							+ (int) (years + 0.5)
							+ "\t"
							+ StrictMath.pow(s.getAsset() / s.getINIT(),
									1.0 / years));
				} catch (ParseException e) {
					LOG.error("", e);
				}
		}
	}

	public double[] calBaseLine(String icb) {
		double[] result = { Double.MIN_VALUE, Double.MAX_VALUE };
		Map<String, List<String>> icbMap = loadIcb();
		// File icbFile = new File("data/" + icb + ".csv");
		List<String> codes = icbMap.get(icb);
		List<Double> ratios = new ArrayList<Double>();
		for (String code : codes) {
			// LOG.info(code);
			Map<String, TreeMap<String, Double>> reportMap = new HashMap<String, TreeMap<String, Double>>();
			TreeMap<String, StockRecord> priceMap = new TreeMap<String, StockRecord>();
			String completedCode = code;
			if (code.startsWith("6")) {
				completedCode = "SS" + completedCode;
			} else {
				completedCode = "SZ" + completedCode;
			}
			DataReader.readReport(completedCode, reportMap);
			DataReader.readPrice(completedCode, priceMap);

			for (String item : reportMap.keySet()) {
				if (item.equals("每股收益")) {
					TreeMap<String, Double> valueMap = reportMap.get(item);
					TreeMap<String, Double> results = calculate(valueMap,
							priceMap);
					ratios.addAll(results.values());
				}
			}
		}
		if (ratios.size() > 0) {
			Collections.sort(ratios);
			int i = 0;
			while (ratios.get(i) < 0) {
				i++;
			}
			int validCount = ratios.size() - i;
			result[0] = ratios.get(validCount / 10 + i);
			result[1] = ratios.get(validCount / 2 + i);
		}
		// LOG.info(icb + ", " + result[0] + ", " + result[1]);
		return result;
	}

	private Map<String, List<String>> loadIcb() {
		Map<String, List<String>> icbMap = new HashMap<String, List<String>>();
		try (Reader ratIn = new FileReader("data/icb");) {
			Iterable<CSVRecord> icbRecords = CSVFormat.EXCEL.parse(ratIn);
			for (CSVRecord record : icbRecords) {
				String code = record.get(0);
				String icb = record.get(1);
				List<String> codeList = icbMap.get(icb);
				if (codeList == null) {
					codeList = new ArrayList<String>();
					icbMap.put(icb, codeList);
				}
				codeList.add(code);
			}
		} catch (FileNotFoundException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		}
		return icbMap;
	}

	private TreeMap<String, Double> calculate(TreeMap<String, Double> valueMap,
			TreeMap<String, StockRecord> priceMap) {
		TreeMap<String, Double> result = new TreeMap<String, Double>();
		for (String date : valueMap.keySet()) {
			double value = valueMap.get(date);

			/************** 只计算最近三个月的每股收益 ****************/
			try {
				if (!date.contains("-03-")) {
					String key = valueMap.lowerKey(date);
					if (key == null)
						continue;
					long prev = format.parse(key).getTime();
					long now = format.parse(date).getTime();
					if ((now - prev) > 1000l * 3600 * 24 * 120)
						continue;
					value -= valueMap.get(key);
				}
			} catch (ParseException e1) {
			}
			/************** 只计算最近三个月的每股收益 ****************/

			try {
				CALENDAR.setTime(LONG_DATE_FORMAT.parse(date));
				String endDate = null;
				String startDate = null;
				if (date.contains("-12-")) { // 年报通常3月底发布
					CALENDAR.add(Calendar.DAY_OF_MONTH, 90);
					endDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
					CALENDAR.add(Calendar.DAY_OF_MONTH, -121);
					startDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
				}else if (date.contains("-06-")) { // 年报通常3月底发布
					CALENDAR.add(Calendar.DAY_OF_MONTH, 62);
					endDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
					CALENDAR.add(Calendar.DAY_OF_MONTH, -92);
					startDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
				}else {
				CALENDAR.add(Calendar.DAY_OF_MONTH, 30);
				endDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
				CALENDAR.add(Calendar.DAY_OF_MONTH, -60);
				startDate = LONG_DATE_FORMAT.format(CALENDAR.getTime());
				}
				for (String priceDate : priceMap.keySet()) {
					if (priceDate.compareTo(endDate) > 0)
						break;
					if (priceDate.compareTo(startDate) < 0)
						continue;
					double price = priceMap.get(priceDate).getClose();
					result.put(priceDate, price / value);
//					try {
//						FileUtils.write(new File("e:/testPE.csv"), priceMap.get(priceDate).getCode() + "\t" + priceDate + "\t" + price + "\t" + value + "\t"
//								+ (price / value) + "\n", true);
//					} catch (IOException e) {
//						LOG.error("", e);
//					}
				}
			} catch (ParseException e) {
				LOG.error("", e);
			}
		}
		return result;
	}
}
