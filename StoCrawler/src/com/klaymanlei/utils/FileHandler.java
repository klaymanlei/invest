package com.klaymanlei.utils;

import java.io.*;

public class FileHandler {

	private String path;
	private FileWriter fileWriter = null;
	private BufferedWriter bufferedWriter = null;
	private FileReader fileReader = null;
	private BufferedReader bufferedReader = null;
	
	public FileHandler(String path) {
		this.path = path;
	}

	synchronized public void openRead() throws IOException {
		if (fileReader != null || bufferedReader != null)
			throw new IOException("Already open " + path + " for read");
		fileReader = new FileReader(path);
		bufferedReader = new BufferedReader(fileReader);
	}

	public String read() throws IOException {
		String content = null;
		if (bufferedReader.ready()) 
			content = bufferedReader.readLine();
		return content;
	}
	
	synchronized public void closeRead() throws IOException {
		if (fileReader == null || bufferedReader == null)
			throw new IOException("Already closed for read");

		bufferedReader.close();
		fileReader.close();
		
		bufferedReader = null;
		fileReader = null;
	}

	synchronized public void openWrite() throws IOException {
		if (fileWriter != null || bufferedWriter != null)
			throw new IOException("Already open " + path + " for write");
		fileWriter = new FileWriter(path);
		bufferedWriter = new BufferedWriter(fileWriter);
	}
	
	synchronized public void closeWrite() throws IOException {
		if (fileWriter == null || bufferedWriter == null)
			throw new IOException("Already closed for write");

		bufferedWriter.close();
		fileWriter.close();
		
		bufferedWriter = null;
		fileWriter = null;
	}
	
	public void append(String content) throws IOException {

		bufferedWriter.write(content);
		bufferedWriter.newLine();

		bufferedWriter.flush();
	}
}