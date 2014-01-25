package control;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class Ignorer implements EventHandler<ActionEvent> {
	private final TreeView<Directory> courses;
	private TreeItem<Directory> treeItem;

	public Ignorer(TreeView<Directory> courses) {
		this.courses = courses;
	}

	@Override
	public void handle(ActionEvent event) {
		treeItem = courses.getSelectionModel().getSelectedItem();
		if (!(treeItem.getValue() instanceof PDF)) {
			return;
		}
		PDF pdf = (PDF) treeItem.getValue();
		if (pdf.isIgnored()) {
			pdf.setIgnored(false);
			Dashboard.setStatusText(pdf.getName() + " wurde ignorieren aufgehoben.", false);
		} else {
			pdf.setIgnored(true);
			Dashboard.setStatusText(pdf.getName() + " wurde auf ignorieren gesetzt.", false);
			// findNodeAndDelete(courses.getRoot().getChildren());
		}
		new IgnoredPdfFilter().filter();
	}

	private void findNodeAndDelete(final ObservableList<TreeItem<Directory>> list) {
		for (final TreeItem<Directory> treeItem : list) {
			if (treeItem.equals(this.treeItem)) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						list.remove(treeItem);
					}
				});
				return;
			} else {
				findNodeAndDelete(treeItem.getChildren());
			}
		}
	}
}
