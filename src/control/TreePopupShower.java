package control;

import java.io.File;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Directory;
import model.PDF;
import model.StorageProvider;
import view.Dashboard;

public class TreePopupShower implements EventHandler<MouseEvent> {
	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
	private final MenuItem ignoreItemCancel;
	private final TreeView<Directory> courses;
	private final MenuItem printItem;
	private final MenuItem openParentFolderItem;
	private final MenuItem openFileItem;

	public TreePopupShower(TreeView<Directory> courses) {
		this.courses = courses;
		menu = new ContextMenu();
		downloadItem = new MenuItem("Herunterladen");
		downloadItem.setOnAction(new Downloader());
		ignoreItem = new MenuItem("Ignorieren");
		ignoreItemCancel = new MenuItem("Ignorieren aufheben");
		ignoreItem.setOnAction(new Ignorer(courses));
		ignoreItemCancel.setOnAction(new Ignorer(courses));
		printItem = new MenuItem("Drucken");
		printItem.setOnAction(new Printer());
		openParentFolderItem = new MenuItem("In Ordner öffnen");
		openParentFolderItem.setOnAction(new LocalFolderOpener(courses));
		openFileItem = new MenuItem("Datei öffnen");
		openFileItem.setOnAction(new FileOpener());
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			final Directory selectedTreeItem = Dashboard.getSelectedItem();
			if (!(selectedTreeItem instanceof PDF)) {
				menu.hide();
				return;
			}
			final PDF pdf = (PDF) selectedTreeItem;
			menu.getItems().clear();
			if (new StorageProvider().userIsLoggedIn()) {
				menu.getItems().add(downloadItem);
			}
			if (pdf.isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
			final File findFile = new LocalDataReader().findFileOnLocalDisk(pdf);
			if (findFile != null) {
				pdf.setFileOnLocalDisk(findFile);
				menu.getItems().add(printItem);
				menu.getItems().add(openParentFolderItem);
				menu.getItems().add(openFileItem);
			}
			menu.show(courses, event.getScreenX(), event.getScreenY());
		} else {
			menu.hide();
		}
	}
}
