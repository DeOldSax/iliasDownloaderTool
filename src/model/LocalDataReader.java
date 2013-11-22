package model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class LocalDataReader {
	private final List<Double> localDataList;

	public LocalDataReader() {
		this.localDataList = new LinkedList<Double>();
	}

	public List<Double> searchPdf(String path) {
		File dir = new File(path);

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				searchPdf(file.getAbsolutePath());
			} else if (file.isFile() && file.getPath().toLowerCase().endsWith(".pdf")) {
				localDataList.add((double) file.length());
			}
		}
		return localDataList;
	}
}
