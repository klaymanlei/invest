package com.klaymanlei.stocrawler.beans;

import com.klaymanlei.utils.Date;
import com.klaymanlei.utils.Utilities;

public class CompHist {

	private final Company company;
	private final Date date;
	private double currentAssets;
	private double currentLiabilities;
	private final BalanceSheet balance;
	private final IncomeStatement incomeStatement;
	private final CashFlow cashFlow;

	public CompHist(Company company, Date date) {
		super();
		Utilities.verifyParam("compName", company);
		Utilities.verifyParam("date", date);
		this.company = company;
		this.date = date;
		balance = new BalanceSheet(company.getStock().getCode(), date);
		incomeStatement = new IncomeStatement(company.getStock().getCode(), date);
		cashFlow = new CashFlow(company.getStock().getCode(), date);
	}

	public double getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(double currentAssets) {
		this.currentAssets = currentAssets;
	}

	public double getCurrentLiabilities() {
		return currentLiabilities;
	}

	public void setCurrentLiabilities(double currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}

	public Company getCompName() {
		return company;
	}

	public Date getDate() {
		return date;
	}

	public BalanceSheet getBalance() {
		return balance;
	}

	public Company getCompany() {
		return company;
	}

	public IncomeStatement getIncomeStatement() {
		return incomeStatement;
	}

	public CashFlow getCashFlow() {
		return cashFlow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((company == null) ? 0 : company.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompHist other = (CompHist) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CompHist [company=" + company.getStock().getCode() + ", date=" + date
				+ ", currentAssets=" + currentAssets + ", currentLiabilities="
				+ currentLiabilities + ",\n balance=" + balance
				+ ",\n incomeStatement=" + incomeStatement + ", \ncashFlow="
				+ cashFlow + "]\n";
	}
	
}
