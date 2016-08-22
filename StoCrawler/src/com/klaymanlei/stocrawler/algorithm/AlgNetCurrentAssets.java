package com.klaymanlei.stocrawler.algorithm;

import com.klaymanlei.stocrawler.beans.CompHist;
import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;

public class AlgNetCurrentAssets implements Algorithm {
	
	private static final double STANDARD = (double)100 / 66;
	
	AlgNetCurrentAssets() {
		super();
	}
	
	@Override
	public double scoring(Company company) {
		double score = 0;		
		Stock stock = company.getStock();
		CompHist situation = company.getLatestSituation();
		double netCurrentAssets = situation.getCurrentAssets() - situation.getCurrentLiabilities();
		double mktCap = stock.getLatestSituation().getMktCap();
		score = netCurrentAssets / mktCap;
		return score;
	}

	@Override
	public boolean upToStandard(Company company) {
		return scoring(company) >= STANDARD;
	}

}
