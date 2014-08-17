package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import model.IliasFile;
import control.LocalFileStorage;

public class DesktopHelper {

	public static void print(IliasFile iliasFile) {
		final File file = LocalFileStorage.getInstance().getFile(iliasFile);
		if (file != null && file.exists()) {
			try {
				Desktop.getDesktop().print(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// showErrorMessage();
		}
	}

	public static void openFile(IliasFile iliasFile) {
		final File file = LocalFileStorage.getInstance().getFile(iliasFile);
		if (file != null && file.exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

	public static void openLocalFolder(IliasFile iliasFile) {
		final File file = LocalFileStorage.getInstance().getFile(iliasFile);
		if (file != null && file.exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file.getParentFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

}
