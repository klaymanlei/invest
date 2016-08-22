package com.klaymanlei.stocrawler.persistence.hibernate4.model;

public class PoCompany {
	private String code;
	private String market;
	private String name;
	private String industry;
	private long mktCap = -1;
	private double pe = -1;
	private double eps = -1;
	private long shares = -1;
	private double bookValue = -1;

	public PoCompany() {
		// this form used by Hibernate
	}

	public PoCompany(String code, String name) {
		// for application use, to create new events
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public long getMktCap() {
		return mktCap;
	}

	public void setMktCap(long mktCap) {
		this.mktCap = mktCap;
	}

	public long getShares() {
		return shares;
	}

	public void setShares(long shares) {
		this.shares = shares;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public double getBookValue() {
		return bookValue;
	}

	public void setBookValue(double bookValue) {
		this.bookValue = bookValue;
	}

	@Override
	public String toString() {
		return "Company [code=" + code + ", market=" + market + ", name="
				+ name + ", industry=" + industry + ", mktCap=" + mktCap
				+ ", pe=" + pe + ", eps=" + eps + ", shares=" + shares
				+ ", bookValue=" + bookValue + "]";
	}
	
}
