package control;

import java.util.List;

import javafx.application.Platform;
import model.Directory;
import model.PDF;

public class Downloader {
	public void download(final Directory directory) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();

				for (PDF pdf : allPdfFiles) {
					if (directory instanceof PDF) {
						if (pdf.getUrl().equals(directory.getUrl())) {
							Platform.runLater(new FileDownloader(pdf, ".pdf"));
						}
					}
				}
			}
		});
	}
}
