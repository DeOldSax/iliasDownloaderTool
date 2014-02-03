package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.IliasPdf;
import view.Dashboard;
import view.ResultList;

public class LocalDataMatcher implements EventHandler<ActionEvent> {
	private final List<IliasPdf> matchedPdfs = new ArrayList<IliasPdf>();

	@Override
	public void handle(ActionEvent event) {
		matchedPdfs.clear();
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();

		final Set<Integer> allLocalPdfSizes = LocalPdfStorage.getInstance().getAllLocalPDFSizes();
		for (IliasPdf pdf : allPdfFiles) {
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
		ResultList.listMode = ResultList.PDF_NOT_SYNCHRONIZED;
		Dashboard.setListHeader(" Lokal nicht vorhandene Dateien " + "(" + String.valueOf(matchedPdfs.size()) + ")", "");
		Dashboard.clearResultList();
		for (IliasPdf pdf : matchedPdfs) {
			Dashboard.addToResultList(pdf);
		}
	}
}
