package com.klayman.invest.stock.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class MergeSeasonData {

	public static void main(String[] args) throws ParseException {
		Map<String, File> files = new HashMap<String, File>();
		Map<String, List<String>> outMap = new HashMap<String, List<String>>();
		File file = new File("data/stockprice");
		String path = "data/seasondata/";
		File[] children = file.listFiles();
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String currentSeason = StockUtils.getSeason(StockUtils.MS_FORMAT.format(now.getTime()));
		System.out.println("current season is " + currentSeason);
		for (File child : children) {
			outMap.clear();
			System.out.println("parsing " + child.getName());
			try {
				List<String> lines = FileUtils.readLines(child);
				String[] prev = null;
				for (String line : lines) {
					if (StringUtils.isEmpty(line) || line.startsWith("Date"))
						continue;
					String[] props = line.split("\\,");
					// 如果开盘、最高、最低、收盘价、调整价与前一天相等，且成交量为0，该行跳过
					if (prev != null && prev[1].equals(props[1])
							&& prev[2].equals(props[2])
							&& prev[3].equals(props[3])
							&& prev[4].equals(props[4])
							&& Long.parseLong(props[5]) == 0
							&& prev[6].equals(props[6]))
						continue;
					else
						prev = props;
					
					String date = props[0];

					String season = StockUtils.getSeason(date);
					if (!season.equals(currentSeason))
						continue;
					List<String> outList = outMap.get(season);
					if (outList == null) {
						outList = new ArrayList<String>();
						outMap.put(season, outList);
					}
					outList.add(child.getName().substring(2) + "," + line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (String name : outMap.keySet()) {
				File out = files.get(name);
				if (out == null) {
					out = new File(path + name);
					files.put(name, out);
				}
				try {
					FileUtils.writeLines(out, outMap.get(name), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
