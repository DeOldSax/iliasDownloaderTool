package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

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

	public FileContextMenu() {
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

	public ContextMenu createMenu(final Directory directory, final MouseEvent event) {
		menu.getItems().clear();
		if (Settings.getInstance().userIsLoggedIn()) {
			menu.getItems().add(downloadItem);
		}

		if (directory instanceof Forum) {
			menu.getItems().add(openForumItem);
			return menu;
		}
		if (directory instanceof PDF) {
			if (((PDF) directory).isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
		}
		final File file = new LocalDataReader().findFileOnLocalDisk((PDF) directory);
		if (file != null) {
			((PDF) directory).setFileOnLocalDisk(file);
			menu.getItems().add(printItem);
			menu.getItems().add(openParentFolderItem);
			menu.getItems().add(openFileItem);
		}
		return menu;
	}

	private void openFile() {
		final PDF selectedDirectory = (PDF) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(selectedDirectory.getFileOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void openForum() {
		final Forum forum = (Forum) Dashboard.getSelectedDirectory();
		Dashboard.browse(forum.getUrl());
	}

	private void openLocalFolder() {
		PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(pdf.getParentFolderOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void print() {
		PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().print(pdf.getFileOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}