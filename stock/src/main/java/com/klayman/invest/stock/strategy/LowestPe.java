package com.klayman.invest.stock.strategy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klayman.invest.stock.bean.Operation;
import com.klayman.invest.stock.bean.StockRecord;
import com.klayman.invest.stock.util.DataReader;
import com.klayman.invest.stock.util.TradeSimulator;

public class LowestPe {
	private static final Logger LOG = LoggerFactory.getLogger(LowestPe.class);
	private static final String PE_ITEM = "每股收益";
	private static final double PE_LOWER = 0.1;
	private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final String OUT_PATH;

	public LowestPe() {
		OUT_PATH = "data/result/lowestpe/";
	}

	public TreeMap<String, List<Operation>> start() {
		TreeMap<String, List<Operation>> dateOp = new TreeMap<String, List<Operation>>();
		TreeMap<String, List<PeRecord>> datePeList = new TreeMap<String, List<PeRecord>>();
		loadData(datePeList);
		for (String date : datePeList.keySet()) {
			List<PeRecord> list = datePeList.get(date);
			// if (list.size() < 100)
			// continue;
			Collections.sort(list);
			for (int i = 0; i < list.size() * PE_LOWER; i++) {
				PeRecord pr = list.get(i);
				// LOG.info(pr.code + "\t" + pr.pe + "\t" + pr.buyDate + "\t"
				// + pr.sellDate);
				Operation buy = new Operation();
				buy.setBuy(true);
				buy.setCode(pr.getCode());
				putOp(buy, pr.getBuyDate(), dateOp);

				if (pr.getSellDate() != null) {
					Operation sell = new Operation();
					sell.setBuy(false);
					sell.setCode(pr.getCode());
					putOp(sell, pr.getSellDate(), dateOp);
				}
			}
		}
		return dateOp;
	}

	private void putOp(Operation op, String opDate,
			TreeMap<String, List<Operation>> dateOp) {
		List<Operation> opList = dateOp.get(opDate);
		if (opList == null) {
			opList = new ArrayList<Operation>();
			dateOp.put(opDate, opList);
		}
		opList.add(op);
	}

	private void loadData(TreeMap<String, List<PeRecord>> datePeList) {
		TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
		TreeMap<String, Double> dateValue = new TreeMap<String, Double>();
		File[] children = new File("data/stockprice").listFiles();
		for (File child : children) {
			datePrice.clear();
			dateValue.clear();
			DataReader.readPrice(child.getName(), datePrice);
			DataReader.readReport(child.getName(), PE_ITEM, dateValue);
			if (dateValue.size() == 0)
				continue;
			for (String date : dateValue.keySet()) {
				if (!date.contains("-12-"))
					continue;
				double value = dateValue.get(date);
				try {
					long time = LONG_DATE_FORMAT.parse(date).getTime();
					Date opDate = new Date();
					opDate.setTime(time + 1000l * 3600 * 24 * 31);
					String opDateStr = LONG_DATE_FORMAT.format(opDate);
					String priceDate = datePrice.higherKey(opDateStr);
					if (priceDate == null)
						continue;
					if (!isValidBuyDate(opDateStr, priceDate))
						continue;
					double pe = datePrice.get(priceDate).getOpen() / value;
					if (pe <= 0)
						continue;
					PeRecord pr = new PeRecord();
					pr.setCode(child.getName().substring(2));
					pr.setPe(pe);
					pr.setBuyDate(priceDate);
					Date sellDate = new Date();
					sellDate.setTime(opDate.getTime() + 1000l * 3600 * 24 * 365);
					String sellPriceDate = datePrice.higherKey(LONG_DATE_FORMAT
							.format(sellDate));
					if (sellPriceDate != null)
						pr.setSellDate(sellPriceDate);
					List<PeRecord> list = datePeList.get(date);
					if (list == null) {
						list = new ArrayList<PeRecord>();
						datePeList.put(date, list);
					}
					list.add(pr);
					// LOG.info(pr.toString());
				} catch (ParseException e) {
				}
			}
		}
	}

	private boolean isValidBuyDate(String opDateStr, String priceDate) {
		try {
			Date buyDate = LONG_DATE_FORMAT.parse(priceDate);
			Date opDate = LONG_DATE_FORMAT.parse(opDateStr);
			long time = buyDate.getTime() - opDate.getTime();
			if (time < 0 || time > 1000l * 3600 * 24 * 15)
				return false;
			else
				return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		LOG.info("init...");
		LowestPe strategy = new LowestPe();
		TreeMap<String, List<Operation>> dateOp = strategy.start();
		Set<String> codeSet = new HashSet<String>();
		for (String date : dateOp.keySet()) {
//			System.out.print(date + "\t");
			for (Operation op : dateOp.get(date)) {
//				System.out.print(op.getCode() + ":" + op.isBuy() + "\t");
				codeSet.add(op.getCode());
			}
//			System.out.println();
		}
		LOG.info("read prices...");
		Map<String, TreeMap<String, StockRecord>> codeDatePrice = new HashMap<String, TreeMap<String, StockRecord>>();
		File[] children = new File("data/stockprice").listFiles();
		for (File child : children) {
			if (!codeSet.contains(child.getName().substring(2))) {
				continue;
			}
			TreeMap<String, StockRecord> datePrice = new TreeMap<String, StockRecord>();
			DataReader.readPrice(child.getName(), datePrice);
			codeDatePrice.put(child.getName().substring(2), datePrice);
		}
		LOG.info("sim trade...");
		TradeSimulator s = new TradeSimulator(800000);
		s.setPerBuy(10000d);
		s.trade(codeDatePrice, dateOp, "上证50");
	}

	private class PeRecord implements Comparable<PeRecord> {

		private String code;
		private Double pe;
		private String buyDate;
		private String sellDate;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Double getPe() {
			return pe;
		}

		public void setPe(Double pe) {
			this.pe = pe;
		}

		public String getBuyDate() {
			return buyDate;
		}

		public void setBuyDate(String buyDate) {
			this.buyDate = buyDate;
		}

		public String getSellDate() {
			return sellDate;
		}

		public void setSellDate(String sellDate) {
			this.sellDate = sellDate;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((buyDate == null) ? 0 : buyDate.hashCode());
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result + ((pe == null) ? 0 : pe.hashCode());
			result = prime * result
					+ ((sellDate == null) ? 0 : sellDate.hashCode());
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
			PeRecord other = (PeRecord) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (buyDate == null) {
				if (other.buyDate != null)
					return false;
			} else if (!buyDate.equals(other.buyDate))
				return false;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			if (pe == null) {
				if (other.pe != null)
					return false;
			} else if (!pe.equals(other.pe))
				return false;
			if (sellDate == null) {
				if (other.sellDate != null)
					return false;
			} else if (!sellDate.equals(other.sellDate))
				return false;
			return true;
		}

		@Override
		public int compareTo(PeRecord o) {
			if (this == o || this.equals(o))
				return 0;
			if (pe > o.pe)
				return 1;
			else if (pe < o.pe)
				return -1;
			else if (!code.equals(o.code))
				return code.compareTo(o.code);
			else if (!buyDate.equals(o.buyDate))
				return buyDate.compareTo(o.buyDate);
			else
				return sellDate.compareTo(o.sellDate);
		}

		private LowestPe getOuterType() {
			return LowestPe.this;
		}

		@Override
		public String toString() {
			return "PeRecord [code=" + code + ", pe=" + pe + ", buyDate="
					+ buyDate + ", sellDate=" + sellDate + "]";
		}
	}
}
