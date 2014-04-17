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
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;
import control.DownloadMode;
import control.DownloaderTask;
import control.LocalPdfStorage;

public class FileContextMenu {

	private final ContextMenu menu;
	private final MenuItem downloadItem;
	private final MenuItem ignoreItem;
//	private final MenuItem ignoreAllItem;
	private MenuItem ignoreItemCancel;
//	private MenuItem ignoreAllItemCancel;
	private MenuItem autoDownloadItem;
	private MenuItem normalDownloadItem;
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
		downloadItem = new MenuItem("Herunterladen");
		downloadItem.setGraphic(new ImageView("img/downloadArrow.png"));
		downloadItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				download(selectedIliasTreeNode);
			}
		});
		autoDownloadItem = new MenuItem("Auswahl Herunterladen AUTO");
		normalDownloadItem = new MenuItem("Auswahl Herunterladen");
		normalDownloadItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				download(selectedIliasTreeNodes);
			}
		});
		ignoreItem = new MenuItem("Ignorieren");
//		ignoreItem = new MenuItem("Auswahl ignorieren"); 
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
			System.out.println("only one item selected");
			return createMenu(selectedNodes.get(0), event); 
		} 
		// filter selection for IliasPdfs.
		if (Settings.getInstance().userIsLoggedIn()) {
			for (IliasTreeNode iliasTreeNode : selectedNodes) {
				if (!(iliasTreeNode instanceof IliasPdf)) {
					selectedNodes.remove(iliasTreeNode); 
				}
			}
			selectedIliasTreeNodes = selectedNodes; 
			menu.getItems().addAll(autoDownloadItem, normalDownloadItem); 
		}
		return menu;
	}

	private ContextMenu createMenu(final IliasTreeNode node, final MouseEvent event) {
		this.selectedIliasTreeNode = node;
		menu.getItems().clear();

		if (node instanceof IliasForum) {
			menu.getItems().add(openForumItem);
			return menu;
		} else if (node instanceof IliasPdf) {
			IliasPdf pdf = (IliasPdf) node;
			if (pdf.isIgnored()) {
				menu.getItems().add(ignoreItem);
			} else {
				menu.getItems().add(ignoreItemCancel);
			}
			if (LocalPdfStorage.getInstance().contains(pdf)) {
				menu.getItems().add(printItem);
				menu.getItems().add(openParentFolderItem);
				menu.getItems().add(openFileItem);
			}
		}
		if (Settings.getInstance().userIsLoggedIn() && node instanceof IliasPdf) {
			menu.getItems().add(0, downloadItem);
		}
		return menu;
	}

	private void download(IliasTreeNode iliasTreeNode) {
		new Thread(new DownloaderTask(iliasTreeNode)).start(); 
	}
	
	private void download(List<IliasTreeNode> iliasTreeNodes) {
		for (IliasTreeNode iliasTreeNode : iliasTreeNodes) {
			new Thread(new DownloaderTask(iliasTreeNode, DownloadMode.AUTO)).start(); 
		}
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