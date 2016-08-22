package com.klayman.invest.stock.crawler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klaymanlei.utils.WebPageCrawler;

public class IndustryCrawler {
	private static final Logger LOG = LoggerFactory.getLogger(IndustryCrawler.class);
	private static final String SPACE_HOLDER = "<STOCK_CODE>";
	private static final String URL = "http://stockdata.stock.hexun.com/"
			+ SPACE_HOLDER + ".shtml";
	private static final String START_CODE = "SZ002538";

	public static void main(String[] args) {
		File report = new File("data/icb");
		File parent = new File("data/stockprice");
		File[] childs = parent.listFiles();
		// StringBuffer line = new StringBuffer();
		for (File file : childs) {
			if (file.getName().startsWith("SS"))
				continue;
			if (file.getName().compareTo(START_CODE) < 0)
				continue;
			String code = file.getName().substring(2);
			String url = URL.replaceAll(SPACE_HOLDER, code);
			String page = null;
			int retry = 0;
			while (retry++ < 1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				try {
					LOG.info(url);
					page = new String(WebPageCrawler.DownLoadPages(url), "gbk");
					Document doc = Jsoup.parse(page);

					Elements contents = doc
							.getElementsByAttributeValueContaining("href",
									"icb.aspx");
					for (Element e : contents) {
						FileUtils.write(report, code + "," + e.text() + "\n", true);
						break;
					}
					break;
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
			//break;
		}
	}

}
