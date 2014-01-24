package control;

import java.util.List;

import model.Directory;
import model.FileStorageProvider;
import model.PDF;

public class FileSystem {
	private static List<Directory> allFiles = null;
	private static List<PDF> allPdfFiles = null;

	public static List<PDF> getAllPdfFiles() {
		if (allPdfFiles == null) {
			allPdfFiles = new FileStorageProvider().loadAllPdfFiles();
		}
		return allPdfFiles;
	}

	public static void setAllPdfFiles(List<PDF> allPdfFiles) {
		FileSystem.allPdfFiles = allPdfFiles;
	}

	public static List<Directory> getAllFiles() {
		if (allFiles == null) {
			allFiles = new FileStorageProvider().loadAllFiles();
		}
		return allFiles;
	}

	public static void setAllFiles(List<Directory> allFiles) {
		new FileStorageProvider().storeAllFiles(allFiles);
		FileSystem.allFiles = allFiles;
	}
}
