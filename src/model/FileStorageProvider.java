package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class FileStorageProvider {
	private static final String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
	private static final String LAST_UPDATE_DAY = "LAST_UPDATE_DAY";
	private static final String URL_DIVIDER = "#URL_DIVIDER_#";
	private static final String SIZE_DIVIDER = "#SIZE_DIVIDER_#";
	private static Preferences prefsRoot;
	private static Preferences myPrefs;

	public FileStorageProvider() {
		prefsRoot = Preferences.userRoot();
		myPrefs = prefsRoot.node("DownloaderTool.preferences.files");
	}

	public static void main(String[] args) {
		new FileStorageProvider().removeNode();
		new StorageProvider().removeNode();
	}

	private void removeNode() {
		try {
			myPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public List<PDF> loadAllPdfFiles() {
		final List<Directory> allFiles = loadAllFiles();
		final ArrayList<PDF> allPdfFiles = new ArrayList<PDF>();

		filter(allFiles, allPdfFiles);

		return allPdfFiles;
	}

	private void filter(final List<Directory> allFiles, final ArrayList<PDF> allPdfFiles) {
		for (Directory directory : allFiles) {
			if (directory instanceof PDF) {
				allPdfFiles.add((PDF) directory);
			}
			filter(directory.getChildFolders(), allPdfFiles);
		}
	}

	public void storeAllFiles(List<Directory> allFiles) {
		store(allFiles, "");
	}

	public List<Directory> loadAllFiles() {
		List<Directory> values = new ArrayList<Directory>();
		load(values, "", null);
		return values;
	}

	private void store(List<Directory> allFiles, String prefix) {
		int counter = 0;
		String suffix = null;
		for (Directory dir : allFiles) {
			if (dir instanceof Folder) {
				suffix = "#fdr";
			} else if (dir instanceof PDF) {
				suffix = "#PDF";
			} else if (dir instanceof Forum) {
				suffix = "#frm";
			}

			String value = dir.getName() + suffix + URL_DIVIDER + dir.getUrl();
			if (dir instanceof PDF) {
				value = value + SIZE_DIVIDER + ((PDF) dir).getSize();
			}
			myPrefs.put(prefix + String.valueOf(counter), value);

			store(dir.getChildFolders(), prefix + String.valueOf(counter) + "+");
			counter++;
		}
	}

	private void load(List<Directory> values, String prefix, Directory parent) {
		int counter = 0;
		String name = null;
		while (true) {
			name = myPrefs.get(prefix + String.valueOf(counter), "");

			if (name.equals("")) {
				for (int i = 0; i < values.size(); i++) {
					if (values.get(i) instanceof Folder) {
						List<Directory> childFolders = values.get(i).getChildFolders();
						childFolders = new ArrayList<Directory>();
						load(childFolders, prefix + i + "+", values.get(i));
					}
				}
				break;
			}

			int size = 0;
			int startIndexOfSize = name.indexOf(SIZE_DIVIDER);
			if (startIndexOfSize == -1) {
				startIndexOfSize = name.length();
			} else {
				size = Integer.valueOf(name.substring(startIndexOfSize + SIZE_DIVIDER.length()));
			}
			final int startIndexOfUrl = name.indexOf(URL_DIVIDER);
			String url = name.substring(startIndexOfUrl + URL_DIVIDER.length(), startIndexOfSize);
			name = name.substring(0, startIndexOfUrl);

			if (name.endsWith("#fdr")) {
				values.add(new Folder(name.replace("#fdr", ""), url, parent));
			}
			if (name.endsWith("#PDF")) {
				values.add(new PDF(name.replace("#PDF", ""), url, parent, size));
			}
			if (name.endsWith("#frm")) {
				values.add(new Forum(name.replace("#frm", ""), url, parent));
			}
			counter++;
		}
	}

	public String getActualisationDate() {
		final String currentDay = new SimpleDateFormat("D'-'M'-'Y").format(Calendar.getInstance().getTime());

		String time = myPrefs.get(LAST_UPDATE_TIME, "-");
		String day = myPrefs.get(LAST_UPDATE_DAY, "-");

		if (time != "-") {
			if (currentDay.equals(day)) {
				final int index = time.indexOf(',');
				time = time.substring(index);
				time = "Heute" + time;
			} else {
				final String[] split = day.split("-");
				final String[] split2 = currentDay.split("-");
				if (Integer.valueOf(split[0]) < Integer.valueOf(split2[0]) && Integer.valueOf(split[1]) == Integer.valueOf(split2[1])) {
					final int index = time.indexOf(',');
					time = time.substring(index);
					time = "Gestern" + time;
				}
			}
		}

		return "Letzte Aktualisierung: " + time;
	}

	public void setActualisationDate() {
		final String time = new SimpleDateFormat("EEEE',' HH:mm").format(Calendar.getInstance().getTime());
		final String day = new SimpleDateFormat("D'-'M'-'Y").format(Calendar.getInstance().getTime());
		myPrefs.put(LAST_UPDATE_TIME, time);
		myPrefs.put(LAST_UPDATE_DAY, day);
	}
}
