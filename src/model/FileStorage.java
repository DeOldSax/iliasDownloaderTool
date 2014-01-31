package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
	private static final String TIME_STORE_PATH = System.getProperty("user.home") + "/" + ".ilias" + "/" + "time.ser";
	private static final String ILIAS_STORE_FOLDER = System.getProperty("user.home") + "/" + ".ilias";
	private static final String COURSES_STORE_PATH = System.getProperty("user.home") + "/" + ".ilias" + "/" + "dirs.ser";

	public static List<PDF> loadAllPdfFiles() {
		final List<Directory> allFiles = loadAllFiles();
		final ArrayList<PDF> allPdfFiles = new ArrayList<PDF>();
		pdfFilter(allFiles, allPdfFiles);
		return allPdfFiles;
	}

	private static void pdfFilter(final List<Directory> allFiles, final ArrayList<PDF> allPdfFiles) {
		for (Directory directory : allFiles) {
			if (directory instanceof PDF) {
				allPdfFiles.add((PDF) directory);
			}
			pdfFilter(directory.getChildFolders(), allPdfFiles);
		}
	}

	public static void storeAllFiles(List<Directory> allFiles) {
		serialize(allFiles, COURSES_STORE_PATH);
	}

	@SuppressWarnings("unchecked")
	public static List<Directory> loadAllFiles() {
		final Object object = deserialize(COURSES_STORE_PATH);
		if (object == null) {
			return new ArrayList<Directory>();
		}
		return (List<Directory>) object;
	}

	public static String getActualisationDate() {
		final ActualisationDate actualisationDate = (ActualisationDate) deserialize(TIME_STORE_PATH);

		if (actualisationDate == null) {
			return "Letzte Aktualisierung: -";
		}
		return actualisationDate.toString();
	}

	public static void setActualisationDate() {
		serialize(new ActualisationDate(), TIME_STORE_PATH);
	}

	private static Object deserialize(String path) {
		if (!fileExists(path)) {
			return null;
		}
		Object object = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream(new File(path));
			ObjectInputStream o = new ObjectInputStream(fileInputStream);
			object = o.readObject();
			o.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	private static void serialize(Object object, String path) {
		createStorageFolder();
		ObjectOutputStream out = null;
		try {
			FileOutputStream output = new FileOutputStream(path);
			out = new ObjectOutputStream(output);
			out.writeObject(object);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createStorageFolder() {
		File file = new File(ILIAS_STORE_FOLDER);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private static boolean fileExists(String path) {
		return new File(path).exists();
	}
}
