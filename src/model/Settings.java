package model;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
	private static Settings settings = null;

	private static final String ILIAS_FOLDER = "ILIAS_FOLDER";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String LNT = "LNT_*";
	private static Preferences prefsRoot;
	private static Preferences myPrefs;

	public Settings() {
		prefsRoot = Preferences.userRoot();
		myPrefs = prefsRoot.node("DownloaderTool.preferences");
	}

	public static Settings getInstance() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	public void storeLocalIliasFolderPath(String path) {
		myPrefs.put(ILIAS_FOLDER, path);
	}

	public String loadLocalIliasFolderPath() {
		return myPrefs.get(ILIAS_FOLDER, ".");
	}

	public void storeUsername(String username) {
		myPrefs.put(USERNAME, username);
	}

	public void storePassword(String password) {
		myPrefs.put(PASSWORD, password);
	}

	public String getUsername() {
		return myPrefs.get(USERNAME, "");
	}

	public String getPassword() {
		return myPrefs.get(PASSWORD, "");
	}

	public void storeIgnoredPdfSize(String key, int size) {
		myPrefs.putInt(key, size);
	}

	public int isIgnored(String key) {
		return myPrefs.getInt(key, -1);
	}

	public void removeIgnoredPdfSize(String key) {
		myPrefs.remove(key);
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

	// public static void main(String[] args) {
	// getInstance().removeNode();
	// }

	public void setLogIn(boolean b) {
		myPrefs.putBoolean("#LOGIN#", b);
	}

	public boolean userIsLoggedIn() {
		return myPrefs.getBoolean("#LOGIN#", false);
	}

	public boolean updateCanceled() {
		return myPrefs.getBoolean("UpdateCanceled!?", false);
	}

	public void setUpdateCanceled(boolean b) {
		myPrefs.putBoolean("UpdateCanceled!?", b);
	}

	public void setAutoUpdate(boolean b) {
		myPrefs.putBoolean("AUTO_UPDATE", b);
	}

	public boolean autoUpdate() {
		return myPrefs.getBoolean("AUTO_UPDATE", false);
	}

	public void setAutoLogin(boolean b) {
		myPrefs.putBoolean("AUTO_LOGIN", b);
	}

	public boolean autoLogin() {
		return myPrefs.getBoolean("AUTO_LOGIN", false);
	}

	public boolean oneInstanceIsAlreadyOpen() {
		return myPrefs.getBoolean("INSTANCE_ALREADY_OPEN", false);
	}

	public void setOpen(boolean open) {
		myPrefs.putBoolean("INSTANCE_ALREADY_OPEN", open);
	}

	public void storeLocalNotThere(String key, boolean value) {
		myPrefs.putBoolean(key + LNT, value);
	}

	public boolean loadLocalNotThere(String key) {
		return myPrefs.getBoolean(key + LNT, true);
	}
}
