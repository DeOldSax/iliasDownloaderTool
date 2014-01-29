package control;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import model.PDF;
import view.Dashboard;

public class SearchTextField extends TextField {

	public SearchTextField() {
		super();
		setId("searchTextField");
		setPromptText("Datei suchen");
		setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				searchPdf(getText());
			};
		});
	}

	private void searchPdf(String inputString) {
		Dashboard.clearResultList();

		if (inputString.length() == 0 || inputString.equals(" ")) {
			Dashboard.clearResultList();
			Dashboard.setListHeader(" Gefundene Dateien " + "(" + String.valueOf(0) + ")", "green");
			return;
		}

		final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();

		if (allPdfFiles.isEmpty()) {
			Dashboard.clearResultList();
			Dashboard.setStatusText("Keine passende Datei gefunden.", false);
			return;
		}

		List<PDF> alreadyAddedPDF = new ArrayList<PDF>();

		for (PDF pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				continue;
			}
			final String[] splitedStrings = pdf.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = (splitedStrings[i] + " ").toLowerCase();
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].startsWith(inputString.toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (inputString.contains(" ") && pdf.getName().toLowerCase().contains(inputString.toLowerCase())
						&& !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(inputString.toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (pdf.getParentDirectory() != null) {
					if (inputString.length() > 3 && pdf.getParentDirectory().getName().toLowerCase().contains(inputString.toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
				if (pdf.getParentDirectory().getParentDirectory() != null) {
					if (inputString.length() > 3
							&& pdf.getParentDirectory().getParentDirectory().getName().toLowerCase().contains(inputString.toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
			}
		}
		Dashboard.setListHeader(" Gefundene Dateien " + "(" + String.valueOf(alreadyAddedPDF.size()) + ")", "green");
		if (alreadyAddedPDF.isEmpty()) {
			Dashboard.setStatusText("Keine Datei gefunden.");
		} else {
			for (PDF pdf : alreadyAddedPDF) {
				Dashboard.addToResultList(pdf);
			}
		}
	}
}
