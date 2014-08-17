package model.persistance;

import java.util.ArrayList;
import java.util.List;

import model.IliasFile;

public class Settings {
	// old Settings path in registry
	// myPrefs = prefsRoot.node("DownloaderTool.preferences");

	// methode to remove all entries:
	// public void removeNode() {
	// try {
	// myPrefs.removeNode();
	// } catch (BackingStoreException e) {
	// e.printStackTrace();
	// }
	// }
	private static final String ILIAS_STORE_FOLDER = System.getProperty("user.home") + "/"
			+ ".ilias";
	private static Settings instance;
	private Storer storer;
	private List<Storable> storableObjects;

	private User user;
	private Flags flags;
	private FileStates fileStates;
	private IliasFolderSettings iliasFolderSettings;

	private Settings() {
		storableObjects = new ArrayList<Storable>();
		storer = new Storer(ILIAS_STORE_FOLDER);
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public User getUser() {
		if (user == null) {
			user = (User) load(new User());
			if (user == null) {
				user = new User();
			}
			storableObjects.add(user);
		}
		return user;
	}

	public Flags getFlags() {
		if (flags == null) {
			flags = (Flags) load(new Flags());
			if (flags == null) {
				flags = new Flags();
			}
			storableObjects.add(flags);
		}
		return flags;

	}

	public FileStates getFileStates() {
		if (fileStates == null) {
			fileStates = (FileStates) load(new FileStates());
			if (fileStates == null) {
				fileStates = new FileStates();
			}
			storableObjects.add(fileStates);
		}
		return fileStates;
	}

	public IliasFolderSettings getIliasFolderSettings() {
		if (iliasFolderSettings == null) {
			iliasFolderSettings = (IliasFolderSettings) load(new IliasFolderSettings());
			if (iliasFolderSettings == null) {
				iliasFolderSettings = new IliasFolderSettings();
			}
			storableObjects.add(iliasFolderSettings);
		}
		return iliasFolderSettings;
	}

	public void toggleFileIgnored(IliasFile file) {
		if (file.isIgnored()) {
			file.setIgnored(false);
		} else {
			file.setIgnored(true);
		}
	}

	private Storable load(Storable storeable) {
		storeable = storer.load(storeable);
		return storeable;
	}

	public void store() {
		for (Storable storableObject : storableObjects) {
			storer.store(storableObject);
		}
	}

}
