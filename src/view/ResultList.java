package view;

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
import model.Directory;
import model.PDF;
import model.Settings;
import control.Downloader;
import control.Ignorer;

public class ResultList extends ListView<Directory> {
	private final ObservableList<Directory> items;
	private final CoursesTreeView courses;
	private ContextMenu menu;

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
		if (event.getCode() == KeyCode.DELETE) {
			new Ignorer().act();
			getSelectionModel().selectNext();
		} else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			final Directory selectedDirectory = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
			courses.expandTreeItem(selectedDirectory);
		} else if (event.getCode() == KeyCode.ENTER && Settings.getInstance().userIsLoggedIn()) {
			new Downloader().download((Directory) event.getSource());
		}
	}

	private void showContextMenu(MouseEvent event) {
		menu.hide();
		final Directory selectedDirectory = ((ResultList) event.getSource()).getSelectionModel().getSelectedItem();
		if (selectedDirectory == null) {
			return;
		}
		courses.expandTreeItem(selectedDirectory);
		if (event.getButton() == MouseButton.SECONDARY) {
			final PDF pdf = (PDF) selectedDirectory;
			menu = new FileContextMenu().createMenu(pdf, event);
			menu.show(this, event.getScreenX(), event.getScreenY());
		}
	}

	public void clear() {
		items.clear();
	}

	public void add(Directory dir) {
		items.add(dir);
	}
}
