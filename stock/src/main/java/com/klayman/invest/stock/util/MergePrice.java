package com.klayman.invest.stock.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MergePrice {

	public static void main(String[] args) throws IOException {
		File old = new File("data/stockprice");
		File[] olds = old.listFiles();
		for (File f : olds) {
			List<String> lines = FileUtils.readLines(f);
			String header = null;
			String name = f.getName();
			File outFile = new File("data/newprice/" + name);
			if (lines.get(0).contains("Date"))
				header = lines.remove(0);
			Collections.sort(lines);
			for (int i = 0; i < lines.size(); i++) {
				while (i < lines.size() && lines.get(i).startsWith("http")) {
					lines.remove(i);
				}
			}
			lines.add(0, header);
			FileUtils.writeLines(outFile, lines, true);
		}
		System.out.println("Done");
	}

}
