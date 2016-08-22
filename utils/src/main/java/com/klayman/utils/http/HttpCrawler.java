package com.klayman.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCrawler {
	private static final Logger LOG = LoggerFactory
			.getLogger(HttpCrawler.class);

	public static String sendGet(String url, String param,
			Map<String, List<String>> headerFields) throws IOException {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (param != null && !"".equals(param))
				urlNameString += "?" + param;
			//LOG.info(urlNameString);
			URL realUrl = new URL(urlNameString);

			URLConnection connection = realUrl.openConnection();

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			connection.connect();

			if (headerFields != null) {
				headerFields.clear();
				Map<String, List<String>> map = connection.getHeaderFields();

				for (String key : map.keySet()) {
					headerFields.put(key, map.get(key));
				}
			}

			// return connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
		return result;
	}
}
