package control;

import java.awt.Desktop;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class FileOpener implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final ObservableList<TreeItem<Directory>> selectedItems = Dashboard.getSelectedItems();
		for (TreeItem<Directory> treeItem : selectedItems) {
			PDF pdf = (PDF) treeItem.getValue();
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(pdf.getFileOnLocalDisk());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
