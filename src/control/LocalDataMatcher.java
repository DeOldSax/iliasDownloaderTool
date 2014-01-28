package control;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.PDF;
import view.Dashboard;

public class LocalDataMatcher implements EventHandler<ActionEvent> {
	private final List<PDF> matchedPdfs = new ArrayList<PDF>();

	@Override
	public void handle(ActionEvent event) {
		matchedPdfs.clear();
		final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();

		final List<Integer> allLocalPdfSizes = new LocalDataReader().getAllLocalPDFSizes();
		for (PDF pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				continue;
			}
			if (!allLocalPdfSizes.contains(pdf.getSize())) {
				matchedPdfs.add(pdf);
			}
		}
		Dashboard.setStatusText("Gesamt: " + allPdfFiles.size() + ", davon sind " + matchedPdfs.size() + " noch nicht im Ilias Ordner.",
				false);
		showInResultList();
	}

	private void showInResultList() {
		Dashboard.clearResultList();
		for (PDF pdf : matchedPdfs) {
			Dashboard.addToResultList(pdf);
		}
	}
}
