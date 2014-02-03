package view;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;
import control.Downloader;
import control.IliasTreeProvider;

public class ResultList extends ListView<IliasTreeNode> {
	private final ObservableList<IliasTreeNode> items;
	private final CoursesTreeView courses;
	private ContextMenu menu;
	public static int listMode = 0;
	public static final int IGNORE_MODE = 1;
	public static final int SEARCH_MODE = 2;
	public static final int PDF_NOT_SYNCHRONIZED = 3;

	public ResultList(CoursesTreeView courses) {
		super();
		menu = new ContextMenu();
		this.courses = courses;
		setId("listView");
		items = FXCollections.observableArrayList();
		setItems(items);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && Settings.getInstance().userIsLoggedIn()) {
					new Downloader().download(((ResultList) event.getSource()).getSelectionModel().getSelectedItem());
				} else {
					showContextMenu(event);
				}
			};
		});
		setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				handleKeyEvents(event);
			};
		});
	}

	private void handleKeyEvents(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE && listMode == IGNORE_MODE) {
			final IliasPdf pdf = (IliasPdf) getSelectionModel().getSelectedItem();
			Settings.getInstance().togglePdfIgnored(pdf);
			Dashboard.showPdfIgnoredState(pdf);
			pdfIgnoredStateChanged(pdf);
			getSelectionModel().selectNext();
		} else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			final IliasTreeNode selectedDirectory = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
			courses.selectPdf((IliasPdf) selectedDirectory);
		} else if (event.getCode() == KeyCode.ENTER && Settings.getInstance().userIsLoggedIn()) {
			new Downloader().download((IliasTreeNode) event.getSource());
		}
	}

	private void showContextMenu(MouseEvent event) {
		menu.hide();
		final IliasTreeNode selectedNode = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
		if (selectedNode == null) {
			return;
		}
		courses.selectPdf((IliasPdf) selectedNode);
		if (event.getButton() == MouseButton.SECONDARY) {
			final IliasPdf pdf = (IliasPdf) selectedNode;
			menu = new FileContextMenu().createMenu(pdf, event);
			menu.show(this, event.getScreenX(), event.getScreenY());
		}
	}

	public void clear() {
		items.clear();
	}

	public void add(IliasTreeNode dir) {
		items.add(dir);
	}

	public void pdfIgnoredStateChanged(IliasPdf pdf) {
		if (listMode != IGNORE_MODE) {
			return;
		}
		if (pdf.isIgnored()) {
			if (!items.contains(pdf)) {
				items.add(pdf);
			}
		} else {
			items.remove(pdf);
		}
	}

	public void showIgnoredFiles() {
		listMode = IGNORE_MODE;
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();
		ArrayList<IliasPdf> ignoredPdf = new ArrayList<IliasPdf>();
		for (IliasPdf pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				ignoredPdf.add(pdf);
			}
		}
		items.clear();
		Dashboard.setListHeader(" Ignorierte Dateien " + "(" + String.valueOf(ignoredPdf.size()) + ")", "red");
		for (IliasPdf pdf : ignoredPdf) {
			items.add(pdf);
		}
		Dashboard.setStatusText(ignoredPdf.size() + " ignorierte Dateien in Ignorieren-Liste.", false);
	}
}
