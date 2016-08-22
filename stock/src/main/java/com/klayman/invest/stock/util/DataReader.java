package com.klayman.invest.stock.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Dividend;
import com.klayman.invest.stock.bean.Pb;
import com.klayman.invest.stock.bean.StockRecord;

public class DataReader {
	private static final Logger LOG = LoggerFactory.getLogger(DataReader.class);

	public static void print(String path, String line, boolean append) {
		File file = new File(path);
		try {
			FileUtils.write(file, line + "\n", append);
		} catch (IOException e) {
		}
	}

	public static void readReport(String name, String item, 
			TreeMap<String, Double> dateValue) {
		Map<String, TreeMap<String, Double>> allMap = new HashMap<String, TreeMap<String, Double>>();
		readReport(name, allMap);
		if (allMap.get(item) == null)
			return;
		dateValue.putAll(allMap.get(item));
	}
	public static void readReport(String name,
			Map<String, TreeMap<String, Double>> map) {
		File report = new File("data/report/" + name);
		String date = null;
		try (Reader ratIn = new FileReader(report);) {
			Iterable<CSVRecord> ratios = CSVFormat.EXCEL.parse(ratIn);
			for (CSVRecord item : ratios) {
				if (item.size() < 2)
					continue;
				if ("会计年度".equals(item.get(0)) || "报告年度".equals(item.get(0))) {
					date = item.get(1).replaceAll("\\.", "-");
				} else if (!"备注".equals(item.get(0))
						&& !"--".equals(item.get(1))) {
					try {
						String title = null;
						double value = 0;
						if (item.size() == 2) {
							title = item.get(0);
							value = Double.parseDouble(item.get(1));
						} else if (item.size() == 3
								&& !"--".equals(item.get(2))) {
							title = item.get(0) + " " + item.get(1);
							value = Double.parseDouble(item.get(2));
						} else
							continue;
						TreeMap<String, Double> itemMap = map.get(title);
						if (itemMap == null) {
							itemMap = new TreeMap<String, Double>();
							map.put(title, itemMap);
						}
						itemMap.put(date, value);
					} catch (NumberFormatException e) {
						LOG.error(item.get(0) + " : " + item.get(1), e);
					}
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			LOG.error("", e);
		} catch (IllegalArgumentException e) {
			LOG.error("", e);
		}
	}

	public static void readDividend(String name, List<Dividend> divs) {
		File file = new File("data/dividend/" + name);
		String code = file.getName().substring(2);
		try (Reader ratIn = new FileReader(file);) {
			Iterable<CSVRecord> ratios = CSVFormat.EXCEL.parse(ratIn);
			for (CSVRecord item : ratios) {
				Dividend d = new Dividend();
				d.setDate(item.get(0));
				d.setCode(code);
				d.setMoney(Double.parseDouble(item.get(1)));
				d.setShare1(Double.parseDouble(item.get(2)));
				d.setShare2(Double.parseDouble(item.get(3)));
				divs.add(d);
			}
		} catch (FileNotFoundException e) {
			// LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		} catch (IllegalArgumentException e) {
		}

	}

	public static void readPrice(String name, TreeMap<String, StockRecord> map) {
		File file = new File("data/stockprice/" + name);
		String code = file.getName().substring(2);
		try (Reader in = new FileReader(file);
				CSVParser parser = new CSVParser(in,
						CSVFormat.EXCEL.withHeader());) {
			for (CSVRecord price : parser) {
				String date = price.get("Date");
				StockRecord r = new StockRecord();
				r.setClose(Double.parseDouble(price.get("Close")));
				r.setCode(code);
				r.setDate(date);
				r.setHigh(Double.parseDouble(price.get("High")));
				r.setLow(Double.parseDouble(price.get("Low")));
				r.setOpen(Double.parseDouble(price.get("Open")));
				r.setVolume(Long.parseLong(price.get("Volume")));
				map.put(date, r);
			}
		} catch (FileNotFoundException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		} catch (IllegalArgumentException e) {
		}

	}

	public static void readPb(String icb, TreeMap<String, Pb> dateCodePb) {
		StringBuffer dateCode = new StringBuffer();
		try (Reader ratIn = new FileReader("data/result/pb/" + icb + ".csv");) {
			Iterable<CSVRecord> icbRecords = CSVFormat.EXCEL.parse(ratIn);
			for (CSVRecord record : icbRecords) {
				dateCode.setLength(0);
				dateCode.append(record.get(0)).append(",");
				dateCode.append(record.get(1));
				Pb pb = new Pb();
				pb.setPrice(Double.parseDouble(record.get(2)));
				pb.setBookValue(Double.parseDouble(record.get(3)));
				pb.setPb(Double.parseDouble(record.get(4)));
				dateCodePb.put(dateCode.toString(), pb);
			}
		} catch (FileNotFoundException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		}
	}
	
	public static void readIcb(Map<String, List<String>> icbMap) {
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
	}

	public static String completeCode(String code) {
		String completedCode = code;

		if (code.startsWith("6")) {
			completedCode = "SS" + completedCode;
		}
		else 
			completedCode = "SZ" + completedCode;
		return completedCode;
	}
	
	public static List<String> initSs50() {
		List<String> codes = new ArrayList<String>();
		codes.add("600000");
		codes.add("600016");
		codes.add("600030");
		codes.add("600050");
		codes.add("600109");
		codes.add("600256");
		codes.add("600519");
		codes.add("600637");
		codes.add("600887");
		codes.add("600999");
		codes.add("601166");
		codes.add("601288");
		codes.add("601390");
		codes.add("601628");
		codes.add("601766");
		codes.add("601857");
		codes.add("601989");
		codes.add("600010");
		codes.add("600018");
		codes.add("600036");
		codes.add("600089");
		codes.add("600111");
		codes.add("600406");
		codes.add("600583");
		codes.add("600690");
		codes.add("600893");
		codes.add("601006");
		codes.add("601169");
		codes.add("601318");
		codes.add("601398");
		codes.add("601668");
		codes.add("601800");
		codes.add("601901");
		codes.add("601998");
		codes.add("600015");
		codes.add("600028");
		codes.add("600048");
		codes.add("600104");
		codes.add("600150");
		codes.add("600518");
		codes.add("600585");
		codes.add("600837");
		codes.add("600958");
		codes.add("601088");
		codes.add("601186");
		codes.add("601328");
		codes.add("601601");
		codes.add("601688");
		codes.add("601818");
		codes.add("601988");
		return codes;
	}
}
