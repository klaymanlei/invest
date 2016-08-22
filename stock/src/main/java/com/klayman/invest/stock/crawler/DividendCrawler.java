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

public class DividendCrawler {
	private static final Logger LOG = LoggerFactory
			.getLogger(DividendCrawler.class);
	private static final String SPACE_HOLDER = "<STOCK_CODE>";
	private static final String URL = "http://stockdata.stock.hexun.com/2009_fhzzgb_"
			+ SPACE_HOLDER + ".shtml";

	public static void main(String[] args) {
		File parent = new File("data/stockprice");
		File[] childs = parent.listFiles();
		StringBuffer line = new StringBuffer();
		for (File file : childs) {
			if (file.getName().startsWith("SS"))
				continue;
			File report = new File("data/dividend/" + file.getName());
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

					Elements contents = doc.getElementsByAttributeValue("id",
							"zaiyaocontent");
					for (Element zaiyaocontent : contents) {
						Elements children = zaiyaocontent.children();
						for (Element e : children) {
							if (e.nodeName().equals("table")) {
								Elements trs = e.child(0).children();
								for (Element tr : trs) {
									if (tr.childNodeSize() < 6)
										continue;
									try {
										double money = Double.parseDouble(tr
												.child(1).child(0).text());
										double share1 = Double.parseDouble(tr
												.child(2).child(0).text());
										double share2 = Double.parseDouble(tr
												.child(3).child(0).text());
										String date = tr.child(6).child(0)
												.text();
										FileUtils.write(report, date + ", " + money
												+ ", " + share1 + ", "
												+ share2 + "\n", true);
//										System.out
//												.println(date + ", " + money
//														+ ", " + share1 + ", "
//														+ share2);
									} catch (NumberFormatException e1) {
//										LOG.error("", e1);
									}
								}
							}
						}
						break;
					}
					break;
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
			// break;
		}
	}

}
