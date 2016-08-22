package com.klayman.invest.stock.bean;

public class Dividend implements Comparable<Dividend> {
	private double money;
	private double share1;
	private double share2;
	private String date;
	private String code;

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getShare1() {
		return share1;
	}

	public void setShare1(double share1) {
		this.share1 = share1;
	}

	public double getShare2() {
		return share2;
	}

	public void setShare2(double share2) {
		this.share2 = share2;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		Dividend other = (Dividend) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

	@Override
	public int compareTo(Dividend arg0) {
		if (this.equals(arg0))
			return 0;
		if (date.compareTo(arg0.date) != 0)
			return date.compareTo(arg0.date);
		if (code.compareTo(arg0.code) != 0)
			return code.compareTo(arg0.code);
		return 0;
	}

	@Override
	public String toString() {
		return "Dividend [money=" + money + ", share1=" + share1 + ", share2="
				+ share2 + ", date=" + date + ", code=" + code + "]";
	}

}
