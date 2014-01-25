package control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Directory;
import model.PDF;
import model.StorageProvider;

public class LocalDataReader {
	private final List<Integer> localDataList;
	private final Map<Integer, String> localPdfWithParents;
	private final StorageProvider storageProvider;
	private File file;

	public LocalDataReader() {
		this.localDataList = new ArrayList<Integer>();
		this.localPdfWithParents = new HashMap<Integer, String>();
		this.storageProvider = new StorageProvider();
	}

	public List<Integer> getAllLocalPDFSizes() {
		return scanFolders(storageProvider.loadLocalIliasFolderPath());
	}

	public Map<Integer, String> getAllLocalPDFSizesAsMap() {
		scanFolders(storageProvider.loadLocalIliasFolderPath());
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
		scanFolders(storageProvider.loadLocalIliasFolderPath());
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
		return storageProvider.loadLocalIliasFolderPath();
	}

	public File findFileOnLocalDisk(PDF pdf) {
		scanForPath(storageProvider.loadLocalIliasFolderPath(), pdf);
		if (file != null) {
			return file;
		}
		return null;
	}

	private void scanForPath(String path, PDF pdf) {
		File dir = new File(path);

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				scanForPath(file.getAbsolutePath(), pdf);
			}
			if ((int) file.length() == pdf.getSize()) {
				this.file = file;
			}
		}
	}
}
