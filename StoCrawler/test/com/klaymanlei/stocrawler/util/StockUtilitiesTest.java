package com.klaymanlei.stocrawler.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.persistence.PersistenceTool;
import com.klaymanlei.stocrawler.persistence.PersistenceToolFactory;
import com.klaymanlei.stocrawler.persistence.hibernate4.dao.StockPriceHistDao;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.StockPriceHist;
import com.klaymanlei.utils.Date;

public class StockUtilitiesTest {
	private final static Logger log = Logger
			.getLogger(StockUtilitiesTest.class);

	public void testMovingAverage() {
		List<StoHist> hists = fakeHists();
		Calendar calendar = Calendar.getInstance();
		for (int i = 10; i < 50; i++) {
			Date date = new Date(calendar);
			log.debug(StockUtilities.movingAverage(10, hists, date));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		// fail("Not yet implemented");
	}

	@Test
	public void testExponentailMovingAverage() {
		PersistenceTool tool = new PersistenceToolFactory()
				.getPersistenceTool();
		List<Company> list = (List<Company>) tool.query("PoCompany");
		testExponentailMovingAverage("000001");
	}

	public void testExponentailMovingAverage(String code) {
		StockPriceHistDao dao = new StockPriceHistDao();
		List<StockPriceHist> prices = dao.findByCode(code);
		List<StoHist> hists = new ArrayList<StoHist>();
		for (StockPriceHist price : prices) {
			Calendar c = Calendar.getInstance();
			c.setTime(price.getDate());
			StoHist hist = new StoHist(code, new Date(c));
			hist.setClose(price.getClose());
			hist.setVolume(price.getVolume());
			hists.add(hist);
		}
		StockUtilities.ExponentailMovingAverage(hists);
		for (StoHist hist : hists) {
			log.debug("Code: " + code);
			log.debug("Date: " + hist.getDate());
			log.debug("Close: " + hist.getClose());
			log.debug("Diff: " + hist.getDiff());
			log.debug("Dea: " + hist.getDea());
			log.debug("MACD: " + hist.getMacd());
		}
		// fail("Not yet implemented");
	}

	private List<StoHist> fakeHists() {
		List<StoHist> hists = new ArrayList<StoHist>();
		String code = "000002";
		Calendar calendar = Calendar.getInstance();
		for (int i = 10; i < 50; i++) {
			Date date = new Date(calendar);
			StoHist hist = new StoHist(code, date);
			hist.setOpen(i + 1);
			hist.setClose(i + 2);
			hist.setLow(i);
			hist.setHigh(i + 3);
			hist.setVolume(100000 + i * 10000);
			log.debug("Generated fake StoHist: " + hist);
			hists.add(hist);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return hists;
	}

}
