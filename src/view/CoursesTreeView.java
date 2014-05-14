package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import utils.DesktopHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.IliasFolder;
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.IliasTreeProvider;
import model.Settings;
import control.LocalPdfStorage;
import download.IliasPdfDownloadCaller;
import download.IliasFolderDownloaderTask;

public class CoursesTreeView extends TreeView<IliasTreeNode> {
	private final TreeItem<IliasTreeNode> rootItem;
	private ContextMenu menu;
	private final Dashboard dashboard;

	public CoursesTreeView(Dashboard dashboard) {
		super();
		setId("coursesTree");
		setMinWidth(270);
		this.dashboard = dashboard;
		rootItem = new TreeItem<IliasTreeNode>(new IliasFolder("Übersicht", null, null));
		rootItem.setExpanded(true);
		setRoot(rootItem);
		setShowRoot(false);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		menu = new ContextMenu();
		setCellFactory(new Callback<TreeView<IliasTreeNode>, TreeCell<IliasTreeNode>>() {
			
			@Override
			public TreeCell<IliasTreeNode> call(TreeView<IliasTreeNode> arg0) {
				return new IliasTreeCell();
			}
		});
		
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
							new Thread(new IliasPdfDownloadCaller(((CoursesTreeView) event.getSource()).getSelectionModel().getSelectedItem()
									.getValue())).start();
						}
					}
				} else if (event.getButton() == MouseButton.SECONDARY) {
					showContextMenu(getSelectionModel().getSelectedItems(), event);
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

	private void showContextMenu(ObservableList<TreeItem<IliasTreeNode>> selectedItems, MouseEvent event) {
		menu.getItems().clear();

		List<IliasTreeNode> selectedIliasTreeNodes = new ArrayList<IliasTreeNode>(); 
		for (TreeItem<IliasTreeNode> treeItem : selectedItems) {
			selectedIliasTreeNodes.add(treeItem.getValue()); 					
		}
		
		menu = new FileContextMenu(dashboard).createMenu(selectedIliasTreeNodes, event);
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
	
	/**
	 * This class draws the ListCells.
	 * 
	 * @author deoldsax
	 *
	 */
	private class IliasTreeCell extends TreeCell<IliasTreeNode> {
		
		private IliasTreeNode node; 
		
		public IliasTreeCell() {
			EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					if (node != null) {
						redraw();
					}
				}
			};
			setOnMouseEntered(mouseHandler);
			setOnMouseExited(mouseHandler);
		}
		
		@Override
		protected void updateItem(final IliasTreeNode node, final boolean empty) {
			super.updateItem(node, empty);
			
			if (empty) {
				this.node = null; 
			} else {
				this.node = node; 
			}
			redraw(); 
		}
		
		private void redraw() {
			if (node == null) {
				setGraphic(null);
				return; 
			}
			boolean addOptions = this.isHover(); 
			final BorderPane pane = new BorderPane(); 
			final Label box = new Label();
			box.setAlignment(Pos.TOP_LEFT);

			if (node instanceof IliasFolder) {
				IliasFolder folder = (IliasFolder) node;
				if (LocalPdfStorage.getInstance().isFolderSynchronized(folder)) {
					box.setGraphic(new ImageView("img/folder.png"));
				} else {
					box.setGraphic(new ImageView("img/folder_pdf_not_there.png"));
				}
			} else if (node instanceof IliasPdf) {
				IliasPdf pdf = (IliasPdf) node;
				if (pdf.isIgnored()) {
					box.setGraphic(new ImageView("img/pdf_ignored.png"));
				} else if (!(LocalPdfStorage.getInstance().contains(pdf))) {
					box.setGraphic(new ImageView("img/pdf_local_not_there.png"));
				} else {
					box.setGraphic(new ImageView("img/pdf.png"));
				}
			} else if (node instanceof IliasForum) {
				box.setGraphic(new ImageView("img/forum.png"));
			}
			
			box.setText(node.toString());
			pane.setLeft(box);
			createAndAddActions(node, pane);
			setGraphic(pane);
		}

		private void createAndAddActions(final IliasTreeNode node, final BorderPane pane) {
			Button downloadButton = new Button(); 
			downloadButton.setGraphic(new ImageView("img/downloadArrow.png"));
			downloadButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					download(node); 
				}
			});
			Button ignoreButton = new Button(); 
			ignoreButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					toggleIgnoredState(node);
				}
			});
			Button openerButton = new Button(); 
			openerButton.setGraphic(new ImageView("img/folder_small.png"));
			openerButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DesktopHelper.openFile((IliasPdf)node);
				}
			});
			Button printerButton = new Button(); 
			printerButton.setGraphic(new ImageView("img/printer.png"));
			printerButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DesktopHelper.print((IliasPdf)node);
				}
			});
			
			HBox actions = new HBox(); 
			actions.setId("actionBar");
			actions.setSpacing(10);
			
			if (!(node instanceof IliasForum)) {
				actions.getChildren().add(downloadButton); 
			}
			if (node instanceof IliasPdf) {
				if (((IliasPdf)node).isIgnored()) {
					ignoreButton.setGraphic(new ImageView("img/check.png"));
				} else {
					ignoreButton.setGraphic(new ImageView("img/ignore.png"));
				}
				actions.getChildren().add(ignoreButton); 
				actions.getChildren().add(openerButton); 
				actions.getChildren().add(printerButton); 
			}
			pane.setRight(actions);
		}
		
		private void download(IliasTreeNode node) {
			if (node instanceof IliasPdf) {
				new Thread(new IliasPdfDownloadCaller(node)).start(); 
			} else if (node instanceof IliasFolder) {
				new Thread(new IliasFolderDownloaderTask(node)).start();
			}
		}

		private void toggleIgnoredState(final IliasTreeNode node) {
			IliasPdf pdf = (IliasPdf)node;
			Settings.getInstance().togglePdfIgnored(pdf);
			dashboard.pdfIgnoredStateChanged(pdf);
			dashboard.getResultList().pdfIgnoredStateChanged(pdf);
			pdfStatusChanged(pdf);
		}
	}
}
