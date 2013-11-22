package worker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import model.Adresse;

public class PdfDownloader implements ActionListener {

	private final Vector<String> adresseList;
	public final List<Adresse> allPdf;

	public PdfDownloader() {
		adresseList = new Vector<String>();
		allPdf = new ArrayList<Adresse>();
	}

	public void download(Adresse adresse) {
		new Thread(new FileDownloader(adresse, "pdf")).start();
	}

	public void download(List<Adresse> adresseList) {
		for (Adresse pdf : adresseList) {
			new Thread(new FileDownloader(pdf, "pdf")).start();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<Adresse> selectedAdresse = new ArrayList<Adresse>();
		for (String name : adresseList) {
			for (Adresse adresse : allPdf) {
				if (name.equals(adresse.getName())) {
					selectedAdresse.add(adresse);
				}
			}
		}
		download(selectedAdresse);
	}
}
