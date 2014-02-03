package control;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.IliasFolder;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;

public class LocalPdfStorage {
	private static LocalPdfStorage instance;
	private final Map<Integer, String> localPdfLocations;

	private LocalPdfStorage() {
		this.localPdfLocations = new HashMap<Integer, String>();
	}

	public static LocalPdfStorage getInstance() {
		if (instance == null) {
			instance = new LocalPdfStorage();
		}
		return instance;
	}

	public void refresh() {
		scanFolders(Settings.getInstance().loadLocalIliasFolderPath());
	}

	public Set<Integer> getAllLocalPDFSizes() {
		return localPdfLocations.keySet();
	}

	private void scanFolders(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				scanFolders(file.getAbsolutePath());
			} else if (file.isFile() && file.getPath().toLowerCase().endsWith(".pdf")) {
				localPdfLocations.put((int) file.length(), file.getAbsolutePath());
			}
		}
	}

	public boolean contains(IliasPdf pdf) {
		return localPdfLocations.containsKey(pdf.getSize());
	}

	public void addPdf(IliasPdf pdf, String path) {
		localPdfLocations.put(pdf.getSize(), path);
	}

	public boolean isFolderSynchronized(IliasFolder folder) {
		for (IliasTreeNode node : folder.getChildNodes()) {
			if (node instanceof IliasPdf) {
				IliasPdf pdf = (IliasPdf) node;
				if (!contains(pdf) && !pdf.isIgnored()) {
					return false;
				}

			} else if (node instanceof IliasFolder) {
				if (!isFolderSynchronized((IliasFolder) node)) {
					return false;
				}
			}
		}
		return true;
	}

	private String getContainingFolder(IliasPdf onlineAdresse) {
		final int size = onlineAdresse.getSize();
		String path = localPdfLocations.get(size);
		if (path == null) {
			return null;
		}
		path = path.substring(0, path.lastIndexOf(File.separator));
		return path;
	}

	public String suggestDownloadPath(IliasTreeNode pdf) {
		final List<IliasTreeNode> siblings = pdf.getParentFolder().getChildNodes();
		for (IliasTreeNode node : siblings) {
			if (node instanceof IliasPdf) {
				final String result = getContainingFolder((IliasPdf) node);
				if (result != null) {
					System.out.println(result);
					return result;
				}
			}
		}
		return Settings.getInstance().loadLocalIliasFolderPath();
	}

	public File getFile(IliasPdf pdf) {
		final String location = localPdfLocations.get(pdf.getSize());
		if (location == null) {
			return null;
		}
		return new File(location);
	}
}
