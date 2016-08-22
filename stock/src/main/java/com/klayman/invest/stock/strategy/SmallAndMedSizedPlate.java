package com.klayman.invest.stock.strategy;

import java.util.List;

import com.klayman.invest.stock.util.StockUtils;

public class SmallAndMedSizedPlate {

	public static void main(String[] args) {
		List<String> stocks = StockUtils.smallAndMedSizedPlate();
		for (String code: stocks) {
			System.out.println(code);
		}
	}

}
