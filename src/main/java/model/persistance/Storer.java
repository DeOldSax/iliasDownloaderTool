package model.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class Storer {
	private static final String ILIAS_STORE_FOLDER = System.getProperty("user.home") + "/" + ".ilias";
	
	protected void store(Storable storableObject) {
		createStorageFolder();
		String thisStorePath = createStorePath(storableObject);
		// TODO check if file already exists and throw exception!
		serialize(storableObject, thisStorePath); 
	}
	
	protected Storable load(Storable storableObject) {
		return (Storable) deserialize(createStorePath(storableObject));
	}

	private String createStorePath(Storable storableObject) {
		String thisStorePath = ILIAS_STORE_FOLDER + "/" + storableObject.getStorageFileName();
		return thisStorePath;
	}
	
	private static void serialize(Object object, String path) {
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
