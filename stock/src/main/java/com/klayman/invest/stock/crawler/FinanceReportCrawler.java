package com.klayman.invest.stock.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.klayman.invest.stock.util.StockUtils;
import com.klaymanlei.utils.Utilities;
import com.klaymanlei.utils.WebPageCrawler;

public class FinanceReportCrawler {
	// private static final String PLACEHOLDER_SEASON = "<SEASON>";
	// private static final String PLACEHOLDER_YEAR = "<YEAR>";
	// private static final String PLACEHOLDER_STOCK_CODE = "<STOCK_CODE>";

	// http://stockdata.stock.hexun.com/2008/xjll.aspx?stockid=600010&accountdate=2014.09.30

	private static final String[] URLS = {
			"http://stockdata.stock.hexun.com/2008/cwbl.aspx",
			"http://stockdata.stock.hexun.com/2008/zcfz.aspx",
			"http://stockdata.stock.hexun.com/2008/lr.aspx",
			"http://stockdata.stock.hexun.com/2008/xjll.aspx" };
	private static final String[] TYPES = {"财务比率", "资产负债表", "利润表", "现金流量表"};
	
	public static final String PARAMS_STOCK_ID = "stockid=";
	public static final String PARAMS_ACCOUNT_DATE = "&accountdate=";

	public static final String SEPARATOR = ".";

	private static final String[] SEASON = { "03.15", "06.30", "09.30", "12.31" };

	private static String parseHexun(String page) throws IOException {
		BufferedReader in = new BufferedReader(new StringReader(page));
		String line = null;
		while ((line = in.readLine()) != null) {
			// FileUtils.write(file, new String(line + "\n"), true);
			if (line.contains("class='tishi'")) {
				// line = line.replaceAll("<td class='dotborder' width='45%'>",
				// "\n");
				// line = line.replaceAll("<div class='tishi'>", ",");
				// line = line.replaceAll("<tr>", "");
				// line = line.replaceAll("</div>", "");
				// line = line.replaceAll("</tr>", "");
				// line = line.replaceAll("<td>", "");
				// line = line.replaceAll("</td>", "");
				// line = line.replaceAll("<strong>", "");
				// line = line.replaceAll("</strong>", "");
				// System.out.println(line);
			}
		}
		return null;
	}

	private static List<String> parseReportUrls(String seedUrl) {
		List<String> urls = new ArrayList<String>();
		try {
			String page = new String(WebPageCrawler.DownLoadPages(seedUrl),
					"gbk");
			String line = Utilities.findLine(page, "年年度");
			if (Utilities.isEmpty(line))
				return urls;
			line = line.trim();
			StringBuffer strBuffer = new StringBuffer(line);
			// cut prefix
			Utilities.cutBefore(strBuffer, "dateurl=\"");
			String reportHistUrl = Utilities.cutBefore(strBuffer, "\"");
			while (true) {
				String dateStr = Utilities.cutSubString(strBuffer, "['", "',");
				// log.debug(dateStr);
				if (Utilities.isEmpty(dateStr))
					break;
				urls.add(reportHistUrl + dateStr);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urls;
	}

	public static void main2(String[] args) throws IOException {
		StringBuffer params = new StringBuffer();
		File parent = new File("E:/develop/jars/filter.txt");
		List<String> content = FileUtils.readLines(parent);
		StringBuffer line = new StringBuffer();
		int count = 0;
		for (String l : content) {
			count++;
			System.out.println(count + "/" + content.size());
			String[] array = l.split("\t");
//			if (file.getName().startsWith("SS"))
//				continue;
			String code = array[0];
			String dt = array[1];
//			if (code.compareTo("300255") < 0)
//				continue;
			for (int i = 0; i < URLS.length; i++) {
				String url = URLS[i];
				String type = TYPES[i];
				params.setLength(0);
				params.append(PARAMS_STOCK_ID).append(code)
						.append(PARAMS_ACCOUNT_DATE).append(dt);
				String reportUrl = url + "?" + params.toString();
				System.out.println(url + "?"
						+ params.toString());
					int retry = 0;
					String page = null;
					while (retry++ < 1) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						try {
							System.out.println(reportUrl);
							int index = reportUrl.indexOf(PARAMS_ACCOUNT_DATE) + PARAMS_ACCOUNT_DATE.length();
							String date = reportUrl.substring(index, index + 10).replaceAll("\\.", "-");
							String season = StockUtils.getReportSeason(date);
							page = new String(
									WebPageCrawler.DownLoadPages(reportUrl),
									"gbk");

							Document doc = Jsoup.parse(page);
							Elements contents = doc
									.getElementsByAttributeValue("class",
											"dotborder");
							List<String> data = new ArrayList<String>();
							for (Element e : contents) {
								Element p = e.parent();
								Elements children = p.children();
								line.setLength(0);
								for (Element c : children) {
									line.append(c.text().replaceAll(",", ""))
											.append(" ");
								}
								data.add(code + "," + type + "," + date + "," + line.toString().trim()
										.replaceAll(" ", ","));
							}
							File report = new File("data/appendreport/" + season.substring(0, 7) + "/" + season);
							FileUtils.writeLines(report, "utf-8", data, true);
							break;
						} catch (IOException e) {
							System.out.println(e.getMessage());
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				
				System.out.println("reports downloaded");
			}
		}

	}
	
	private static void parseUrls() throws IOException {
		StringBuffer buf = new StringBuffer();
		File parent = new File("/home/hadoop/data/stockprice");
		File[] childrenFile = parent.listFiles();
		StringBuffer params = new StringBuffer();
		TreeSet<String> allUrls = new TreeSet<String>();
		int count = 0;
		for (File file : childrenFile) {
			count++;
//			if (file.getName().startsWith("SS"))
//				continue;
			String code = file.getName().substring(2);
//			if (code.compareTo("300255") < 0)
//				continue;
			System.out.println("parse (" + count + "/" + childrenFile.length + "):\t" + code);
			for (int i = 0; i < URLS.length; i++) {
				String url = URLS[i];
				String type = TYPES[i];
				params.setLength(0);
				params.append(PARAMS_STOCK_ID).append(code)
						.append(PARAMS_ACCOUNT_DATE).append(2015)
						.append(SEPARATOR).append("06.30");
				List<String> urls = parseReportUrls(url + "?"
						+ params.toString());
				for (String u : urls) {
					String date = u.substring(u.length() - 10);
					buf.setLength(0);
					buf.append(date).append("\t").append(code).append("\t").append(type).append("\t").append(u);
					if (date.equals("2016.03.15"))
						allUrls.add(buf.toString());
				}
			}
		}
		FileUtils.writeLines(new File("/home/hadoop/data/statements_allurls_2016_03"), allUrls);
	}
	
	private static TreeSet<String> readUrls() throws IOException {
		List<String> lines = FileUtils.readLines(new File("/home/hadoop/data/statements_allurls_2016_03"));
		TreeSet<String> allUrls = new TreeSet<String>();
		for (String line : lines) {
			allUrls.add(line);
		}
		return allUrls;
	}
	
	/**
	 * 解析hexun股票财报页面，获取历史财报url，抓取所有历史财报
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		StringBuffer line = new StringBuffer();
		parseUrls();
		TreeSet<String> allUrls = readUrls();
		int count = 0;
		for (;0 < allUrls.size();) {
			String str = allUrls.last();
			allUrls.remove(str);
			count++;
			String[] array = str.split("\t");
			String code = array[1];
			String type = array[2];
			String reportUrl = array[3];
			int retry = 0;
			String page = null;
			while (retry++ < 1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				try {
					System.out.println(reportUrl);
					int index = reportUrl.indexOf(PARAMS_ACCOUNT_DATE) + PARAMS_ACCOUNT_DATE.length();
					String date = reportUrl.substring(index, index + 10).replaceAll("\\.", "-");
					String season = StockUtils.getReportSeason(date);
					page = new String(
							WebPageCrawler.DownLoadPages(reportUrl),
							"gbk");

					Document doc = Jsoup.parse(page);
					Elements contents = doc
							.getElementsByAttributeValue("class",
									"dotborder");
					List<String> data = new ArrayList<String>();
					for (Element e : contents) {
						Element p = e.parent();
						Elements children = p.children();
						line.setLength(0);
						for (Element c : children) {
							line.append(c.text().replaceAll(",", ""))
									.append(" ");
						}
						data.add(code + "," + type + "," + date + "," + line.toString().trim()
								.replaceAll(" ", ","));
					}
					File report = new File("/home/hadoop/data/statements/" + season.substring(0, 7) + "/" + season);
					FileUtils.writeLines(report, "utf-8", data, true);
					break;
				} catch (IOException e) {
					System.out.println(e.getMessage());
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			System.out.println(count + "/" + allUrls.size() + " urls downloaded.");
			FileUtils.writeLines(new File("/home/hadoop/data/statements_retains_2016_03"), allUrls);
		}
	}
	
	/**
	 * 解析hexun股票财报页面，获取历史财报url，抓取所有历史财报
	 * @param args
	 */
	public static void main3(String[] args) {
		StringBuffer params = new StringBuffer();
		File parent = new File("/home/hadoop/data/stockprice");
		File[] childrenFile = parent.listFiles();
		StringBuffer line = new StringBuffer();
		int count = 0;
		for (File file : childrenFile) {
			count++;
//			if (file.getName().startsWith("SS"))
//				continue;
			String code = file.getName().substring(2);
//			if (code.compareTo("300255") < 0)
//				continue;
			System.out.println(count + "/" + childrenFile.length + "\t" + code);
			for (int i = 0; i < URLS.length; i++) {
				String url = URLS[i];
				String type = TYPES[i];
				params.setLength(0);
				params.append(PARAMS_STOCK_ID).append(code)
						.append(PARAMS_ACCOUNT_DATE).append(2015)
						.append(SEPARATOR).append("06.30");
				List<String> urls = parseReportUrls(url + "?"
						+ params.toString());
				for (String reportUrl : urls) {
					int retry = 0;
					String page = null;
					while (retry++ < 1) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						try {
							System.out.println(reportUrl);
							int index = reportUrl.indexOf(PARAMS_ACCOUNT_DATE) + PARAMS_ACCOUNT_DATE.length();
							String date = reportUrl.substring(index, index + 10).replaceAll("\\.", "-");
							String season = StockUtils.getReportSeason(date);
							page = new String(
									WebPageCrawler.DownLoadPages(reportUrl),
									"gbk");

							Document doc = Jsoup.parse(page);
							Elements contents = doc
									.getElementsByAttributeValue("class",
											"dotborder");
							List<String> data = new ArrayList<String>();
							for (Element e : contents) {
								Element p = e.parent();
								Elements children = p.children();
								line.setLength(0);
								for (Element c : children) {
									line.append(c.text().replaceAll(",", ""))
											.append(" ");
								}
								data.add(code + "," + type + "," + date + "," + line.toString().trim()
										.replaceAll(" ", ","));
							}
							File report = new File("/home/hadoop/data/statements/" + season.substring(0, 7) + "/" + season);
							FileUtils.writeLines(report, "utf-8", data, true);
							break;
						} catch (IOException e) {
							System.out.println(e.getMessage());
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				System.out.println("reports downloaded");
			}
		}

	}

	public static void main1(String[] args) {
		StringBuffer params = new StringBuffer();
		File parent = new File("/home/hadoop/data/stockprice");
		File[] childs = parent.listFiles();
		StringBuffer line = new StringBuffer();
		for (File file : childs) {
			File report = new File("/home/hadoop/data/report/" + file.getName());
			String code = file.getName().substring(2);
			if (code.compareTo("600567") < 0)
				continue;
			for (int i = 1989; i < 2016; i++) {
				for (String season : SEASON) {
					params.setLength(0);

					params.append(PARAMS_STOCK_ID).append(code)
							.append(PARAMS_ACCOUNT_DATE).append(i)
							.append(SEPARATOR).append(season);
					for (String url : URLS) {
						int retry = 0;
						String page = null;
						while (retry++ < 1) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							}
							try {
								System.out.println(url + "?"
										+ params.toString());
								// page = HttpCrawler.sendGet(url,
								// params.toString(), null);
								page = new String(
										WebPageCrawler.DownLoadPages(url + "?"
												+ params.toString()), "gbk");

								Document doc = Jsoup.parse(page);
								Elements contents = doc
										.getElementsByAttributeValue("class",
												"dotborder");
								List<String> data = new ArrayList<String>();
								for (Element e : contents) {
									Element p = e.parent();
									Elements children = p.children();
									line.setLength(0);
									for (Element c : children) {
										line.append(
												c.text().replaceAll(",", ""))
												.append(" ");
									}
									data.add(line.toString().trim()
											.replaceAll(" ", ","));
								}
								FileUtils.writeLines(report, "gbk", data, true);
								break;
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}
						}
					}
				}
			}
		}

	}
}
