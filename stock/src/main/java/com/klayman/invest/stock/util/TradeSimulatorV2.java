package com.klayman.invest.stock.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Asset;
import com.klayman.invest.stock.bean.Dividend;
import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.strategy.PositionManagement;
import com.klayman.invest.stock.strategy.Strategy;

public class TradeSimulatorV2 {
	private static final Logger LOG = LoggerFactory
			.getLogger(TradeSimulatorV2.class);
	private Asset asset;

	// 输入起始资金
	public TradeSimulatorV2() {
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Asset getAsset() {
		return asset;
	}

	private boolean validParams(List<String> codes, Strategy strategy,
			PositionManagement posManage) {
		boolean rs = true;
		if (codes == null || codes.size() == 0) {
			LOG.error("codes is empty");
			rs = false;
		}
		if (strategy == null) {
			LOG.error("strategy is null");
			rs = false;
		}
		if (posManage == null) {
			LOG.error("posManage is null");
			rs = false;
		}
		return rs;
	}

	private List<Dividend> initDivs(List<String> codes) {
		List<Dividend> divs = new ArrayList<Dividend>();
		for (String code : codes) {
			String completedCode = code;
			if (code.startsWith("6")) {
				completedCode = "SS" + completedCode;
			} else {
				completedCode = "SZ" + completedCode;
			}
			DataReader.readDividend(completedCode, divs);
		}
		Collections.sort(divs);
		return divs;
	}

	private int dividend(List<Dividend> divs, int divPointer, String date) {
		Dividend div = null;
		if (divs.size() > divPointer)
			div = divs.get(divPointer);
		else
			return 0;

		while (div != null && div.getDate().compareTo(date) <= 0) {
			Integer shares = asset.removeShares(div.getCode());
			if (shares != null) {
				asset.setCash(asset.getCash() + div.getMoney() * shares);
				shares = (int) (div.getShare1() * shares + div.getShare2()
						* shares + shares);
				asset.putShares(div.getCode(), shares);
			}
			divPointer++;
			if (divPointer < divs.size())
				div = divs.get(divPointer);
			else
				div = null;
		}
		return divPointer;
	}

	public void trade(List<String> codes, Strategy strategy,
			PositionManagement posManage) {
		if (!validParams(codes, strategy, posManage))
			return;

		List<Operation> ops = strategy.calculate(codes);
		if (ops == null || ops.size() == 0)
			return;

		List<Dividend> divs = initDivs(codes); // 读取分红配股数据
		int divPointer = 0;

		for (Operation op : ops) {
			String date = op.getDate();
			divPointer = dividend(divs, divPointer, date); // 计算date之前的分红配股
			buy(op, posManage);
		}
	}

	private void buy(Operation op, PositionManagement posManage) {
		int stockChange = posManage.calculate(op, asset);
		String code = op.getCode();
		double moneyChange = stockChange * op.getPrice();
		double cash = asset.getCash();
		if (cash + moneyChange < 0)
			return;
		asset.setCash(cash + moneyChange);
		Integer shareCount = asset.removeShares(code);
		if (shareCount == null) {
			shareCount = 0;
		}
		shareCount += stockChange;
		asset.putShares(code, shareCount);
		// System.out.println(date + "\t1\t" + stockIn + "\t" + code + "\t" +
		// price + "\t" + shareCount);
	}
}
