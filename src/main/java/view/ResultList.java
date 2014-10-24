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
import model.IliasFile;
import model.IliasTreeNode;
import model.persistance.IliasTreeProvider;
import model.persistance.Settings;
import control.LocalFileStorage;
import download.IliasPdfDownloadCaller;

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
		listHeader.setId("listHeaderTextNormal");
		listHeader.setTextAlignment(TextAlignment.CENTER);

		headerPane = new GridPane();
		listPane = new BorderPane();

		headerPane.setId("listHeaderNormal");
		listPane.setCenter(this);

		listPane.setTop(headerPane);
		headerPane.add(listHeader, 0, 0);

		menu = new ContextMenu();
		setId("listView");
		items = FXCollections.observableArrayList();
		setItems(items);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
						&& Settings.getInstance().getFlags().isUserLoggedIn()) {
					new Thread(new IliasPdfDownloadCaller(((ResultList) event.getSource())
							.getSelectionModel().getSelectedItem())).start();
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
			final IliasFile file = (IliasFile) getSelectionModel().getSelectedItem();
			Settings.getInstance().toggleFileIgnored(file);
			dashboard.pdfIgnoredStateChanged(file);
			pdfIgnoredStateChanged(file);
			getSelectionModel().selectNext();
		} else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			final IliasTreeNode selectedDirectory = ((ResultList) event.getSource())
					.getSelectionModel().getSelectedItem();
			dashboard.getCoursesTreeView().selectFile((IliasFile) selectedDirectory);
		} else if (event.getCode() == KeyCode.ENTER
				&& Settings.getInstance().getFlags().isUserLoggedIn()) {
			new Thread(new IliasPdfDownloadCaller(getSelectionModel().getSelectedItem())).start();
		}
	}

	private void showContextMenu(MouseEvent event) {
		menu.hide();
		final List<IliasTreeNode> selectedNodes = ((ResultList) event.getSource())
				.getSelectionModel().getSelectedItems();
		if (selectedNodes.isEmpty() || selectedNodes == null) {
			return;
		}
		if (selectedNodes.size() == 1) {
			dashboard.getCoursesTreeView().selectFile((IliasFile) selectedNodes.get(0));
		}

		if (event.getButton() == MouseButton.SECONDARY) {
			menu = new FileContextMenu(dashboard).createMenu(selectedNodes, event);
			menu.show(this, event.getScreenX(), event.getScreenY());
		}
	}

	public void pdfIgnoredStateChanged(IliasFile file) {
		dashboard.setNumberofIngoredPdfs(getIgnoredIliasPdfs().size());
		dashboard.setNumberOfUnsynchronizedPdfs(getUnsynchronizedPdfs().size());
		if (listMode != ResultListMode.IGNORE_MODE) {
			return;
		}
		if (file.isIgnored()) {
			if (!items.contains(file)) {
				items.add(file);
			}
		} else {
			items.remove(file);
		}
	}

	public void fileSynchronizedStateChanged(IliasFile file) {
		dashboard.setNumberofIngoredPdfs(getIgnoredIliasPdfs().size());
		dashboard.setNumberOfUnsynchronizedPdfs(getUnsynchronizedPdfs().size());
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
		dashboard.setNumberofIngoredPdfs(getIgnoredIliasPdfs().size());
		dashboard.setNumberOfUnsynchronizedPdfs(getUnsynchronizedPdfs().size());
	}

	public void showIgnoredFiles() {
		listMode = ResultListMode.IGNORE_MODE;
		items.clear();
		ArrayList<IliasFile> ignoredPdf = getIgnoredIliasPdfs();
		setListHeader(" Ignorierte Dateien", "red");
		for (IliasFile pdf : ignoredPdf) {
			items.add(pdf);
		}
		dashboard.setStatusText(ignoredPdf.size() + " ignorierte Dateien in Ignorieren-Liste.",
				false);
		dashboard.setNumberofIngoredPdfs(ignoredPdf.size());
	}

	public ArrayList<IliasFile> getIgnoredIliasPdfs() {
		final List<IliasFile> allFiles = IliasTreeProvider.getAllIliasFiles();
		ArrayList<IliasFile> ignoredFiles = new ArrayList<IliasFile>();
		for (IliasFile file : allFiles) {
			if (file.isIgnored()) {
				ignoredFiles.add(file);
			}
		}
		return ignoredFiles;
	}

	protected void showUnsynchronizedPdfs() {
		listMode = ResultListMode.PDF_NOT_SYNCHRONIZED;
		final List<IliasFile> allFiles = IliasTreeProvider.getAllIliasFiles();
		final List<IliasFile> unsynchronizedFiles = new ArrayList<IliasFile>();

		final Set<Integer> allLocalFileSizes = LocalFileStorage.getInstance()
				.getAllLocalFileSizes();
		for (IliasFile file : allFiles) {
			if (file.isIgnored()) {
				continue;
			}
			if (!allLocalFileSizes.contains(file.getSize())) {
				unsynchronizedFiles.add(file);
			}
		}
		dashboard.setStatusText("Gesamt: " + allFiles.size() + ", davon sind "
				+ unsynchronizedFiles.size() + " noch nicht im Ilias Ordner.", false);
		setListHeader(" Lokal nicht vorhandene Dateien ", "");
		dashboard.setNumberOfUnsynchronizedPdfs(unsynchronizedFiles.size());
		items.clear();

		for (IliasFile file : unsynchronizedFiles) {
			items.add(file);
		}
	}

	protected List<IliasFile> getUnsynchronizedPdfs() {
		final List<IliasFile> allFiles = IliasTreeProvider.getAllIliasFiles();
		final List<IliasFile> unsynchronizedPdfs = new ArrayList<IliasFile>();

		final Set<Integer> allLocalPdfSizes = LocalFileStorage.getInstance().getAllLocalFileSizes();
		for (IliasFile file : allFiles) {
			if (file.isIgnored()) {
				continue;
			}
			if (!allLocalPdfSizes.contains(file.getSize())) {
				unsynchronizedPdfs.add(file);
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

		final List<IliasFile> allFiles = IliasTreeProvider.getAllIliasFiles();

		if (allFiles.isEmpty()) {
			dashboard.setStatusText("Keine passende Datei gefunden.", false);
			return;
		}
		dashboard.setStatusText("");

		List<IliasFile> alreadyAddedFile = new ArrayList<IliasFile>();

		for (IliasFile file : allFiles) {
			if (file.isIgnored()) {
				continue;
			}
			final String[] splitedStrings = file.getName().split(" ");
			for (int i = 0; i < splitedStrings.length; i++) {
				splitedStrings[i] = (splitedStrings[i] + " ").toLowerCase();
			}
			for (int i = 0; i < splitedStrings.length; i++) {
				if (splitedStrings[i].startsWith(inputString.toLowerCase())
						&& !alreadyAddedFile.contains(file)) {
					alreadyAddedFile.add(file);
					continue;
				}
				if (inputString.contains(" ")
						&& file.getName().toLowerCase().contains(inputString.toLowerCase())
						&& !alreadyAddedFile.contains(file)) {
					alreadyAddedFile.add(file);
					continue;
				}
				if (splitedStrings[i].toLowerCase().contains(inputString.toLowerCase())
						&& !alreadyAddedFile.contains(file)) {
					alreadyAddedFile.add(file);
					continue;
				}
				if (file.getParentFolder() != null) {
					if (inputString.length() > 3
							&& file.getParentFolder().getName().toLowerCase()
									.contains(inputString.toLowerCase())
							&& !alreadyAddedFile.contains(file)) {
						alreadyAddedFile.add(file);
					}
					continue;
				}
				if (file.getParentFolder().getParentFolder() != null) {
					if (inputString.length() > 3
							&& file.getParentFolder().getParentFolder().getName().toLowerCase()
									.contains(inputString.toLowerCase())
							&& !alreadyAddedFile.contains(file)) {
						alreadyAddedFile.add(file);
					}
					continue;
				}
			}
		}
		setListHeader(" Gefundene Dateien " + "(" + String.valueOf(alreadyAddedFile.size()) + ")",
				"green");
		if (alreadyAddedFile.isEmpty()) {
			dashboard.setStatusText("Keine Datei gefunden.");
		} else {
			for (IliasFile file : alreadyAddedFile) {
				items.add(file);
			}
		}
	}

	private void setListHeader(String text, String color) {
		listHeader.setText(text);
		if (color.equals("red")) {
			headerPane.setId("listHeaderIgnored");
			listHeader.setId("listHeaderTextIgnored");
		} else if (color.equals("green")) {
			headerPane.setId("listHeaderSearch");
			listHeader.setId("listHeaderTextSearch");
		} else {
			headerPane.setId("listHeaderNormal");
			listHeader.setId("listHeaderTextNormal");
		}
	}

	protected BorderPane getPane() {
		return listPane;
	}
}
