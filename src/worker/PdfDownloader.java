package worker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import model.PDF;

public class PdfDownloader implements ActionListener {

	private final Vector<String> adresseList;
	public final List<PDF> allPdf;

	public PdfDownloader() {
		adresseList = new Vector<String>();
		allPdf = new ArrayList<PDF>();
	}

	public void download(PDF pdf) {
		new Thread(new FileDownloader(pdf, "pdf")).start();
	}

	public void download(List<PDF> adresseList) {
		for (PDF pdf : adresseList) {
			new Thread(new FileDownloader(pdf, "pdf")).start();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<PDF> selectedPDF = new ArrayList<PDF>();
		for (String name : adresseList) {
			for (PDF adresse : allPdf) {
				if (name.equals(adresse.getName())) {
					selectedPDF.add(adresse);
				}
			}
		}
		download(selectedPDF);
	}
}
