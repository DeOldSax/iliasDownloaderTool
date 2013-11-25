package model;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import view.LocalFolderService;

public class LocalDataReader {
	private final List<Integer> localDataList;
	private final Map<Integer, String> localPdfWithParents;

	public LocalDataReader() {
		this.localDataList = new LinkedList<Integer>();
		this.localPdfWithParents = new HashMap<Integer, String>();
	}

	public List<Integer> searchPdf(String path) {
		File dir = new File(path);

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				searchPdf(file.getAbsolutePath());
			} else if (file.isFile() && file.getPath().toLowerCase().endsWith(".pdf")) {
				localDataList.add((int) file.length());
				localPdfWithParents.put((int) file.length(), path);
			}
		}
		return localDataList;
	}

	public String searchLocalParentFolder(Adresse onlineAdresse) {
		searchPdf(LocalFolderService.getLocalIliasPathString());
		final int size = (int) onlineAdresse.getSize();
		final String path = localPdfWithParents.get(size);
		return path;
	}

	public String findLocalDownloadPath(Adresse adresse) {
		final List<Adresse> childFolders = adresse.getParentFolder().getChildFolders();
		for (Adresse dir : childFolders) {
			if (dir.isPdf()) {
				final String result = searchLocalParentFolder(dir);
				if (result != null) {
					return result;
				}
			}
		}
		return LocalFolderService.getLocalIliasPathString();
	}
}
