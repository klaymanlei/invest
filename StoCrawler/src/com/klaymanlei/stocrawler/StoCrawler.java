package com.klaymanlei.stocrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.algorithm.Algorithm;
import com.klaymanlei.stocrawler.algorithm.AlgorithmManager;
import com.klaymanlei.stocrawler.beans.CompHist;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.stocrawler.dataparser.HexunParser;
import com.klaymanlei.utils.Date;

public class StoCrawler {

	private final static Logger log = Logger.getLogger(StoCrawler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String code = "600016";
			HexunParser parser = new HexunParser(code);
//			parser.setStockCode(code);
			Stock stock = new Stock(code);
			Company comp = new Company(stock);
			parser.parse(comp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void scrolling() {
		String code = "601668";
		Stock stock = new Stock(code);
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar);
		StoHist stoHist = new StoHist(code, date);
		long mktCap = (long)(882.00 * 100000000);
		stoHist.setMktCap(mktCap);
		stock.addSituation(date, stoHist);
		Company comp = new Company(stock);
		CompHist compHist = new CompHist(comp, date);
		comp.addSituation(date, compHist);
		compHist.setCurrentAssets(473119349000.00);
		compHist.setCurrentLiabilities(340202663000.00);
		Algorithm alg = AlgorithmManager.getAlgorithm(AlgorithmManager.NET_CURRENT_ASSETS);
		double score = alg.scoring(comp);
		System.out.println("Score: " + score);
		System.out.println("pass line: " + alg.upToStandard(comp));
	}

}
