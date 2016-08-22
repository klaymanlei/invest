package com.klayman.invest.stock.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;

public class SpeedAnalysis {

	public void analysis(String code) throws IOException {
		TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
		DataReader.readPrice(DataReader.completeCode(code), datePrice);
		double prevPrice = -1;
		double prevV = -1;
		TreeMap<String, Double> speed = new TreeMap<String, Double>();
		TreeMap<String, Double> speed2 = new TreeMap<String, Double>();
		for (String key : datePrice.keySet()) {
			if (prevPrice >= 0) {
				double tmp = datePrice.get(key).getClose() - prevPrice;
				if (tmp < 0)
					tmp *= -1;
				speed.put(key, tmp);
			}
			if (prevV >= 0) {
				double tmp = datePrice.get(key).getVolume() - prevV;
				if (tmp < 0)
					tmp *= -1;
				speed2.put(key, tmp);
			}
			prevPrice = datePrice.get(key).getClose();
			prevV = datePrice.get(key).getVolume();
		}
		File f = new File("data/speedanalysis/" + DataReader.completeCode(code));
		List<Double> ma = new ArrayList<Double>(20);
		List<Double> ma2 = new ArrayList<Double>(20);
		for (String date : speed.keySet()) {
			if (ma.size() == 20)
				ma.remove(0);
			ma.add(speed.get(date));
			if (ma2.size() == 20)
				ma2.remove(0);
			ma2.add(speed2.get(date));
			double avg = 0;
			for (double s : ma) {
				avg += s;
			}
			avg /= ma.size();
			double avg2 = 0;
			for (double s : ma2) {
				avg2 += s;
			}
			avg2 /= ma2.size();
			FileUtils
					.write(f,
							date + "," + datePrice.get(date).getClose() + "," + speed.get(date) + "," + avg + ","
									+ datePrice.get(date).getVolume() + "," + speed2.get(date) + "," + avg2 + "\n",
							true);
		}
	}

	public static void main(String[] args) throws IOException {
		SpeedAnalysis a = new SpeedAnalysis();
		a.analysis("600000");
		System.out.println("Done");
	}

}
