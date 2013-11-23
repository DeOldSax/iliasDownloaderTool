package model;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class StorageProvider {
	private static final String ILIAS_FOLDER = "1";
	private static final String DOWNLOAD_PATH = "2";
	private static Preferences prefsRoot;
	private static Preferences myPrefs;

	public StorageProvider() {
		prefsRoot = Preferences.userRoot();
		myPrefs = prefsRoot.node("DownloaderTool.preferences");
	}

	public void storeLocalIliasFolderPath(String path) {
		myPrefs.put(ILIAS_FOLDER, path);
	}

	public void storeDownloadPath(String path) {
		myPrefs.put(DOWNLOAD_PATH, path);
	}

	public String loadLocalIliasFolderPath() {
		return myPrefs.get(ILIAS_FOLDER, "../Kit/Semester XY");
	}

	public String loadDownloadPath() {
		return myPrefs.get(DOWNLOAD_PATH, "C:\\Users\\%USERNAME%\\Desktop");
	}

	public void removeNode() {
		try {
			myPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void storeCommitVersion(String numberOfCommits) {
		myPrefs.put("commitNumber", numberOfCommits);
	}

	public String getCommitVersion() {
		return myPrefs.get("commitNumber", "-1");
	}

	public void storeUsername(String username) {
		myPrefs.put("username", username);
	}

	public void storePassword(String password) {
		myPrefs.put("password", password);
	}

	public String getUsername() {
		return myPrefs.get("username", "");
	}

	public String getPassword() {
		return myPrefs.get("password", "");
	}

}
