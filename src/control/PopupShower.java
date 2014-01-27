package control;

import java.io.File;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.PDF;
import model.StorageProvider;

public class PopupShower {

	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
	private final MenuItem ignoreItemCancel;
	private final MenuItem printItem;
	private final MenuItem openParentFolderItem;
	private final MenuItem openFileItem;
	private final MenuItem openForumItem;

	public PopupShower() {
		menu = new ContextMenu();
		downloadItem = new MenuItem("Herunterladen");
		downloadItem.setGraphic(new ImageView("img/downloadArrow.png"));
		downloadItem.setOnAction(new Downloader());
		ignoreItem = new MenuItem("Ignorieren");
		ignoreItem.setGraphic(new ImageView("img/ignore.png"));
		ignoreItemCancel = new MenuItem("Ignorieren aufheben");
		ignoreItemCancel.setGraphic(new ImageView("img/check.png"));
		ignoreItem.setOnAction(new Ignorer());
		ignoreItemCancel.setOnAction(new Ignorer());
		printItem = new MenuItem("Drucken");
		printItem.setGraphic(new ImageView("img/printer.png"));
		printItem.setOnAction(new Printer());
		openParentFolderItem = new MenuItem("In Ordner öffnen");
		openParentFolderItem.setOnAction(new LocalFolderOpener());
		openFileItem = new MenuItem("Datei öffnen");
		openFileItem.setOnAction(new FileOpener());
		openForumItem = new MenuItem("Im Browser öffnen");
		openForumItem.setOnAction(new ForumOpener());
	}

	public ContextMenu createMenu(final PDF pdf1, final MouseEvent event) {
		menu.getItems().clear();
		final PDF pdf = pdf1;
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
		return menu;
	}

}