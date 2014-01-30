package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Directory;
import model.Forum;
import model.PDF;
import model.Settings;
import control.Downloader;
import control.Ignorer;
import control.LocalDataReader;

public class FileContextMenu {

	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
	private final MenuItem ignoreItemCancel;
	private final MenuItem printItem;
	private final MenuItem openParentFolderItem;
	private final MenuItem openFileItem;
	private final MenuItem openForumItem;
	private Directory directory;

	public FileContextMenu() {
		menu = new ContextMenu();
		downloadItem = new MenuItem("Herunterladen");
		downloadItem.setGraphic(new ImageView("img/downloadArrow.png"));
		downloadItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				download();
			}
		});
		ignoreItem = new MenuItem("Ignorieren");
		ignoreItem.setGraphic(new ImageView("img/ignore.png"));
		ignoreItemCancel = new MenuItem("Ignorieren aufheben");
		ignoreItemCancel.setGraphic(new ImageView("img/check.png"));
		ignoreItem.setOnAction(new Ignorer());
		ignoreItemCancel.setOnAction(new Ignorer());
		printItem = new MenuItem("Drucken");
		printItem.setGraphic(new ImageView("img/printer.png"));
		printItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				print();
			}
		});
		openParentFolderItem = new MenuItem("In Ordner öffnen");
		openParentFolderItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openLocalFolder();
			}
		});
		openFileItem = new MenuItem("Datei öffnen");
		openFileItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openFile();
			}
		});
		openForumItem = new MenuItem("Im Browser öffnen");
		openForumItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openForum();
			}
		});
	}

	private void download() {
		new Downloader().download(directory);
	}

	public ContextMenu createMenu(final Directory directory, final MouseEvent event) {
		this.directory = directory;
		menu.getItems().clear();

		if (directory instanceof Forum) {
			menu.getItems().add(openForumItem);
			return menu;
		} else if (directory instanceof PDF) {
			if (((PDF) directory).isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
			if (!((PDF) directory).isLocalNotThere()) {
				menu.getItems().add(printItem);
				menu.getItems().add(openParentFolderItem);
				menu.getItems().add(openFileItem);
			}
		}
		if (Settings.getInstance().userIsLoggedIn()) {
			menu.getItems().add(0, downloadItem);
		}
		return menu;
	}

	private void openFile() {
		final PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		new LocalDataReader().findFileOnLocalDisk(pdf);
		if (pdf.getFileOnLocalDisk() != null && pdf.getFileOnLocalDisk().exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(pdf.getFileOnLocalDisk());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

	private void openForum() {
		final Forum forum = (Forum) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(forum.getUrl()));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			Dashboard.browse(forum.getUrl());
		}
	}

	private void openLocalFolder() {
		PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		new LocalDataReader().findFileOnLocalDisk(pdf);
		if (pdf.getFileOnLocalDisk() != null && pdf.getFileOnLocalDisk().exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(pdf.getParentFolderOnLocalDisk());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

	private void print() {
		PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		new LocalDataReader().findFileOnLocalDisk(pdf);
		if (pdf.getFileOnLocalDisk() != null && pdf.getFileOnLocalDisk().exists()) {
			try {
				Desktop.getDesktop().print(pdf.getFileOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// showErrorMessage();
		}
	}
}