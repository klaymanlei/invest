package com.klayman.invest.stock.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public class TempPb {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) throws IOException, ParseException {
		Map<String, String> icbMap = initIcbs();
		Map<String, Double> apsMap = initAps();
//		Map<String, Double> priceMap = initPrice();
//		splitPb(); // 把一个pb文件按照icb进行拆分
//		genPb(priceMap, apsMap, icbMap); // 补全2016年pb数据
		List<String> ss50 = DataReader.initSs50();
		for (String code : ss50) {
			String icb = icbMap.get(code);
			if (icb == null)
				continue;
			boolean sig = buySig(code, icb, 0.5, 0.1);
			if (sig)
				System.out.println(code + ":" + icb);
		}
	}

	public static boolean buySig(String code, String icb, double top, double bottom) throws IOException, ParseException {
		List<String> pb = FileUtils.readLines(new File("data/pb/" + icb));
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(FORMAT.parse("2015-10-31").getTime());
//		String nowDate = FORMAT.format(startCal);
		startCal.setTimeInMillis(startCal.getTimeInMillis() - 1000L * 3600 * 24 * 365 * 3);
		String startDate = FORMAT.format(startCal.getTime());
		List<Double> list = new ArrayList<Double>();
		Double currentPb = 0d;
		String tmpDate = null;
		for (String line : pb) {
			try {
				String[] value = line.split("\t");
				if (value[0].compareTo(startDate) < 0 || value[5].equals("NULL"))
					continue;
				if (value[0].compareTo("2015-10-31") > 0)
					continue;
				if (Double.parseDouble(value[5]) > 0)
					list.add(Double.parseDouble(value[5]));
				if (!value[1].equals(code))
					continue;
				if (tmpDate == null || value[0].compareTo(tmpDate) > 0) {
					tmpDate = value[0];
					currentPb = Double.parseDouble(value[5]);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(list);
//		double t = list.get((int)(top * list.size()));
		double b = list.get((int)(bottom * list.size()));
		
		if (currentPb < b) {
//			System.out.println(code);
//			System.out.println(icb);
//			System.out.println(b);
//			System.out.println(currentPb);
//			System.out.println("=========================");
			return true;
		}
		return false;
	}
	
	public static Map<String, Double> initPrice() throws IOException {
		List<String> price = FileUtils.readLines(new File("data/2016-01-01"));
		Map<String, Double> priceMap = new TreeMap<String, Double>();
		for (String line : price) {
			String[] value = line.split("\\,");
			priceMap.put(value[1] + "\t" + value[0], Double.parseDouble(value[5]));
		}
		return priceMap;
	}

	public static Map<String, String> initIcbs() throws IOException {
		List<String> icb = FileUtils.readLines(new File("data/icb"));
		Map<String, String> icbMap = new HashMap<String, String>();
		for (String line : icb) {
			String[] value = line.split("\\,");
			icbMap.put(value[0], value[1]);
		}
		return icbMap;
	}

	public static Map<String, Double> initAps() throws IOException {
		List<String> aps = FileUtils.readLines(new File("data/2015-10.aps"));
		Map<String, Double> apsMap = new HashMap<String, Double>();
		for (String line : aps) {
			String[] value = line.split("\\,");
			apsMap.put(value[0], Double.parseDouble(value[4]));
		}
		return apsMap;
	}
	
	public static void splitPb() {
		BufferedReader in = null;
		Map<String, List<String>> out = new HashMap<String, List<String>>();
		int count = 0;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					"data/pb.out")));
			String line = null;
			while((line = in.readLine()) != null) {
				if (++count % 100000 == 0) {
					for (String key : out.keySet()) {
						FileUtils.writeLines(new File("data/pb/" + key), out.get(key), true);
						out.get(key).clear();
					}
					System.out.println(count);
				}
				String[] value = line.split("\t");
				List<String> lines = out.get(value[2]);
				if (lines == null) {
					lines = new ArrayList<String>();
					out.put(value[2], lines);
				}
				lines.add(line);
			}
			for (String key : out.keySet()) {
				FileUtils.writeLines(new File("data/pb/" + key), out.get(key), true);
				out.get(key).clear();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void genPb(Map<String, Double> priceMap, Map<String, Double> apsMap, Map<String, String> icbMap) throws IOException {
		Map<String, List<String>> out = new HashMap<String, List<String>>();
		for (String key : priceMap.keySet()) {
			String[] value = key.split("\t");
			double price = priceMap.get(key);
			String icb = icbMap.get(value[1]);
			double aps = apsMap.get(value[1]);
			double pb = price / aps;
			List<String> lines = out.get(icb);
			if (lines == null) {
				lines = new ArrayList<String>();
				out.put(icb, lines);
			}
			lines.add(value[0] + "\t" + value[1] + "\t" + icb + "\t" + price + "\t" + aps + "\t" + pb);
		}
		for (String key: out.keySet()) {
			FileUtils.writeLines(new File("data/pb/" + key), out.get(key), true);
			out.get(key).clear();
		}
	}
}
