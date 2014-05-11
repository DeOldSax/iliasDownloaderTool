package model;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {

	private static final String ILIAS_FOLDER = "1";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String UPDATE_CANCELED = "UpdateCanceled!?";
	private static final String LOCAL_ILIAS_PATH_IS_SET = "localIliasPathIsAlreadySet";
	private static final String USER_IS_LOGGED_IN = "#LOGIN#";
	private static final String LNT = "LNT_*";
	private static Preferences prefsRoot;
	private static Preferences myPrefs;
	private static Settings instance = null;

	private Settings() {
		prefsRoot = Preferences.userRoot();
		myPrefs = prefsRoot.node("DownloaderTool.preferences");
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public void storeLocalIliasFolderPath(String path) {
		myPrefs.put(ILIAS_FOLDER, path);
	}

	public String loadLocalIliasFolderPath() {
		final String path = myPrefs.get(ILIAS_FOLDER, ".");
		if (!new File(path).exists()) {
			return ".";
		}
		return path;
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

	public void storeIgnoredFileSize(String key, int size) {
		myPrefs.putInt(key, size);
	}

	public int isIgnored(String key) {
		return myPrefs.getInt(key, -1);
	}

	public void removeIgnoredFileSize(String key) {
		myPrefs.remove(key);
	}

	public void togglePdfIgnored(IliasPdf pdf) {
		if (pdf.isIgnored()) {
			pdf.setIgnored(false);
		} else {
			pdf.setIgnored(true);
		}
	}

	public boolean localIliasPathIsAlreadySet() {
		return myPrefs.getBoolean(LOCAL_ILIAS_PATH_IS_SET, false);
	}

	public void setLocalIliasPathStored(boolean value) {
		myPrefs.putBoolean(LOCAL_ILIAS_PATH_IS_SET, value);
	}

	public void removeNode() {
		try {
			myPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void setLogIn(boolean b) {
		myPrefs.putBoolean(USER_IS_LOGGED_IN, b);
	}

	public boolean userIsLoggedIn() {
		return myPrefs.getBoolean(USER_IS_LOGGED_IN, false);
	}

	public boolean updateCanceled() {
		return myPrefs.getBoolean(UPDATE_CANCELED, false);
	}

	public void setUpdateCanceled(boolean b) {
		myPrefs.putBoolean(UPDATE_CANCELED, b);
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

	public void storeLocalNotThere(String key, boolean value) {
		myPrefs.putBoolean(key + LNT, value);
	}

	public boolean loadLocalNotThere(String key) {
		return myPrefs.getBoolean(key + LNT, true);
	}
}
