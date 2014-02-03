package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;

public class IliasTreeStorage {
	private static final String ILIAS_STORE_FOLDER = System.getProperty("user.home") + "/" + ".ilias";
	private static final String TIME_STORE_PATH = ILIAS_STORE_FOLDER + "/" + "time.ser";
	private static final String COURSES_STORE_PATH = ILIAS_STORE_FOLDER + "/" + "dirs.ser";
	private static List<IliasFolder> allFiles;

	public static void storeAllFiles(List<IliasFolder> allFiles) {
		serialize(allFiles, COURSES_STORE_PATH);
	}

	@SuppressWarnings("unchecked")
	public static List<IliasFolder> getTree() {
		if (allFiles == null) {
			allFiles = (List<IliasFolder>) deserialize(COURSES_STORE_PATH);
		}
		return allFiles;
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
		Object object = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream(new File(path));
			ObjectInputStream o = new ObjectInputStream(fileInputStream);
			object = o.readObject();
			o.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			return null;
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
