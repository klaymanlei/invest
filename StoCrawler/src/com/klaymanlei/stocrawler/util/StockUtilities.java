package com.klaymanlei.stocrawler.util;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.utils.Date;

public class StockUtilities {
	private final static Logger log = Logger.getLogger(StockUtilities.class);
	/**
	 * 计算指定日期的MA值
	 * @param duration 时间跨度
	 * @param hists 价格历史
	 * @param date 需要计算MA值的日期
	 * @return MA值
	 */
	public static double movingAverage(int duration, List<StoHist> hists,
			Date date) {
		log.debug("parameters: duration = " + duration + "\n\t hists = "
				+ hists + "\n\t date = " + date);
		StoHist tempHist = new StoHist(hists.get(0).getCode(), date);
		int index = hists.indexOf(tempHist);
		if (index > -1) {
			int i;
			double sum = 0;
			for (i = index; i >= 0 && i > (index - duration); i--) {
				sum += hists.get(i).getClose();
			}
			return sum / (index - i);
		} else {
			return 0;
		}
	}

	/**
	 * 计算给定StoHist列表中每个元素的DIFF值、DEA值和MACD值，短期EMA、长期EMA以及DEA的初始值取默认值
	 * @param hists
	 */
	public static void ExponentailMovingAverage(List<StoHist> hists) {
		if (hists == null || hists.size() == 0) {
			return;
		}
		double emaShort = hists.get(0).getClose();
		double emaLong = emaShort;
		double dea = 0;
		
		ExponentailMovingAverage(hists, emaShort, emaLong, dea);
	}

	/**
	 * 计算给定StoHist列表中每个元素的DIFF值、DEA值和MACD值
	 * @param hists 历史价格列表
	 * @param emaShort 短期EMA初始值
	 * @param emaLong 中期EMA初始值
	 * @param dea DEA初始值
	 */
	public static void ExponentailMovingAverage(List<StoHist> hists, double emaShort, double emaLong, double dea) {
		if (hists == null || hists.size() == 0) {
			return;
		}
		
		int longPeriod = Constants.MACD_LONG_PERIOD;
		int shortPeriod = Constants.MACD_SHORT_PERIOD;
		int signalPeriod = Constants.MACD_SIGNAL_PERIOD;
		
		double diff = emaShort - emaLong;
		
		for (int i = 0; i < hists.size(); i++) {
			StoHist hist = hists.get(i);
			if (hist.getVolume() == 0)
				continue;
			emaShort = (2 * hist.getClose() + (shortPeriod - 1) * emaShort) / (shortPeriod + 1);
			emaLong = (2 * hist.getClose() + (longPeriod - 1) * emaLong) / (longPeriod + 1);
			hist.setEmaShort(emaShort);
			hist.setEmaLong(emaLong);
			diff = emaShort - emaLong;
			hist.setDiff(diff);
			dea = (2 * diff + (signalPeriod - 1) * dea) / (signalPeriod + 1);
			hist.setDea(dea);
			hist.setMacd((diff - dea) * 2);
		}
	}

	public static Calendar latestReportDate() {
		Calendar currentDate = Calendar.getInstance();

		Calendar firstQ = Calendar.getInstance();
		firstQ.set(Calendar.MONTH, 2);
		firstQ.set(Calendar.DAY_OF_MONTH, 1);
		Calendar secondQ = Calendar.getInstance();
		secondQ.set(Calendar.MONTH, 5);
		secondQ.set(Calendar.DAY_OF_MONTH, 1);
		Calendar thirdQ = Calendar.getInstance();
		thirdQ.set(Calendar.MONTH, 8);
		thirdQ.set(Calendar.DAY_OF_MONTH, 1);
		Calendar fourthQ = Calendar.getInstance();
		fourthQ.set(Calendar.MONTH, 11);
		fourthQ.set(Calendar.DAY_OF_MONTH, 1);

		if (currentDate.compareTo(fourthQ) >= 0)
			return fourthQ;
		else if (currentDate.compareTo(thirdQ) >= 0)
			return thirdQ;
		else if (currentDate.compareTo(secondQ) >= 0)
			return secondQ;
		else
			return firstQ;
	}
}
