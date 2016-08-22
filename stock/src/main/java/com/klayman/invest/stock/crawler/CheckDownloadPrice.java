package com.klayman.invest.stock.crawler;

import java.io.File;

public class CheckDownloadPrice {

	public static void main(String[] args) {
		File[] newFiles = new File("E:/develop/workspace/stock/data/newprice").listFiles();
		File[] oldFiles = new File("E:/develop/workspace/stock/data/stockprice").listFiles();
		boolean match = false;
		for (File f : oldFiles) {			
			match = false;
			for (File n : newFiles) {
				if (f.getName().equals(n.getName()))
				{
					match = true;
					break;
				}
			}
			if (!match)
				System.out.println(f.getName());
		}
	}

}
