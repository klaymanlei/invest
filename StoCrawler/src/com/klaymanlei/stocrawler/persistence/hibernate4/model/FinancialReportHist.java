package com.klaymanlei.stocrawler.persistence.hibernate4.model;

import java.util.Date;

public class FinancialReportHist {
	private int id = -1;
	private String code;
	private String kind;
	private Date date;
	private String item;
	private double value = -1;
	
	public FinancialReportHist() {
	}
	
	public FinancialReportHist(String code, String kind, Date date) {
		this.code = code;
		this.kind = kind;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FinancialReportHist [id=" + id + ", code=" + code + ", kind="
				+ kind + ", date=" + date + ", item=" + item + ", value="
				+ value + "]";
	}

}
