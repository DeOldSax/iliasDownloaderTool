package control;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class Downloader implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();
		final Directory selectedDirectory = Dashboard.getSelectedDirectory();

		for (PDF pdf : allPdfFiles) {
			if (selectedDirectory instanceof PDF) {
				if (pdf.getUrl().equals(selectedDirectory.getUrl())) {
					new Thread(new FileDownloader(pdf, ".pdf")).start();
				}
			}
		}
	}
}
