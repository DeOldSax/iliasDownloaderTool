package worker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import model.Adresse;
import view.DownloaderToolWindow;

public class FileSearcher extends KeyAdapter {

	private final List<Adresse> allPdfs;
	private final JTextField word;
	private final DownloaderToolWindow window;

	public FileSearcher(List<Adresse> allPdfs, JTextField word, DownloaderToolWindow window) {
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
		List<String> alreadyAddedStrings = new ArrayList<String>();
		window.clearResultList();
		if (word.getText().isEmpty() || word.getText().equals(" ")) {
			return;
		}
		for (Adresse pdf : allPdfs) {
			final String[] splitedStrings = pdf.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = splitedStrings[i] + " ";
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].toLowerCase().startsWith(word.getText().toLowerCase())
						&& !alreadyAddedStrings.contains(pdf.getName())) {
					window.addToResultList(pdf.getName(), pdf.getParentFolder());
					alreadyAddedStrings.add(pdf.getName());
					continue;
				}
				if (word.getText().contains(" ") && pdf.getName().toLowerCase().contains(word.getText().toLowerCase())
						&& !alreadyAddedStrings.contains(pdf.getName())) {
					window.addToResultList(pdf.getName(), pdf.getParentFolder());
					alreadyAddedStrings.add(pdf.getName());
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(word.getText().toLowerCase()) && !alreadyAddedStrings.contains(pdf.getName())) {
					window.addToResultList(pdf.getName(), pdf.getParentFolder());
					alreadyAddedStrings.add(pdf.getName());
					continue;
				}
				if (pdf.getParentFolder() != null) {
					if (word.getText().length() > 3 && pdf.getParentFolder().getName().toLowerCase().contains(word.getText().toLowerCase())
							&& !alreadyAddedStrings.contains(pdf.getName())) {
						window.addToResultList(pdf.getName(), pdf.getParentFolder());
						alreadyAddedStrings.add(pdf.getName());
					}
					continue;
				}
				if (pdf.getParentFolder().getParentFolder() != null) {
					if (word.getText().length() > 3
							&& pdf.getParentFolder().getParentFolder().getName().toLowerCase().contains(word.getText().toLowerCase())
							&& !alreadyAddedStrings.contains(pdf.getName())) {
						window.addToResultList(pdf.getName(), pdf.getParentFolder());
						alreadyAddedStrings.add(pdf.getName());
					}
					continue;
				}
			}
		}
	}
}
