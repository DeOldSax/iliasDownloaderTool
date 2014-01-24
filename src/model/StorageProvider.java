package model;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class StorageProvider {
	private static final String ILIAS_FOLDER = "1";
	private static Preferences prefsRoot;
	private static Preferences myPrefs;

	public StorageProvider() {
		prefsRoot = Preferences.userRoot();
		myPrefs = prefsRoot.node("DownloaderTool.preferences");
	}

	public void storeLocalIliasFolderPath(String path) {
		myPrefs.put(ILIAS_FOLDER, path);
	}

	public String loadLocalIliasFolderPath() {
		return myPrefs.get(ILIAS_FOLDER, ".");
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

	public void storeIgnoredPdfSize(PDF pdf) {
		String key = createKey(pdf);
		myPrefs.putInt(key, pdf.getSize());
	}

	public int isIgnored(PDF pdf) {
		String key = createKey(pdf);
		return myPrefs.getInt(key, -1);
	}

	public void removeIgnoredPdfSize(PDF pdf) {
		myPrefs.remove(createKey(pdf));
	}

	private String createKey(PDF pdf) {
		String key = pdf.getUrl();
		// FIXME !!!!!!!
		if (key == null) {
			return "";
		}
		final int beginIndex = key.indexOf("ref_id=");
		final int endIndex = key.indexOf("&cmd=sendfile");
		key = key.substring(beginIndex + 7, endIndex);
		return key;
	}

	public boolean localIliasPathIsAlreadySet() {
		return myPrefs.getBoolean("localIliasPathIsAlreadySet", false);
	}

	public void setLocalIliasPathTrue() {
		myPrefs.putBoolean("localIliasPathIsAlreadySet", true);
	}

	public void removeNode() {
		try {
			myPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
