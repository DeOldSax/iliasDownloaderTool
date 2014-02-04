package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.IliasFolder;
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.IliasTreeProvider;
import model.Settings;
import control.Downloader;
import control.LocalPdfStorage;

public class CoursesTreeView extends TreeView<IliasTreeNode> {
	private final TreeItem<IliasTreeNode> rootItem;
	private ContextMenu menu;
	private final Dashboard dashboard;

	public CoursesTreeView(Dashboard dashboard) {
		super();
		this.dashboard = dashboard;
		rootItem = new TreeItem<IliasTreeNode>(new IliasFolder("Übersicht", null, null));
		rootItem.setExpanded(true);
		setRoot(rootItem);
		setShowRoot(false);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		menu = new ContextMenu();

		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				menu.hide();
				final TreeItem<IliasTreeNode> selectedItem = getSelectionModel().getSelectedItem();
				if (selectedItem == null) {
					return;
				}
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					if (selectedItem.getValue() instanceof IliasForum) {
						openForum();
						return;
					} else if (selectedItem.getValue() instanceof IliasPdf) {
						if (Settings.getInstance().userIsLoggedIn()) {
							new Downloader().download(((CoursesTreeView) event.getSource()).getSelectionModel().getSelectedItem()
									.getValue());
						}
					}
				}
				if (event.getButton() == MouseButton.SECONDARY) {
					if (!(selectedItem.getValue() instanceof IliasPdf || selectedItem.getValue() instanceof IliasForum)) {
						return;
					}
					showContextMenu(selectedItem, event);
				}
			};
		});
	}

	private void openForum() {
		final IliasForum forum = (IliasForum) this.getSelectionModel().getSelectedItem().getValue();
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

	private void showContextMenu(TreeItem<IliasTreeNode> item, MouseEvent event) {
		menu.getItems().clear();
		menu = new FileContextMenu(dashboard).createMenu(item.getValue(), event);
		menu.show(this, event.getScreenX(), event.getScreenY());
	}

	public void update() {
		rootItem.getChildren().clear();
		setCourses(rootItem, IliasTreeProvider.getTree());
	}

	private void setCourses(TreeItem<IliasTreeNode> rootItem, List<? extends IliasTreeNode> kurse) {
		for (IliasTreeNode node : kurse) {
			TreeItem<IliasTreeNode> item = new TreeItem<IliasTreeNode>(node);
			rootItem.getChildren().add(item);

			if (node instanceof IliasFolder) {
				IliasFolder folder = (IliasFolder) node;
				setCourses(item, folder.getChildNodes());
				if (LocalPdfStorage.getInstance().isFolderSynchronized(folder)) {
					item.setGraphic(new ImageView("img/folder.png"));
				} else {
					item.setGraphic(new ImageView("img/folder_pdf_not_there.png"));
				}
			} else if (node instanceof IliasPdf) {
				IliasPdf pdf = (IliasPdf) node;
				if (pdf.isIgnored()) {
					item.setGraphic(new ImageView("img/pdf_ignored.png"));
				} else if (!(LocalPdfStorage.getInstance().contains(pdf))) {
					item.setGraphic(new ImageView("img/pdf_local_not_there.png"));
				} else {
					item.setGraphic(new ImageView("img/pdf.png"));
				}
			} else if (node instanceof IliasForum) {
				item.setGraphic(new ImageView("img/forum.png"));
			}
		}
	}

	public void collapse() {
		collapse(rootItem.getChildren());
	}

	private void collapse(ObservableList<TreeItem<IliasTreeNode>> items) {
		for (TreeItem<IliasTreeNode> item : items) {
			item.setExpanded(false);
			collapse(item.getChildren());
		}
		dashboard.setStatusText("");
	}

	public void selectPdf(IliasPdf selectedDirectory) {
		collapse();
		final TreeItem<IliasTreeNode> linkedTreeItem = getItem(selectedDirectory);
		linkedTreeItem.setExpanded(true);
		getSelectionModel().clearSelection();
		getSelectionModel().select(linkedTreeItem);
		scrollTo(getSelectionModel().getSelectedIndex());
	}

	public TreeItem<IliasTreeNode> getItem(final IliasPdf pdf) {
		return search(rootItem, pdf);
	}

	private TreeItem<IliasTreeNode> search(TreeItem<IliasTreeNode> item, final IliasPdf pdf) {
		for (TreeItem<IliasTreeNode> treeItem : item.getChildren()) {
			if (treeItem.getValue().equals(pdf)) {
				return treeItem;
			}
			final TreeItem<IliasTreeNode> searchResult = search(treeItem, pdf);
			if (searchResult != null) {
				return searchResult;
			}
		}
		return null;
	}

	public void pdfStatusChanged(IliasPdf pdf) {
		final LocalPdfStorage localPdfStorage = LocalPdfStorage.getInstance();
		TreeItem<IliasTreeNode> treeItem = getItem(pdf);
		if (pdf.isIgnored()) {
			setGraphic(treeItem, new ImageView("img/pdf_ignored.png"));
		} else if (localPdfStorage.contains(pdf)) {
			setGraphic(treeItem, new ImageView("img/pdf.png"));
		} else {
			setGraphic(treeItem, new ImageView("img/pdf_local_not_there.png"));
		}
		while (treeItem.getParent() != null) {
			treeItem = treeItem.getParent();
			IliasFolder folder = (IliasFolder) treeItem.getValue();
			if (LocalPdfStorage.getInstance().isFolderSynchronized(folder)) {
				setGraphic(treeItem, new ImageView("img/folder.png"));
			} else {
				setGraphic(treeItem, new ImageView("img/folder_pdf_not_there.png"));
			}
		}
	}

	private void setGraphic(final TreeItem<IliasTreeNode> treeItem, final ImageView image) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				treeItem.setExpanded(!treeItem.isExpanded());
				treeItem.setGraphic(image);
				treeItem.setExpanded(!treeItem.isExpanded());
			}
		});
	}
}
