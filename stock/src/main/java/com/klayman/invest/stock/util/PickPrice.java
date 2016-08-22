package com.klayman.invest.stock.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class PickPrice {

	public static void main(String[] args) {
		Map<String, File> files = new HashMap<String, File>();
		List<String> outLines = new ArrayList<String>();
		File file = new File("data/newprice");
		String path = "data/";
		File[] children = file.listFiles();
		String month = "2016-02-18";		
		String name = month.replaceAll("-", "");
		for (File child : children) {
			System.out.println("parsing " + child.getName());
			try {
				List<String> lines = FileUtils.readLines(child);
				for (String line : lines) {
					if (StringUtils.isEmpty(line) || line.startsWith("Date"))
						continue;
					if (!line.startsWith(month))
						continue;
					outLines.add(child.getName().substring(2) + "," + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File out = files.get(name);
		if (out == null) {
			out = new File(path + month);
			files.put(month, out);
		}
		try {
			FileUtils.writeLines(out, outLines, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
