package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;
import control.Downloader;
import control.LocalPdfStorage;

public class FileContextMenu {

	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
	private final MenuItem ignoreItemCancel;
	private final MenuItem printItem;
	private final MenuItem openParentFolderItem;
	private final MenuItem openFileItem;
	private final MenuItem openForumItem;
	private IliasTreeNode directory;
	private final Dashboard dashboard;

	public FileContextMenu(final Dashboard dashboard) {
		this.dashboard = dashboard;
		// FIXME add param Dashboard
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

		final EventHandler<ActionEvent> pdfIgnorer = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final IliasPdf pdf = (IliasPdf) directory;
				Settings.getInstance().togglePdfIgnored(pdf);
				dashboard.pdfIgnoredStateChanged(pdf);
				dashboard.getResultList().pdfIgnoredStateChanged(pdf);
			}
		};

		ignoreItem.setOnAction(pdfIgnorer);
		ignoreItemCancel.setOnAction(pdfIgnorer);
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

	public ContextMenu createMenu(final IliasTreeNode node, final MouseEvent event) {
		this.directory = node;
		menu.getItems().clear();

		if (node instanceof IliasForum) {
			menu.getItems().add(openForumItem);
			return menu;
		} else if (node instanceof IliasPdf) {
			IliasPdf pdf = (IliasPdf) node;
			if (pdf.isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
			if (LocalPdfStorage.getInstance().contains(pdf)) {
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
		final IliasPdf pdf = (IliasPdf) this.directory;
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
		if (file != null && file.exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

	private void openForum() {
		final IliasForum forum = (IliasForum) this.directory;
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(forum.getUrl()));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			dashboard.browse(forum.getUrl());
		}
	}

	private void openLocalFolder() {
		IliasPdf pdf = (IliasPdf) this.directory;
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
		if (file != null && file.exists()) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file.getParentFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// showErrorMessage();
		}
	}

	private void print() {
		IliasPdf pdf = (IliasPdf) this.directory;
		final File file = LocalPdfStorage.getInstance().getFile(pdf);
		if (file != null && file.exists()) {
			try {
				Desktop.getDesktop().print(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// showErrorMessage();
		}
	}
}