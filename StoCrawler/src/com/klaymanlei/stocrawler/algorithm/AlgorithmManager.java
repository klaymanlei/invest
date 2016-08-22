package com.klaymanlei.stocrawler.algorithm;

public class AlgorithmManager {

	public static final int NET_CURRENT_ASSETS = 0;
	private static final Algorithm netCurrentAssets = new AlgNetCurrentAssets();
	
	public static Algorithm getAlgorithm(int kind){
		switch (kind) {
		case NET_CURRENT_ASSETS:
			return netCurrentAssets;
		}
		return null;
	}
}
