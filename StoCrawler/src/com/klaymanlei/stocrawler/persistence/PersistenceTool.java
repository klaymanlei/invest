package com.klaymanlei.stocrawler.persistence;

import java.util.List;

import com.klaymanlei.stocrawler.beans.Company;
import com.klaymanlei.stocrawler.beans.Stock;

public interface PersistenceTool {
	
	public void saveOrUpdate(Object obj);

	public List<?> query(String dataKind);
	
	public Company queryCompany(Stock stock);
	
	public Company queryCompanyWithAllSituations(Stock stock);

	public void delete(Object obj);

	public void close();
}
