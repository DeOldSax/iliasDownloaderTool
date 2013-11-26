package worker;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Directory;
import model.PDF;
import view.LocalFolderService;

public class LocalDataReader {
	private final List<Integer> localDataList;
	private final Map<Integer, String> localPdfWithParents;

	public LocalDataReader() {
		this.localDataList = new LinkedList<Integer>();
		this.localPdfWithParents = new HashMap<Integer, String>();
	}

	public List<Integer> getAllLocalPDFSizes() {
		return scanFolders(LocalFolderService.getLocalIliasPathString());
	}

	public Map<Integer, String> getAllLocalPDFSizesAsMap() {
		scanFolders(LocalFolderService.getLocalIliasPathString());
		return localPdfWithParents;
	}

	private List<Integer> scanFolders(String path) {
		File dir = new File(path);

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				scanFolders(file.getAbsolutePath());
			} else if (file.isFile() && file.getPath().toLowerCase().endsWith(".pdf")) {
				localDataList.add((int) file.length());
				localPdfWithParents.put((int) file.length(), path);
			}
		}
		return localDataList;
	}

	private String searchLocalParentFolder(PDF onlineAdresse) {
		scanFolders(LocalFolderService.getLocalIliasPathString());
		final int size = onlineAdresse.getSize();
		final String path = localPdfWithParents.get(size);
		return path;
	}

	public String findLocalDownloadPath(Directory pdf) {
		final List<Directory> childFolders = pdf.getParentDirectory().getChildFolders();
		for (Directory directory : childFolders) {
			if (directory instanceof PDF) {
				final String result = searchLocalParentFolder((PDF) directory);
				if (result != null) {
					return result;
				}
			}
		}
		return LocalFolderService.getLocalIliasPathString();
	}
}
