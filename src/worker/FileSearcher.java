package worker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import model.PDF;
import view.DownloaderToolWindow;

public class FileSearcher extends KeyAdapter {

	private final List<PDF> allPdfs;
	private final JTextField word;
	private final DownloaderToolWindow window;

	public FileSearcher(List<PDF> allPdfs, JTextField word, DownloaderToolWindow window) {
		this.allPdfs = allPdfs;
		this.word = word;
		this.window = window;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		act();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		act();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		act();
	}

	private void act() {
		List<PDF> alreadyAddedPDF = new ArrayList<PDF>();
		window.clearResultList();
		if (word.getText().isEmpty() || word.getText().equals(" ")) {
			return;
		}
		for (PDF pdf : allPdfs) {
			final String[] splitedStrings = pdf.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = (splitedStrings[i] + " ").toLowerCase();
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].startsWith(word.getText().toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					window.addToResultList(pdf);
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (word.getText().contains(" ") && pdf.getName().toLowerCase().contains(word.getText().toLowerCase())
						&& !alreadyAddedPDF.contains(pdf)) {
					window.addToResultList(pdf);
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(word.getText().toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					window.addToResultList(pdf);
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (pdf.getParentDirectory() != null) {
					if (word.getText().length() > 3
							&& pdf.getParentDirectory().getName().toLowerCase().contains(word.getText().toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						window.addToResultList(pdf);
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
				if (pdf.getParentDirectory().getParentDirectory() != null) {
					if (word.getText().length() > 3
							&& pdf.getParentDirectory().getParentDirectory().getName().toLowerCase().contains(word.getText().toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						window.addToResultList(pdf);
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
			}
		}
	}
}
