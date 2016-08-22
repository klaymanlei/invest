package com.klayman.invest.stock.picking;

import java.util.Map;

/**
 * 寻找一类股票中连续三周涨幅都处于最前10%的股票
 * 
 * @author Dayu
 *
 */
public class BiggestGain {
	private int duration = 21; // 最近3周
	private double percent = 0.1; // 前10%
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 给定日期前三周之中，stocks列表中涨幅靠前的10%股票。stocks的格式为Map<code, Map<date, close>>
	 * @param stocks
	 * @param date
	 */
	public void cal(Map<String, Map<String, Double>> stocks, String date) 
	{
		
	}
	
}
