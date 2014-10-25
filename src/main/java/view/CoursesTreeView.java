package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.IliasFile;
import model.IliasFolder;
import model.IliasForum;
import model.IliasTreeNode;
import model.persistance.IliasTreeProvider;
import model.persistance.Settings;
import utils.DesktopHelper;
import download.IliasFolderDownloaderTask;
import download.IliasPdfDownloadCaller;

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
					} else if (selectedItem.getValue() instanceof IliasFile) {
						if (Settings.getInstance().getFlags().isUserLoggedIn()) {
							new Thread(new IliasPdfDownloadCaller(
									((CoursesTreeView) event.getSource()).getSelectionModel()
											.getSelectedItem().getValue())).start();
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

	private void showContextMenu(ObservableList<TreeItem<IliasTreeNode>> selectedItems,
			MouseEvent event) {
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
				item.setGraphic(node.getGraphic());
			} else if (node instanceof IliasFile) {
				item.setGraphic(node.getGraphic());
			} else if (node instanceof IliasForum) {
				item.setGraphic(node.getGraphic());
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

	public void selectFile(IliasFile selectedDirectory) {
		collapse();
		final TreeItem<IliasTreeNode> linkedTreeItem = getItem(selectedDirectory);
		linkedTreeItem.setExpanded(true);
		getSelectionModel().clearSelection();
		getSelectionModel().select(linkedTreeItem);
		scrollTo(getSelectionModel().getSelectedIndex());
	}

	public TreeItem<IliasTreeNode> getItem(final IliasFile file) {
		return search(rootItem, file);
	}

	private TreeItem<IliasTreeNode> search(TreeItem<IliasTreeNode> item, final IliasFile file) {
		for (TreeItem<IliasTreeNode> treeItem : item.getChildren()) {
			if (treeItem.getValue().equals(file)) {
				return treeItem;
			}
			final TreeItem<IliasTreeNode> searchResult = search(treeItem, file);
			if (searchResult != null) {
				return searchResult;
			}
		}
		return null;
	}

	public void fileStatusChanged(IliasFile iliasFile) {
		TreeItem<IliasTreeNode> treeItem = getItem(iliasFile);
		setGraphic(treeItem, iliasFile.getGraphic());
		while (treeItem.getParent() != null) {
			treeItem = treeItem.getParent();
			IliasFolder folder = (IliasFolder) treeItem.getValue();
			setGraphic(treeItem, folder.getGraphic());
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
	 * http://stackoverflow.com/questions/23137131
	 * /javafx-listview-with-button-in-each-cell
	 * 
	 * @author deoldsax
	 *
	 */
	private class IliasTreeCell extends TreeCell<IliasTreeNode> {

		private IliasTreeNode node;
		private int i = 0;
		private BorderPane pane;
		private Label box;
		private Button downloadButton;
		private Button ignoreButton;
		private Button openerButton;
		private Button printerButton;
		private HBox actions;
		private boolean calledfirsttime = true;

		public IliasTreeCell() {
			pane = buildCell();
			EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					if (node != null) {
						// redraw();
						update();
					}
				}
			};
			// setOnMouseEntered(mouseHandler);
			// setOnMouseExited(mouseHandler);
		}

		@Override
		protected void updateItem(final IliasTreeNode node, final boolean empty) {
			super.updateItem(node, empty);

			if (empty || (node == null)) {
				// this.node = null;
				setGraphic(null);
			} else {
				this.node = node;
				update();
				setGraphic(pane);
			}
			// redraw();
		}

		private BorderPane buildCell() {
			final BorderPane pane = new BorderPane();
			box = new Label();
			box.setAlignment(Pos.TOP_LEFT);

			pane.setLeft(box);
			createAndAddActions(pane);
			// setGraphic(pane);
			createToolTip();
			return pane;
		}

		private void update() {

			if (node instanceof IliasFolder) {
				IliasFolder folder = (IliasFolder) node;
				box.setGraphic(folder.getGraphic());
			} else if (node instanceof IliasFile) {
				IliasFile file = (IliasFile) node;
				box.setGraphic(file.getGraphic());
			} else if (node instanceof IliasForum) {
				box.setGraphic(new ImageView("img/forum.png"));
			}

			box.setText(node.toString());

			actions.getChildren().clear();
			if (!(node instanceof IliasForum)) {
				actions.getChildren().add(downloadButton);
			}
			if (node instanceof IliasFile) {
				if (((IliasFile) node).isIgnored()) {
					ignoreButton.setGraphic(new ImageView("img/check.png"));
				} else {
					ignoreButton.setGraphic(new ImageView("img/ignore.png"));
				}
				actions.getChildren().add(ignoreButton);
				actions.getChildren().add(openerButton);
				actions.getChildren().add(printerButton);
			}

			downloadButton.visibleProperty().bind(hoverProperty());
			downloadButton.mouseTransparentProperty().bind(hoverProperty().not());
			ignoreButton.visibleProperty().bind(hoverProperty());
			ignoreButton.mouseTransparentProperty().bind(hoverProperty().not());
			openerButton.visibleProperty().bind(hoverProperty());
			openerButton.mouseTransparentProperty().bind(hoverProperty().not());
			printerButton.visibleProperty().bind(hoverProperty());
			printerButton.mouseTransparentProperty().bind(hoverProperty().not());
		}

		private void redraw() {
			if (node == null) {
				setGraphic(null);
				return;
			}

			final BorderPane pane = new BorderPane();
			final Label box = new Label();
			box.setAlignment(Pos.TOP_LEFT);

			if (node instanceof IliasFolder) {
				IliasFolder folder = (IliasFolder) node;
				box.setGraphic(folder.getGraphic());
			} else if (node instanceof IliasFile) {
				IliasFile file = (IliasFile) node;
				box.setGraphic(file.getGraphic());
			} else if (node instanceof IliasForum) {
				box.setGraphic(new ImageView("img/forum.png"));
			}

			box.setText(node.toString());
			pane.setLeft(box);
			createAndAddActions(pane);
			setGraphic(pane);
			createToolTip();
		}

		private void createToolTip() {
			if (node instanceof IliasFile) {
				Tooltip tooltip = new Tooltip();
				String fileExtension = ((IliasFile) this.node).getExtension();
				String fileSizeLabel = ((IliasFile) this.node).getSizeLabel();
				tooltip.setText("Elementtyp: " + fileExtension + "\n" + "Größe: " + fileSizeLabel);
				this.setTooltip(tooltip);
			}
		}

		private void createAndAddActions(final BorderPane pane) {
			downloadButton = new Button();
			downloadButton.setGraphic(new ImageView("img/downloadArrow.png"));
			downloadButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					download(node);
				}
			});
			ignoreButton = new Button();
			ignoreButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					toggleIgnoredState(node);
				}
			});
			openerButton = new Button();
			openerButton.setGraphic(new ImageView("img/folder_small.png"));
			openerButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DesktopHelper.openFile((IliasFile) node);
				}
			});
			printerButton = new Button();
			printerButton.setGraphic(new ImageView("img/printer.png"));
			printerButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DesktopHelper.print((IliasFile) node);
				}
			});

			actions = new HBox();
			actions.setId("actionBar");
			actions.setSpacing(10);

			if (!(node instanceof IliasForum)) {
				actions.getChildren().add(downloadButton);
			}
			if (node instanceof IliasFile) {
				if (((IliasFile) node).isIgnored()) {
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
			if (node instanceof IliasFile) {
				new Thread(new IliasPdfDownloadCaller(node)).start();
			} else if (node instanceof IliasFolder) {
				new Thread(new IliasFolderDownloaderTask(node)).start();
			}
		}

		private void toggleIgnoredState(final IliasTreeNode node) {
			IliasFile file = (IliasFile) node;
			Settings.getInstance().toggleFileIgnored(file);
			dashboard.pdfIgnoredStateChanged(file);
			dashboard.getResultList().pdfIgnoredStateChanged(file);
			fileStatusChanged(file);
		}
	}
}
