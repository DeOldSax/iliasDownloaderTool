package control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class Ignorer implements EventHandler<ActionEvent> {
	private Directory selectedDirectory;

	@Override
	public void handle(ActionEvent event) {
		act();
	}

	public void act() {
		selectedDirectory = Dashboard.getSelectedDirectory();

		if (!(selectedDirectory instanceof PDF)) {
			return;
		}
		PDF pdf = (PDF) selectedDirectory;
		if (pdf.isIgnored()) {
			pdf.setIgnored(false);
			Dashboard.setStatusText(pdf.getName() + " wird nicht mehr ignoriert.", false);
		} else {
			pdf.setIgnored(true);
			Dashboard.setStatusText(pdf.getName() + " wurde auf ignorieren gesetzt.", false);
		}
		changeGraphicInTreeView(pdf);
		new IgnoredPdfFilter().filter();
	}

	private void changeGraphicInTreeView(PDF pdf) {
		TreeItem<Directory> treeItem = Dashboard.getLinkedTreeItem(pdf);
		ImageView image;
		if (new LocalDataReader().getAllLocalPDFSizes().contains(pdf.getSize())) {
			image = new ImageView("img/pdf.png");
		} else {
			image = new ImageView("img/pdf_local_not_there.png");
		}
		if (pdf.isIgnored()) {
			image = new ImageView("img/pdf_ignored.png");
		}
		setGraphic(image, treeItem);
	}

	private void setGraphic(final ImageView image, final TreeItem<Directory> treeItem) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				treeItem.setExpanded(false);
				treeItem.setGraphic(image);
				treeItem.setExpanded(true);
			}
		});
	}
}
