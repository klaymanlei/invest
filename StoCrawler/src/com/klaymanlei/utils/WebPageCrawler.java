package com.klaymanlei.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class WebPageCrawler {

	public static Logger log = Logger
			.getLogger(WebPageCrawler.class);

	public static byte[] DownLoadPages(String urlStr) throws MalformedURLException, IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		int chByte = 0;

		URL url = null;

		HttpURLConnection httpConn = null;

		InputStream in = null;

		try {
			url = new URL(urlStr);
			httpConn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			//httpConn.setRequestProperty("User-Agent", "MSIE 8.0");
			httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");

			in = httpConn.getInputStream();

			chByte = in.read();
			while (chByte != -1) {
				byteStream.write(chByte);
				// System.out.println(chByte);
				chByte = in.read();
			}
			byte[] data = byteStream.toByteArray();
//			return null;
			return data;
		} catch (MalformedURLException e) {
			//log.error("", e);
			throw e;
		} catch (IOException e) {
			//log.error("", e);
			throw e;
		} finally {
			try {
				byteStream.close();
			} catch (Exception ex) {
				//log.error("", ex);
			}
			try {
				in.close();
			} catch (Exception ex) {
				//log.error("", ex);
			}
			try {
				httpConn.disconnect();
			} catch (Exception ex) {
				//log.error("", ex);
			}
		}
	}
}
