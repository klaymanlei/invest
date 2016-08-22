package com.klayman.invest.stock.strategy;

import java.util.List;

import com.klayman.invest.stock.bean.Operation;

public interface Strategy {
	public List<Operation> calculate(List<String> codes);
}
