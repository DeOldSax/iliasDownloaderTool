package control;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import model.PDF;
import view.Dashboard;

public class FileSearcher implements EventHandler<KeyEvent> {

	private List<PDF> allPdfs = null;
	private final TextField word;

	public FileSearcher(TextField searchField) {
		this.word = searchField;
	}

	@Override
	public void handle(KeyEvent event) {
		if (word.getText().length() == 0) {
			Dashboard.clearResultList();
			Dashboard.setListHeader(" Gefundene Dateien " + "(" + String.valueOf(0) + ")", "green");
			return;
		}
		this.allPdfs = FileSystem.getAllPdfFiles();
		if (allPdfs.isEmpty()) {
			Dashboard.setStatusText("Keine passende Datei gefunden.", false);
			return;
		}
		act();
	}

	private void act() {
		Dashboard.clearResultList();
		List<PDF> alreadyAddedPDF = new ArrayList<PDF>();
		if (word.getText().isEmpty() || word.getText().equals(" ")) {
			return;
		}
		for (PDF pdf : allPdfs) {
			if (pdf.isIgnored()) {
				continue;
			}
			final String[] splitedStrings = pdf.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = (splitedStrings[i] + " ").toLowerCase();
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].startsWith(word.getText().toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (word.getText().contains(" ") && pdf.getName().toLowerCase().contains(word.getText().toLowerCase())
						&& !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(word.getText().toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (pdf.getParentDirectory() != null) {
					if (word.getText().length() > 3
							&& pdf.getParentDirectory().getName().toLowerCase().contains(word.getText().toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
				if (pdf.getParentDirectory().getParentDirectory() != null) {
					if (word.getText().length() > 3
							&& pdf.getParentDirectory().getParentDirectory().getName().toLowerCase().contains(word.getText().toLowerCase())
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
