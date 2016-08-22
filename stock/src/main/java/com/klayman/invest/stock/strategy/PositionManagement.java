package com.klayman.invest.stock.strategy;

import com.klayman.invest.stock.bean.Asset;
import com.klayman.invest.stock.bean.Operation;

public interface PositionManagement {
	public int calculate(Operation op, Asset asset);
}
