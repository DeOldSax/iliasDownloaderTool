package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import model.IliasPdf;
import control.LocalPdfStorage;

public class DesktopHelper {
	
	public static void print(IliasPdf pdf) {
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
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
	
	public static void openFile(IliasPdf pdf) {
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
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

	public static void openLocalFolder(IliasPdf pdf) {
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
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
