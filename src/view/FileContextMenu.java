package view;

import java.awt.Desktop;
import java.io.File;
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
import model.IliasFolder;
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;
import control.LocalPdfStorage;
import download.DownloadMode;
import download.DownloaderTask;
import download.IliasFolderDownloaderTask;

public class FileContextMenu {

	private final ContextMenu menu;
	private final MenuItem downloadIliasPdfItem;
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
		downloadIliasPdfItem = new MenuItem("Herunterladen");
		downloadIliasPdfItem.setGraphic(new ImageView("img/downloadArrow.png"));
		downloadIliasPdfItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasPdf(selectedIliasTreeNode);
			}
		});
		downloadIliasFolderItem = new MenuItem("Ordner Herunterladen (AUTO)"); 
		downloadIliasFolderItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				downloadIliasFolder((IliasFolder)selectedIliasTreeNode); 
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
				final IliasPdf pdf = (IliasPdf) selectedIliasTreeNode;
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
	
	public ContextMenu createMenu(List<IliasTreeNode> selectedNodes, MouseEvent event) {
		menu.getItems().clear();
		if (selectedNodes.size() == 1) {
			return createMenu(selectedNodes.get(0), event); 
		} 

		if (Settings.getInstance().userIsLoggedIn()) {
			boolean selectedNodesContainsPdf = false; 
			boolean selectedNodesContainsFolder = false; 
			for (IliasTreeNode iliasTreeNode : selectedNodes) {
				if (!selectedNodesContainsPdf && iliasTreeNode instanceof IliasPdf) {
					menu.getItems().addAll(autoDownloadIliasPdfItem, normalDownloadIliasPdfsItem); 
					selectedNodesContainsPdf = true; 
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
		
		if (node instanceof IliasFolder && Settings.getInstance().userIsLoggedIn()) {
			menu.getItems().add(0, downloadIliasFolderItem); 
		} else if (node instanceof IliasForum) {
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
		if (Settings.getInstance().userIsLoggedIn() && node instanceof IliasPdf) {
			menu.getItems().add(0, downloadIliasPdfItem);
		}
		return menu;
	}

	private void downloadIliasPdf(IliasTreeNode selectediliasPdf) {
		new Thread(new DownloaderTask(selectediliasPdf)).start(); 
	}

	private void downloadIliasFolder(IliasFolder selectedIliasFolder) {
		new Thread(new IliasFolderDownloaderTask(selectedIliasFolder)).start();
	}
	
	private void downloadIliasPdfs(List<IliasTreeNode> selectedIliasTreeNodes, DownloadMode mode) {
		for (IliasTreeNode iliasTreeNode : selectedIliasTreeNodes) {
			new Thread(new DownloaderTask(iliasTreeNode, mode)).start(); 
		}
	}

	private void downloadIliasFolders(List<IliasTreeNode> selectedIliasFolders) {
		new Thread(new IliasFolderDownloaderTask(selectedIliasFolders)).start();
	}

	private void openFile() {
		final IliasPdf pdf = (IliasPdf) this.selectedIliasTreeNode;
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

	private void openLocalFolder() {
		IliasPdf pdf = (IliasPdf) this.selectedIliasTreeNode;
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
		IliasPdf pdf = (IliasPdf) this.selectedIliasTreeNode;
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