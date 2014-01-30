package control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Directory;
import model.PDF;
import model.Settings;

public class LocalDataReader {
	private final List<Integer> localDataList;
	private final Map<Integer, String> localPdfWithParents;
	private final Settings settings;
	private File file;

	public LocalDataReader() {
		this.localDataList = new ArrayList<Integer>();
		this.localPdfWithParents = new HashMap<Integer, String>();
		this.settings = Settings.getInstance();
	}

	public List<Integer> getAllLocalPDFSizes() {
		return scanFolders(settings.loadLocalIliasFolderPath());
	}

	public Map<Integer, String> getAllLocalPDFSizesAsMap() {
		scanFolders(settings.loadLocalIliasFolderPath());
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
		scanFolders(settings.loadLocalIliasFolderPath());
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
		return settings.loadLocalIliasFolderPath();
	}

	public File findFileOnLocalDisk(PDF pdf) {
		file = null;
		scanForPath(settings.loadLocalIliasFolderPath(), pdf);
		if (file != null) {
			pdf.setFileOnLocalDisk(file);
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
