package com.klayman.invest.stock.strategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;
import com.klayman.invest.stock.util.TradeSimulator;

//计算之前5年的平均市盈率，如果低于10%买入，高于50%卖出
public class IcbPeStrategy {
	private static final Logger LOG = LoggerFactory
			.getLogger(IcbPeStrategy.class);

	private static final String PE_ITEM = "每股收益";
	private static final double PE_LOWER = 0.1;
	private static final double PE_UPEER = 0.5;
	private static final long SAMPLING_PERIOD = 3;
	private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final String ICB;
	private final String CODE;
	private final String OUT_PATH;
	private List<String> codes = null;

	public IcbPeStrategy(String icb, String code) {
		ICB = icb;
		CODE = code;
		OUT_PATH = "data/result/pe/" + ICB + "/";
		Map<String, List<String>> icbCodes = new HashMap<String, List<String>>();
		DataReader.readIcb(icbCodes);
		codes = icbCodes.get(ICB);
		if (CODE != null) {
			if (codes.contains(CODE)) {
				codes.clear();
				codes.add(CODE);
			} else {
				return;
			}
		}
	}

	public IcbPeStrategy(String icb, List<String> codes) {
		ICB = icb;
		CODE = null;
		OUT_PATH = "data/result/pe/" + ICB + "/";
		this.codes = codes;
	}

	public TreeMap<String, List<Operation>> start() {
		Map<String, TreeMap<String, StockRecord>> codeDatePrice = new HashMap<String, TreeMap<String, StockRecord>>();
		Map<String, TreeMap<String, Double>> codeDateValues = new HashMap<String, TreeMap<String, Double>>();
		init(codes, codeDatePrice, codeDateValues);

		Map<String, TreeMap<String, Double>> codeDatePe = new HashMap<String, TreeMap<String, Double>>();
		calPe(codeDatePrice, codeDateValues, codeDatePe);

		TreeMap<String, List<Operation>> dateOp = new TreeMap<String, List<Operation>>();
		for (String code : codeDateValues.keySet()) {
			TreeMap<String, Double> dateValue = codeDateValues.get(code);
			TreeMap<String, StockRecord> datePrice = codeDatePrice.get(code);
			for (String date : dateValue.keySet()) {
				double[] baseline = calBaseLine(codeDatePe, date);
				if (baseline == null)
					continue;
				TreeMap<String, Double> datePe = codeDatePe.get(code);
				String nextDay = datePe.higherKey(date);
				if (nextDay == null)
					continue;
				try {
					String prevDay = date;
					long prevTime = LONG_DATE_FORMAT.parse(date).getTime();
					long time = LONG_DATE_FORMAT.parse(nextDay).getTime();
					while ((nextDay = datePe.higherKey(nextDay)) != null) {
						time = LONG_DATE_FORMAT.parse(nextDay).getTime();
						if ((time - prevTime) > 1000l * 3600 * 24 * 20)
							break;
						prevTime = time;
						prevDay = nextDay;
					}
					if (nextDay == null)
						continue;
					Double pe = datePe.get(prevDay);
					if (pe == null)
						continue;
					Operation op = null;
					if (pe < baseline[0] && pe > 0) {
						op = new Operation();
						op.setBuy(true);
					} else if (pe > baseline[1]) {
						op = new Operation();
						op.setBuy(false);
					}
					if (op == null)
						continue;
					op.setCode(code);
					String opDate = datePrice.higherKey(prevDay);
					// LOG.info("(" + code + ") pe at " + prevDay + " is " +
					// pe);
					// LOG.info("(" + code + ") " + opDate + " at "
					// + datePrice.get(opDate) + " buy " + op.isBuy());
					List<Operation> ops = dateOp.get(opDate);
					if (ops == null) {
						ops = new ArrayList<Operation>();
						if (opDate == null)
							continue;
						dateOp.put(opDate, ops);
					}
					ops.add(op);
				} catch (ParseException e) {
				}
			}
		}

		TradeSimulator s = new TradeSimulator(100000);
		if (codes.size() == 1)
			s.setPerBuy(100000d / 3);
		s.trade(codeDatePrice, dateOp, ICB);
//		LOG.info(ICB + "\t" + codes.size() + "\t"
//				+ (dateOp.size() > 0 ? dateOp.firstKey() : "") + "\t"
//				+ s.getINIT() + "\t" + s.getAsset());
		
		return dateOp;
	}

	private double[] calBaseLine(
			Map<String, TreeMap<String, Double>> codeDatePe, String currentDate) {
		// LOG.info("get baseline at " + currentDate);
		double upper = 0;
		double lower = 0;
		long current = 0;
		try {
			current = LONG_DATE_FORMAT.parse(currentDate).getTime();
		} catch (ParseException e) {
			return null;
		}
		List<Double> pes = new ArrayList<Double>();
		for (String code : codeDatePe.keySet()) {
			TreeMap<String, Double> datePe = codeDatePe.get(code);
			if (datePe == null || datePe.size() == 0)
				continue;
			long time = 0;
			try {
				time = LONG_DATE_FORMAT.parse(datePe.firstKey()).getTime();
				if ((current - time) < SAMPLING_PERIOD * 365 * 24 * 3600 * 1000)
					continue;
			} catch (ParseException e1) {
			}
			for (String date : datePe.keySet()) {
				try {
					time = LONG_DATE_FORMAT.parse(date).getTime();
					if (time > current + 30l * 24 * 3600 * 1000)
						break;
					if (time >= current - SAMPLING_PERIOD * 365 * 24 * 3600
							* 1000 - 30) {
						// LOG.info("(" + code + ") pe at " + date + " is "
						// + datePe.get(date));
						pes.add(datePe.get(date));
					}
				} catch (ParseException e) {
				}
			}
		}
		if (pes.size() > 0) {
			Collections.sort(pes);
			lower = pes.get((int) (pes.size() * PE_LOWER));
			upper = pes.get((int) (pes.size() * PE_UPEER));
			// LOG.info("low " + lower + "; upper " + upper);
			return new double[] { lower, upper };
		} else
			return null;
	}

	private void calPe(
			Map<String, TreeMap<String, StockRecord>> codeDatePrices,
			Map<String, TreeMap<String, Double>> codeDateValues,
			Map<String, TreeMap<String, Double>> codeDatePe) {

		for (String code : codeDatePrices.keySet()) {
			TreeMap<String, Double> datePe = new TreeMap<String, Double>();
			codeDatePe.put(code, datePe);
			TreeMap<String, Double> dateValues = codeDateValues.get(code);
			if (dateValues == null)
				continue;
			TreeMap<String, StockRecord> datePrice = codeDatePrices.get(code);
			String datePointer = dateValues.firstKey();
			if (datePointer == null)
				continue;
			long timePointer = 0;
			try {
				timePointer = LONG_DATE_FORMAT.parse(datePointer).getTime();
			} catch (ParseException e) {
				timePointer = 0;
			}
			// 取出第二条季报信息，过滤与上一条记录相隔超过3个月的财报记录，方便计算3个月之内的每股利润
			for (String date : dateValues.keySet()) {
				if (date.equals(datePointer))
					continue;
				try {
					long end = LONG_DATE_FORMAT.parse(date).getTime();
					long tmp = end - timePointer;
					if ((tmp / 1000 / 3600 / 24) < 120)
						break;
					timePointer = end;
					datePointer = date;
				} catch (ParseException e) {
					LOG.error("", e);
				}
			}

			while ((datePointer = dateValues.higherKey(datePointer)) != null) {
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
							double profitsPerShare = calProfitsPerShare(
									datePointer, dateValues);
							double pe = profitsPerShare == 0 ? 100000
									: datePrice.get(date).getClose()
											/ profitsPerShare;
							if (pe > 0)
								datePe.put(date, pe);

							/**************************** 输出PE计算结果 ****************************/
							DataReader.print(OUT_PATH + code + ".csv", date
									+ "," + profitsPerShare + "," + pe + ","
									+ datePrice.get(date).getClose(), true);
							/**************************** 输出PE计算结果 ****************************/

						}
					} catch (ParseException e) {
						LOG.error("", e);
					}
				}
			}
		}
	}

	private double calProfitsPerShare(String datePointer,
			TreeMap<String, Double> dateValues) {
		if (datePointer.contains("-03-"))
			return dateValues.get(datePointer);
		String prevDate = dateValues.lowerKey(datePointer);
		if (prevDate == null)
			throw new NullPointerException();
		return dateValues.get(datePointer) - dateValues.get(prevDate);
	}

	private void init(List<String> codes,
			Map<String, TreeMap<String, StockRecord>> codePrices,
			Map<String, TreeMap<String, Double>> codeDateValues) {
		for (String code : codes) {
			TreeMap<String, Double> dateValues = new TreeMap<String, Double>();
			TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
			DataReader.readPrice(DataReader.completeCode(code), datePrice);
			codePrices.put(code, datePrice);
			DataReader.readReport(DataReader.completeCode(code), PE_ITEM,
					dateValues);
			if (dateValues == null || dateValues.size() == 0)
				continue;
			codeDateValues.put(code, dateValues);
		}
	}

	public static void main(String[] args) {
		Map<String, List<String>> icbMap = new HashMap<String, List<String>>();
		DataReader.readIcb(icbMap);
		
		System.out.print("\tStock Count\tMin Asset\tAsset\tMax Asset\tFirst Trade");
		for (int i = 2003; i < 2016; i++) {
			for (int j = 1; j < 13; j++) {
				System.out.print("\t" + i + "-" + (j < 10 ? "0" : "") + j);
			}
		}
		System.out.println();

		// String icb = "移动通信";
		for (String icb : icbMap.keySet()) {
			IcbPeStrategy strategy = new IcbPeStrategy(icb, (String) null);
			strategy.start();
		}
	}

}
