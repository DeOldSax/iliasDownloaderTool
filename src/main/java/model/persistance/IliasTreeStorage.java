package model.persistance;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import model.*;

public class IliasTreeStorage {
	private static final String ILIAS_STORE_FOLDER = System.getProperty("user.home") + "/"
			+ ".ilias";
	private static final String TIME_STORE_PATH = ILIAS_STORE_FOLDER + "/" + "time.ser";
	private static final String COURSES_STORE_PATH = ILIAS_STORE_FOLDER + "/" + "dirs.ser";
	private static List<IliasFolder> allFiles;

	public static void storeAllFiles(List<IliasFolder> allFiles) {
		serialize(allFiles, COURSES_STORE_PATH);
	}

	@SuppressWarnings("unchecked")
	public static List<IliasFolder> getTree() {
		if (allFiles == null && new File(COURSES_STORE_PATH).exists()) {
			allFiles = (List<IliasFolder>) deserialize(COURSES_STORE_PATH);
		}
		return allFiles;
	}

	public static String getActualisationDate() {
		if (!new File(TIME_STORE_PATH).exists()) {
			return "Letzte Aktualisierung: -";
		}
		final ActualisationDate actualisationDate = (ActualisationDate) deserialize(TIME_STORE_PATH);
		return actualisationDate.toString();
	}

	public static void setActualisationDate() {
		serialize(new ActualisationDate(), TIME_STORE_PATH);
	}

	private static Object deserialize(String path) {
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
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			try {
				Files.setAttribute(file.toPath(), "dos:hidden", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
