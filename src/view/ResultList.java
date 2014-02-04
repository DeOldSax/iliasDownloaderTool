package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import model.IliasPdf;
import model.IliasTreeNode;
import model.IliasTreeProvider;
import model.Settings;
import control.Downloader;
import control.LocalPdfStorage;

public class ResultList extends ListView<IliasTreeNode> {
	private final Dashboard dashboard;
	private final GridPane headerPane;
	private final Label listHeader;
	private final ObservableList<IliasTreeNode> items;
	private final BorderPane listPane;
	public ResultListMode listMode;
	private ContextMenu menu;

	enum ResultListMode {
		IGNORE_MODE, SEARCH_MODE, PDF_NOT_SYNCHRONIZED;
	}

	public ResultList(Dashboard dashboard) {
		super();
		this.dashboard = dashboard;
		setMinWidth(260);
		listHeader = new Label();
		listHeader.setId("listHeaderText");
		listHeader.setTextAlignment(TextAlignment.CENTER);

		headerPane = new GridPane();
		listPane = new BorderPane();

		headerPane.setId("listHeader");
		listPane.setCenter(this);

		listPane.setTop(headerPane);
		headerPane.add(listHeader, 0, 0);

		menu = new ContextMenu();
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
		if (event.getCode() == KeyCode.DELETE && listMode.equals(ResultListMode.IGNORE_MODE)) {
			final IliasPdf pdf = (IliasPdf) getSelectionModel().getSelectedItem();
			Settings.getInstance().togglePdfIgnored(pdf);
			dashboard.pdfIgnoredStateChanged(pdf);
			pdfIgnoredStateChanged(pdf);
			getSelectionModel().selectNext();
		} else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			final IliasTreeNode selectedDirectory = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
			dashboard.getCoursesTreeView().selectPdf((IliasPdf) selectedDirectory);
		} else if (event.getCode() == KeyCode.ENTER && Settings.getInstance().userIsLoggedIn()) {
			new Downloader().download(getSelectionModel().getSelectedItem());
		}
	}

	private void showContextMenu(MouseEvent event) {
		menu.hide();
		final IliasTreeNode selectedNode = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
		if (selectedNode == null) {
			return;
		}
		dashboard.getCoursesTreeView().selectPdf((IliasPdf) selectedNode);
		if (event.getButton() == MouseButton.SECONDARY) {
			final IliasPdf pdf = (IliasPdf) selectedNode;
			menu = new FileContextMenu(dashboard).createMenu(pdf, event);
			menu.show(this, event.getScreenX(), event.getScreenY());
		}
	}

	public void pdfIgnoredStateChanged(IliasPdf pdf) {
		dashboard.setNumberofIngoredPdfs(getIgnoredIliasPdfs().size());
		dashboard.setNumberOfUnsynchronizedPdfs(getUnsynchronizedPdfs().size());
		if (listMode != ResultListMode.IGNORE_MODE) {
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

	public void pdfSynchronizedStateChanged(IliasPdf pdf) {
		if (listMode != ResultListMode.PDF_NOT_SYNCHRONIZED) {
			return;
		}
		showUnsynchronizedPdfs();
	}

	public void refresh() {
		if (listMode == ResultListMode.IGNORE_MODE) {
			showIgnoredFiles();
		} else if (listMode == ResultListMode.PDF_NOT_SYNCHRONIZED) {
			showUnsynchronizedPdfs();
		} else if (listMode == ResultListMode.SEARCH_MODE) {
			showPdfMatches(dashboard.getSearchFieldInput());
		}
	}

	public void showIgnoredFiles() {
		listMode = ResultListMode.IGNORE_MODE;
		items.clear();
		ArrayList<IliasPdf> ignoredPdf = getIgnoredIliasPdfs();
		setListHeader(" Ignorierte Dateien", "red");
		for (IliasPdf pdf : ignoredPdf) {
			items.add(pdf);
		}
		dashboard.setStatusText(ignoredPdf.size() + " ignorierte Dateien in Ignorieren-Liste.", false);
		dashboard.setNumberofIngoredPdfs(ignoredPdf.size());
	}

	public ArrayList<IliasPdf> getIgnoredIliasPdfs() {
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();
		ArrayList<IliasPdf> ignoredPdf = new ArrayList<IliasPdf>();
		for (IliasPdf pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				ignoredPdf.add(pdf);
			}
		}
		return ignoredPdf;
	}

	protected void showUnsynchronizedPdfs() {
		listMode = ResultListMode.PDF_NOT_SYNCHRONIZED;
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();
		final List<IliasPdf> unsynchronizedPdfs = new ArrayList<IliasPdf>();

		final Set<Integer> allLocalPdfSizes = LocalPdfStorage.getInstance().getAllLocalPDFSizes();
		for (IliasPdf pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				continue;
			}
			if (!allLocalPdfSizes.contains(pdf.getSize())) {
				unsynchronizedPdfs.add(pdf);
			}
		}
		dashboard.setStatusText("Gesamt: " + allPdfFiles.size() + ", davon sind " + unsynchronizedPdfs.size()
				+ " noch nicht im Ilias Ordner.", false);
		setListHeader(" Lokal nicht vorhandene Dateien ", "");
		dashboard.setNumberOfUnsynchronizedPdfs(unsynchronizedPdfs.size());
		items.clear();

		for (IliasPdf pdf : unsynchronizedPdfs) {
			items.add(pdf);
		}
	}

	protected List<IliasPdf> getUnsynchronizedPdfs() {
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();
		final List<IliasPdf> unsynchronizedPdfs = new ArrayList<IliasPdf>();

		final Set<Integer> allLocalPdfSizes = LocalPdfStorage.getInstance().getAllLocalPDFSizes();
		for (IliasPdf pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				continue;
			}
			if (!allLocalPdfSizes.contains(pdf.getSize())) {
				unsynchronizedPdfs.add(pdf);
			}
		}
		return unsynchronizedPdfs;
	}

	public void showPdfMatches(String inputString) {
		listMode = ResultListMode.SEARCH_MODE;
		items.clear();

		if (inputString.length() == 0 || inputString.equals(" ")) {
			setListHeader(" Gefundene Dateien " + "(" + String.valueOf(0) + ")", "green");
			return;
		}

		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();

		if (allPdfFiles.isEmpty()) {
			dashboard.setStatusText("Keine passende Datei gefunden.", false);
			return;
		}
		dashboard.setStatusText("");

		List<IliasPdf> alreadyAddedPDF = new ArrayList<IliasPdf>();

		for (IliasPdf pdf : allPdfFiles) {
			if (pdf.isIgnored()) {
				continue;
			}
			final String[] splitedStrings = pdf.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = (splitedStrings[i] + " ").toLowerCase();
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].startsWith(inputString.toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (inputString.contains(" ") && pdf.getName().toLowerCase().contains(inputString.toLowerCase())
						&& !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(inputString.toLowerCase()) && !alreadyAddedPDF.contains(pdf)) {
					alreadyAddedPDF.add(pdf);
					continue;
				}
				if (pdf.getParentFolder() != null) {
					if (inputString.length() > 3 && pdf.getParentFolder().getName().toLowerCase().contains(inputString.toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
				if (pdf.getParentFolder().getParentFolder() != null) {
					if (inputString.length() > 3
							&& pdf.getParentFolder().getParentFolder().getName().toLowerCase().contains(inputString.toLowerCase())
							&& !alreadyAddedPDF.contains(pdf)) {
						alreadyAddedPDF.add(pdf);
					}
					continue;
				}
			}
		}
		setListHeader(" Gefundene Dateien " + "(" + String.valueOf(alreadyAddedPDF.size()) + ")", "green");
		if (alreadyAddedPDF.isEmpty()) {
			dashboard.setStatusText("Keine Datei gefunden.");
		} else {
			for (IliasPdf pdf : alreadyAddedPDF) {
				items.add(pdf);
			}
		}
	}

	private void setListHeader(String text, String color) {
		String textColor = "-fx-text-fill: #3d3d3d";
		listHeader.setText(text);
		if (color.equals("red")) {
			color = "-fx-background-color: linear-gradient(red, darkred)";
			textColor = "-fx-text-fill: white";
		} else if (color.equals("green")) {
			color = "-fx-background-color: linear-gradient(lime, limegreen)";
			textColor = "-fx-text-fill: white";
		} else {
			color = "-fx-background-color: 	#c3c4c4,linear-gradient(#d6d6d6 50%, white 100%),radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);";
		}
		headerPane.setStyle(color);
		listHeader.setStyle(textColor);
	}

	protected BorderPane getPane() {
		return listPane;
	}
}
