package view;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Directory;
import model.PDF;
import control.Downloader;
import control.Ignorer;

public class TreePopupShower implements EventHandler<MouseEvent> {
	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
	private final MenuItem ignoreItemCancel;
	private final TreeView<Directory> courses;

	public TreePopupShower(TreeView<Directory> courses) {
		this.courses = courses;
		menu = new ContextMenu();
		downloadItem = new MenuItem("Herunterladen");
		downloadItem.setOnAction(new Downloader(courses));
		ignoreItem = new MenuItem("Ignorieren");
		ignoreItemCancel = new MenuItem("Ignorieren aufheben");
		ignoreItem.setOnAction(new Ignorer(courses));
		ignoreItemCancel.setOnAction(new Ignorer(courses));
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			final TreeItem<Directory> selectedTreeItem = Dashboard.getSelectedTreeItem();
			if (!(selectedTreeItem.getValue() instanceof PDF)) {
				menu.hide();
				return;
			}
			final PDF pdf = (PDF) selectedTreeItem.getValue();
			menu.getItems().clear();
			menu.getItems().add(downloadItem);
			if (pdf.isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
			menu.show(courses, event.getScreenX(), event.getScreenY());
		} else {
			menu.hide();
		}
	}
}
