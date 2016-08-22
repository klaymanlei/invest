package com.klaymanlei.stocrawler.algorithm;

import com.klaymanlei.stocrawler.beans.Company;

public interface Algorithm {
	public boolean upToStandard(Company company);
	public double scoring(Company company);
}
