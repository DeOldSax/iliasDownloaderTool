package model.persistance;

import java.io.File;

public class IliasFolderSettings implements Storable {
	
	private static final long serialVersionUID = -3343051441330383224L;
	private String localIliasFolderPath;
	
	public void setLocalIliasFolderPath(String localIliasFolderPath) {
		this.localIliasFolderPath = localIliasFolderPath;
	}

	public String getLocalIliasFolderPath() {
		String path = localIliasFolderPath;
		if (localIliasFolderPath == null) {
			path = ".";
		}
		if (!new File(path).exists()) {
			return ".";
		}
		return path;
	}

	@Override
	public String getStorageFileName() {
		return "iliasFolderSettings.ser";
	}
}
