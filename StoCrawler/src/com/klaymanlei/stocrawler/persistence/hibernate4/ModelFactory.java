package com.klaymanlei.stocrawler.persistence.hibernate4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.klaymanlei.stocrawler.Constants;
import com.klaymanlei.stocrawler.beans.BalanceSheet;
import com.klaymanlei.stocrawler.beans.CashFlow;
import com.klaymanlei.stocrawler.beans.FinancialStatements;
import com.klaymanlei.stocrawler.beans.IncomeStatement;
import com.klaymanlei.stocrawler.beans.StoHist;
import com.klaymanlei.stocrawler.beans.Stock;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.FinancialReportHist;
import com.klaymanlei.stocrawler.persistence.hibernate4.model.StockPriceHist;
import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class ModelFactory {
	private final static Logger log = Logger.getLogger(ModelFactory.class);

	public List<Object> createModel(Object obj) {
		List<Object> list = new ArrayList<Object>();
		if (obj.getClass() == com.klaymanlei.stocrawler.beans.Company.class) {
			list.add(createCompanyModel((com.klaymanlei.stocrawler.beans.Company) obj));
		} else if (obj.getClass() == StoHist.class) {
			list.add(createStockPriceHistModel((StoHist) obj));
		} else if (obj.getClass() == BalanceSheet.class) {
			BalanceSheet balance = (BalanceSheet) obj;
			list.addAll(createFinancialReportHistModel(balance.getCode(), Constants.KIND_BALANCE_SHEET, balance.getDate(), balance.get()));
		} else if (obj.getClass() == IncomeStatement.class) {
			IncomeStatement incomeStatement = (IncomeStatement) obj;
			list.addAll(createFinancialReportHistModel(incomeStatement.getCode(), Constants.KIND_INCOME_STATEMENT, incomeStatement.getDate(), incomeStatement.get()));
		} else if (obj.getClass() == CashFlow.class) {
			CashFlow cashFlow = (CashFlow) obj;
			list.addAll(createFinancialReportHistModel(cashFlow.getCode(), Constants.KIND_CASH_FLOW, cashFlow.getDate(), cashFlow.get()));
		}
		return list;
	}

	private List<FinancialReportHist> createFinancialReportHistModel(String code, String kind, Date date, Map<String, Double> map) {
		List<FinancialReportHist> list = new ArrayList<FinancialReportHist>();
		for (String key : map.keySet()) {
			FinancialReportHist model = new FinancialReportHist(code, kind, date.getCalendar().getTime());
			model.setItem(key);
			model.setValue(map.get(key));
			list.add(model);
		}
		return list;
	}

	private com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany createCompanyModel(com.klaymanlei.stocrawler.beans.Company bean) {
		Stock stock = bean.getStock();
		com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany model = new com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany(stock.getCode(), bean.getName());
		model.setMarket(stock.getMarket());
		model.setIndustry(bean.getIndustry());
		model.setMktCap(stock.getMktCap());
		model.setPe(stock.getPe());
		model.setEps(stock.getEps());
		model.setShares(stock.getShares());
		model.setBookValue(stock.getBookValue());
		return model;
	}

	private StockPriceHist createStockPriceHistModel(StoHist bean) {
		StockPriceHist model = new StockPriceHist(bean.getCode(), bean.getDate().getCalendar().getTime());
		model.setOpen(bean.getOpen());
		model.setClose(bean.getClose());
		model.setHigh(bean.getHigh());
		model.setLow(bean.getLow());
		model.setVolume(bean.getVolume());
		model.setMktCap(bean.getMktCap());
		model.setPe(bean.getPe());
		model.setEps(bean.getEps());
		model.setShares(bean.getShares());
		model.setBookValue(bean.getBookValue());
		return model;
	}

	public Object createBean(Object obj) {
		if (obj.getClass() == com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany.class) {
			return createCompanyBean((com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany) obj);
		} else if (obj.getClass() == StockPriceHist.class) {
			return createStockPriceHistBean((StockPriceHist) obj);
		}
		return null;
	}

	public Collection<FinancialStatements> createFinancialReport(String code, String kind, List<FinancialReportHist> hists) {
		Utilities.verifyParam("code", code);
		Utilities.verifyParam("kind", kind);
		Utilities.verifyParam("hists", hists);

		Map<Date, FinancialStatements> sheetMap = new HashMap<Date, FinancialStatements>();
		
		if (kind.equals(Constants.KIND_BALANCE_SHEET)) {
			for (FinancialReportHist hist : hists) {
				java.util.Date date = hist.getDate();
//				BalanceSheet sheet = createBalanceSheetBean(code, date, hists);
//				if (sheetMap.containsKey(date)) {
//					sheetMap.put(key, value)
//				}
			}
			return sheetMap.values();
		}

		return null;
	}

	private com.klaymanlei.stocrawler.beans.Company createCompanyBean(com.klaymanlei.stocrawler.persistence.hibernate4.model.PoCompany model) {
		Stock stock = new Stock(model.getCode());
		stock.setBookValue(model.getBookValue());
		stock.setEps(model.getEps());
		stock.setMarket(model.getMarket());
		stock.setMktCap(model.getMktCap());
		stock.setName(model.getName());
		stock.setPe(model.getPe());
		stock.setShares(model.getShares());
		com.klaymanlei.stocrawler.beans.Company bean = new com.klaymanlei.stocrawler.beans.Company(stock);
		bean.setIndustry(model.getIndustry());
		bean.setName(model.getName());
		return bean;
	}

	private StoHist createStockPriceHistBean(StockPriceHist model) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(model.getDate());
		StoHist bean = new StoHist(model.getCode(), new Date(calendar));
		bean.setOpen(model.getOpen());
		bean.setClose(model.getClose());
		bean.setHigh(model.getHigh());
		bean.setLow(model.getLow());
		bean.setVolume(model.getVolume());
		bean.setMktCap(model.getMktCap());
		bean.setPe(model.getPe());
		bean.setEps(model.getEps());
		bean.setShares(model.getShares());
		bean.setBookValue(model.getBookValue());
		return bean;
	}

	private BalanceSheet createBalanceSheetBean(String code, Date date, List<FinancialReportHist> hists) {
		Utilities.verifyParam("code", code);
		Utilities.verifyParam("date", date);
		Utilities.verifyParam("hists", hists);

		BalanceSheet balanceSheet = new BalanceSheet(code, date);
		for (FinancialReportHist hist : hists) {
			if (!verifyFinancialReportHist(code, date, Constants.KIND_BALANCE_SHEET, hist))
				continue;
			balanceSheet.put(hist.getItem(), hist.getValue());
		}
		return balanceSheet;
	}

	private boolean verifyFinancialReportHist(String code, Date date, String kind, FinancialReportHist hist) {
		Utilities.verifyParam("code", code);
		Utilities.verifyParam("date", date);
		Utilities.verifyParam("kind", kind);
		Utilities.verifyParam("hist", hist);

		if (!Constants.KIND_BALANCE_SHEET.equals(hist.getKind()))
			return false;
		if (!code.equals(hist.getCode()))
			return false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(hist.getDate());
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
		if (!new Date(calendar).equals(date))
			return false;

		return true;
	}
}
