package com.klaymanlei.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

public class Utilities {

	/**
	 * @param args
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s);
	}

	/**
	 * @param args
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.size() == 0;
	}

	/**
	 * @param args
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.size() == 0;
	}

	public static boolean verifyParam(String name, String param) {
		if (Utilities.isEmpty(param))
			throw new NullPointerException("Param " + name + " is empty");
		return true;
	}

	public static boolean verifyParam(String name, Map<?, ?> param) {
		if (Utilities.isEmpty(param))
			throw new NullPointerException("Param " + name + " is empty");
		return true;
	}

	public static boolean verifyParam(String name, Object param) {
		if (param == null)
			throw new NullPointerException("Param " + name + " is empty");
		return true;
	}

	public static String findLine(String str, String key) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(str));
		try {
			String line = reader.readLine();
			// 查找包含key字符串的那一行数据
			while (line != null) {
				StringBuffer strBuffer = new StringBuffer(line);
				if (strBuffer.indexOf(key) != -1) {
					return line;
				}
				line = reader.readLine();
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String cutBefore(StringBuffer strBuffer,
			String end) {
		int nameEnd = strBuffer.indexOf(end);
		if (nameEnd < 0)
			return null;
		String name = strBuffer.substring(0, nameEnd);
		strBuffer.delete(0, nameEnd + end.length());
		return name;
	}

	public static String cutSubString(StringBuffer strBuffer, String start,
			String end) {
		// System.out.println(start + ": " + end);
		int startIndex = strBuffer.indexOf(start);
		if (startIndex < 0)
			return null;
		int endIndex = strBuffer.indexOf(end);
		if (endIndex < 0)
			return null;
		String str = strBuffer.substring(startIndex + start.length(), endIndex);
		// System.out.println(nameStart + ": " + nameEnd + ":" +
		// strBuffer.length());
		strBuffer.delete(0, endIndex + end.length());
		return str;
	}
	
}
