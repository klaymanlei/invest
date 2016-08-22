package com.klaymanlei.stocrawler.persistence;

import com.klaymanlei.stocrawler.persistence.hibernate4.Hibernate4Tool;

public class PersistenceToolFactory {

	private final Hibernate4Tool hibernate4Tool = new Hibernate4Tool();

	public PersistenceTool getPersistenceTool() {
		return hibernate4Tool;
	}
}
