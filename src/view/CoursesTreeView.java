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
import model.Directory;
import model.Forum;
import model.PDF;
import model.Settings;
import control.Downloader;
import control.FileSystem;
import control.LocalDataReader;
import control.TreeViewContentFiller;

public class CoursesTreeView extends TreeView<Directory> {
	private final TreeItem<Directory> rootItem;
	private TreeItem<Directory> tempItemDummy;
	private ContextMenu menu;

	public CoursesTreeView() {
		super();
		rootItem = new TreeItem<Directory>(new Directory("Übersicht", null, null));
		rootItem.setExpanded(true);
		setRoot(rootItem);
		setShowRoot(false);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		menu = new ContextMenu();

		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				menu.hide();
				final TreeItem<Directory> selectedItem = getSelectionModel().getSelectedItem();
				if (selectedItem == null) {
					return;
				}
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					if (selectedItem.getValue() instanceof Forum) {
						openForum();
						return;
					} else if (selectedItem.getValue() instanceof PDF) {
						if (Settings.getInstance().userIsLoggedIn()) {
							new Downloader().download(((CoursesTreeView) event.getSource()).getSelectionModel().getSelectedItem()
									.getValue());
						}
					}
				}
				if (event.getButton() == MouseButton.SECONDARY) {
					if (!(selectedItem.getValue() instanceof PDF || selectedItem.getValue() instanceof Forum)) {
						return;
					}
					showContextMenu(selectedItem, event);
				}
			};
		});
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

	private void showContextMenu(TreeItem<Directory> item, MouseEvent event) {
		menu.getItems().clear();
		menu = new FileContextMenu().createMenu(item.getValue(), event);
		menu.show(this, event.getScreenX(), event.getScreenY());
	}

	public void update() {
		rootItem.getChildren().clear();
		final TreeViewContentFiller treeViewContentFiller = new TreeViewContentFiller();
		treeViewContentFiller.addKurseToTree(rootItem, FileSystem.getAllFiles());
		treeViewContentFiller.markCourses(rootItem);
	}

	public void collapse() {
		collapse(rootItem.getChildren());
	}

	private void collapse(ObservableList<TreeItem<Directory>> items) {
		for (TreeItem<Directory> item : items) {
			item.setExpanded(false);
			collapse(item.getChildren());
		}
		Dashboard.setStatusText("");
	}

	public void expandTreeItem(Directory selectedDirectory) {
		collapse();
		final TreeItem<Directory> linkedTreeItem = getLinkedTreeItem((PDF) selectedDirectory);
		linkedTreeItem.setExpanded(true);
		getSelectionModel().clearSelection();
		getSelectionModel().select(linkedTreeItem);
		final int selectedIndex = getSelectionModel().getSelectedIndex();
		scrollTo(selectedIndex);
	}

	public TreeItem<Directory> getLinkedTreeItem(final PDF pdf) {
		tempItemDummy = null;
		return search(rootItem.getChildren(), pdf);
	}

	private TreeItem<Directory> search(final ObservableList<TreeItem<Directory>> children, final PDF pdf) {
		for (TreeItem<Directory> treeItem : children) {
			if (treeItem.getValue().getUrl().equals(pdf.getUrl())) {
				this.tempItemDummy = treeItem;
			}
			search(treeItem.getChildren(), pdf);
		}
		return tempItemDummy;
	}

	public void updateGraphic(PDF pdf) {
		final List<Integer> allLocalPDFSizes = new LocalDataReader().getAllLocalPDFSizes();
		TreeItem<Directory> treeItem = getLinkedTreeItem(pdf);
		ImageView image;
		if (allLocalPDFSizes.contains(pdf.getSize())) {
			image = new ImageView("img/pdf.png");
		} else {
			image = new ImageView("img/pdf_local_not_there.png");
		}
		if (pdf.isIgnored()) {
			image = new ImageView("img/pdf_ignored.png");
		}
		setGraphic(image, treeItem);
	}

	private void setGraphic(final ImageView image, final TreeItem<Directory> treeItem) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				treeItem.setExpanded(false);
				treeItem.setGraphic(image);
				treeItem.setExpanded(true);
			}
		});
	}
}
