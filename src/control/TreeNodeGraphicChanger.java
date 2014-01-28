package control;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class TreeNodeGraphicChanger {
	public void changeGraphicInTreeView(PDF pdf) {
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
