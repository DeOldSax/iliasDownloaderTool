package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.IliasFile;
import model.IliasFolder;
import model.IliasForum;
import model.IliasTreeNode;
import model.persistance.Settings;
import utils.DesktopHelper;
import control.LocalFileStorage;
import download.DownloadMode;
import download.IliasFolderDownloaderTask;
import download.IliasPdfDownloadCaller;

public class FileContextMenu {

	private final ContextMenu menu;
	private final MenuItem downloadIliasFileItem;
	private final MenuItem downloadIliasFolderItem;
	private final MenuItem downloadIliasFoldersItem;
	private final MenuItem ignoreItem;
	private MenuItem ignoreItemCancel;
	private MenuItem autoDownloadIliasPdfItem;
	private MenuItem normalDownloadIliasPdfsItem;
	private final MenuItem printItem;
	private final MenuItem openParentFolderItem;
	private final MenuItem openFileItem;
	private final MenuItem openForumItem;
	private IliasTreeNode selectedIliasTreeNode;
	private List<IliasTreeNode> selectedIliasTreeNodes;
	private final Dashboard dashboard;

	public FileContextMenu(final Dashboard dashboard) {
		this.dashboard = dashboard;
		// FIXME add param Dashboard
		menu = new ContextMenu();
		menu.getScene().getRoot().getStyleClass().add("main-root");
		downloadIliasFileItem = new MenuItem("Herunterladen");
		downloadIliasFileItem.setGraphic(new ImageView("img/downloadArrow.png"));
		downloadIliasFileItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasPdf(selectedIliasTreeNode);
			}
		});
		downloadIliasFolderItem = new MenuItem("Ordner Herunterladen (AUTO)");
		downloadIliasFolderItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasFolder((IliasFolder) selectedIliasTreeNode);
			}
		});
		downloadIliasFoldersItem = new MenuItem("Ordner Herunterladen (AUTO)");
		downloadIliasFoldersItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasFolders(selectedIliasTreeNodes);
			}
		});

		autoDownloadIliasPdfItem = new MenuItem("PDF-Dateien Herunterladen (AUTO)");
		autoDownloadIliasPdfItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasPdfs(selectedIliasTreeNodes, DownloadMode.AUTO);
			}
		});
		normalDownloadIliasPdfsItem = new MenuItem("PDF-Dateien Herunterladen");
		normalDownloadIliasPdfsItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasPdfs(selectedIliasTreeNodes, DownloadMode.NORMAL);
			}
		});
		ignoreItem = new MenuItem("Ignorieren");
		ignoreItem.setGraphic(new ImageView("img/ignore.png"));
		ignoreItemCancel = new MenuItem("Ignorieren aufheben");
		ignoreItemCancel = new MenuItem("Ignorieren für Auswahl aufheben");
		ignoreItemCancel.setGraphic(new ImageView("img/check.png"));

		final EventHandler<ActionEvent> pdfIgnorer = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final IliasFile file = (IliasFile) selectedIliasTreeNode;
				Settings.getInstance().toggleFileIgnored(file);
				dashboard.pdfIgnoredStateChanged(file);
				dashboard.getResultList().pdfIgnoredStateChanged(file);
			}
		};

		ignoreItem.setOnAction(pdfIgnorer);
		ignoreItemCancel.setOnAction(pdfIgnorer);
		printItem = new MenuItem("Drucken");
		printItem.setGraphic(new ImageView("img/printer.png"));
		printItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DesktopHelper.print((IliasFile) selectedIliasTreeNode);
			}
		});
		openParentFolderItem = new MenuItem("In Ordner öffnen");
		openParentFolderItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DesktopHelper.openLocalFolder((IliasFile) selectedIliasTreeNode);
			}
		});
		openFileItem = new MenuItem("Datei öffnen");
		openFileItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DesktopHelper.openFile((IliasFile) selectedIliasTreeNode);
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

	public ContextMenu createMenu(List<IliasTreeNode> selectedNodes, MouseEvent event) {
		menu.getItems().clear();
		if (selectedNodes.size() == 1) {
			return createMenu(selectedNodes.get(0), event);
		}

		if (Settings.getInstance().getFlags().isUserLoggedIn()) {
			boolean selectedNodesContainsFiles = false;
			boolean selectedNodesContainsFolder = false;
			for (IliasTreeNode iliasTreeNode : selectedNodes) {
				if (!selectedNodesContainsFiles && iliasTreeNode instanceof IliasFile) {
					menu.getItems().addAll(autoDownloadIliasPdfItem, normalDownloadIliasPdfsItem);
					selectedNodesContainsFiles = true;
				}
				if (!selectedNodesContainsFolder && iliasTreeNode instanceof IliasFolder) {
					menu.getItems().add(downloadIliasFoldersItem);
					selectedNodesContainsFolder = true;
				}
				if (iliasTreeNode instanceof IliasForum) {
					selectedNodes.remove(iliasTreeNode);
				}
			}
			selectedIliasTreeNodes = selectedNodes;
		}
		return menu;
	}

	private ContextMenu createMenu(final IliasTreeNode node, final MouseEvent event) {
		menu.getItems().clear();
		this.selectedIliasTreeNode = node;

		if (node instanceof IliasFolder && Settings.getInstance().getFlags().isUserLoggedIn()) {
			menu.getItems().add(0, downloadIliasFolderItem);
		} else if (node instanceof IliasForum) {
			menu.getItems().add(openForumItem);
			return menu;
		} else if (node instanceof IliasFile) {
			IliasFile file = (IliasFile) node;
			if (file.isIgnored()) {
				menu.getItems().add(ignoreItemCancel);
			} else {
				menu.getItems().add(ignoreItem);
			}
			if (LocalFileStorage.getInstance().contains(file)) {
				menu.getItems().add(printItem);
				menu.getItems().add(openParentFolderItem);
				menu.getItems().add(openFileItem);
			}
		}
		if (Settings.getInstance().getFlags().isUserLoggedIn() && node instanceof IliasFile) {
			menu.getItems().add(0, downloadIliasFileItem);
		}
		return menu;
	}

	private void downloadIliasPdf(IliasTreeNode selectediliasPdf) {
		new Thread(new IliasPdfDownloadCaller(selectediliasPdf)).start();
	}

	private void downloadIliasFolder(IliasFolder selectedIliasFolder) {
		new Thread(new IliasFolderDownloaderTask(selectedIliasFolder)).start();
	}

	private void downloadIliasPdfs(List<IliasTreeNode> selectedIliasTreeNodes, DownloadMode mode) {
		for (IliasTreeNode iliasTreeNode : selectedIliasTreeNodes) {
			new Thread(new IliasPdfDownloadCaller(iliasTreeNode, mode)).start();
		}
	}

	private void downloadIliasFolders(List<IliasTreeNode> selectedIliasFolders) {
		new Thread(new IliasFolderDownloaderTask(selectedIliasFolders)).start();
	}

	private void openForum() {
		final IliasForum forum = (IliasForum) this.selectedIliasTreeNode;
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
}