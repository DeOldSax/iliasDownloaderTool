package control;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.IliasFile;
import model.IliasFolder;
import model.IliasTreeNode;
import model.persistance.Settings;

/**
 * This class stores all Files. Currently a .zip {@link IliasZip} is treated as a "File" and derives from {@link IliasFile}.
 * 
 * @author David
 *
 */
public class LocalFileStorage {
	private static LocalFileStorage instance;
	private final Map<Integer, String> localFileLocations;

	private LocalFileStorage() {
		this.localFileLocations = new HashMap<Integer, String>();
	}

	public static LocalFileStorage getInstance() {
		if (instance == null) {
			instance = new LocalFileStorage();
		}
		return instance;
	}

	public void refresh() {
		scanFolders(Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath());
	}

	public Set<Integer> getAllLocalFileSizes() {
		return localFileLocations.keySet();
	}

	private void scanFolders(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				scanFolders(file.getAbsolutePath());
														//.zip is not detected as a "file"
			} else if (file.isFile() || file.getPath().toLowerCase().endsWith(".zip")) {
				localFileLocations.put((int) file.length(), file.getAbsolutePath());
			}
		}
	}

	public boolean contains(IliasFile iliasFile) {
		return localFileLocations.containsKey(iliasFile.getSize());
	}

	public void addIliasFile(IliasFile iliasFile, String path) {
		localFileLocations.put(iliasFile.getSize(), path);
	}

	public boolean isFolderSynchronized(IliasFolder folder) {
		for (IliasTreeNode node : folder.getChildNodes()) {
			if (node instanceof IliasFile) {
				IliasFile iliasFile = (IliasFile) node;
				if (!contains(iliasFile) && !iliasFile.isIgnored()) {
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

	public String suggestDownloadPath(IliasFile iliasFile) {
		final List<IliasTreeNode> siblings = iliasFile.getParentFolder().getChildNodes();
		for (IliasTreeNode node : siblings) {
			if (node instanceof IliasFile) {
				final String result = getContainingFolder((IliasFile) node);
				if (result != null) {
					return result;
				}
			}
		}
		return Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath();
	}

	private String getContainingFolder(IliasFile iliasFile) {
		final int size = iliasFile.getSize();
		String path = localFileLocations.get(size);
		if (path == null) {
			return null;
		}
		path = path.substring(0, path.lastIndexOf(File.separator));
		return path;
	}


	public File getFile(IliasFile iliasFile) {
		final String location = localFileLocations.get(iliasFile.getSize());
		if (location == null) {
			return null;
		}
		return new File(location);
	}
}
