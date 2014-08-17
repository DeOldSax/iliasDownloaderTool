package model.persistance;

import java.util.HashMap;

public class FileStates implements Storable {

	private static final long serialVersionUID = -4473735569329058652L;
	private HashMap<String, Integer> ignoredFiles = new HashMap<String, Integer>();

	public void storeIgnoredFileSize(String key, int size) {
		ignoredFiles.put(key, size);
	}

	public int isIgnored(String key) {
		return ignoredFiles.getOrDefault(key, -1);
	}

	public void removeIgnoredFileSize(String key) {
		ignoredFiles.remove(key);
	}

	@Override
	public String getStorageFileName() {
		return "fileStates.ser";
	}

}
