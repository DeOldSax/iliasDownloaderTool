package control;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.PDF;
import view.Dashboard;

public class IgnoredPdfFilter implements EventHandler<ActionEvent> {
	private ArrayList<PDF> ignoredPdf;

	@Override
	public void handle(ActionEvent event) {
		filter();
		Dashboard.setStatusText(ignoredPdf.size() + " ignorierte Dateien in Ignorieren-Liste.", false);
	}

	public void filter() {
		final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();
		ignoredPdf = new ArrayList<PDF>();
		for (PDF pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				ignoredPdf.add(pdf);
			}
		}
		Dashboard.clearResultList();
		Dashboard.setListHeader(" Ignorierte Dateien " + "(" + String.valueOf(ignoredPdf.size()) + ")", "red");
		for (PDF pdf : ignoredPdf) {
			Dashboard.addToResultList(pdf);
		}
	}
}
