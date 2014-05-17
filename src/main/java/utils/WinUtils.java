package utils;

import model.Settings;

public class WinUtils {
	
	public static String makeDirectoryNameValid(String path) {
		String loadLocalIliasFolderPath = Settings.getInstance().loadLocalIliasFolderPath();
		String validString = path.replace(loadLocalIliasFolderPath, ""); 

		validString = validString.replace("*", "+")
		.replace("?", "_")
		.replace("<","[")
		.replace(">", "]")
		.replace("|", "--")
		.replace(":", " - "); 
		
		return loadLocalIliasFolderPath + validString; 
							
	}
	
	public static String makeFileNameValid(String path) {
		return path.replace(":", " - ")
				.replace("/", "+")
				.replace("*", "+")
				.replace("?", "_")
				.replace("<","[")
				.replace(">", "]")
				.replace("|", "--"); 
	}
}
