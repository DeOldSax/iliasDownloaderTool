package control;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class Downloader implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final List<PDF> allPdfFiles = FileSystem.getAllPdfFiles();
		final ObservableList<TreeItem<Directory>> selectedItems = Dashboard.getSelectedItems();
		final List<PDF> selectedPDF = new ArrayList<PDF>();

		for (TreeItem<Directory> treeItem : selectedItems) {
			if (treeItem.getValue() instanceof PDF) {
				for (PDF pdf : allPdfFiles) {
					if (pdf.getUrl().equals(treeItem.getValue().getUrl())) {
						selectedPDF.add(pdf);
					}
				}
			}
		}

		for (PDF pdf : selectedPDF) {
			new Thread(new FileDownloader(pdf, ".pdf")).start();
		}
	}
}
