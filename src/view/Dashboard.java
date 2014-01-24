package view;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Directory;
import model.FileStorageProvider;
import model.PDF;
import model.StorageProvider;
import control.FileSearcher;
import control.FileSystem;
import control.IgnoredPdfFilter;
import control.JTreeContentFiller;
import control.LocalDataMatcher;

public class Dashboard extends Application {

	private static Stage stage;
	private static TreeItem<Directory> rootItem;
	private static Scene scene;
	private LoginFader loginFader;
	private static Label statusFooterText;
	private static TreeView<Directory> courses;
	private static ObservableList<Directory> items;
	private static Button signIn;
	private static GridPane menu;
	private static LoginFader loginFader2;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Dashboard.stage = stage;
		final BorderPane background = new BorderPane();
		background.setPadding(new Insets(10, 10, 10, 10));

		menu = new GridPane();
		menu.setPadding(new Insets(0, 0, 30, 0));

		final GridPane login = new GridPane();
		login.setId("loginBackground");
		login.setHgap(10);
		login.setVgap(5);

		Button goBack = new Button("X");
		goBack.setId("loginButtonCancel");
		loginFader2 = new LoginFader(-500, login, menu);
		goBack.setOnAction(loginFader2);
		TextField username = new TextField();
		username.setId("userField");
		username.setPromptText("Benutzererkennung");
		username.setText(new StorageProvider().getUsername());
		PasswordField password = new PasswordField();
		password.setText(new StorageProvider().getPassword());
		password.setId("userField");
		password.setPromptText("Passwort");
		RadioButton savePwd = new RadioButton("Speichern");
		savePwd.setSelected(true);
		Button loginBtn = new Button("Login");
		loginFader = new LoginFader(-500, login, menu);
		loginBtn.setId("loginButton");
		loginFader.getT().setOnFinished(new LoginProvider(username, password, savePwd));
		loginBtn.setOnAction(loginFader);
		username.setOnAction(loginFader);
		password.setOnAction(loginFader);
		Separator separator = new Separator(Orientation.VERTICAL);

		login.add(goBack, 0, 0);
		login.add(username, 1, 0);
		login.add(password, 2, 0);
		login.add(loginBtn, 3, 0);
		login.add(savePwd, 1, 1);
		login.add(separator, 4, 0);
		login.setOpacity(0);

		StackPane stack = new StackPane();
		stack.getChildren().add(login);
		stack.getChildren().add(menu);

		menu.prefWidthProperty().bind(background.widthProperty());
		menu.setHgap(10);

		Button collapseTree = new Button();
		collapseTree.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("arrow.png"))));
		collapseTree.prefWidthProperty().bind(menu.prefWidthProperty());
		signIn = new Button("Anmelden");
		signIn.setOnAction(new LoginFader(500, login, menu));
		signIn.prefWidthProperty().bind(menu.prefWidthProperty());
		Button showLocalNotThere = new Button("Lokal nicht vorhandene Dateien");
		showLocalNotThere.prefWidthProperty().bind(menu.prefWidthProperty());
		showLocalNotThere.setOnAction(new LocalDataMatcher());
		Button showIgnored = new Button("Ignorierte Dateien");
		showIgnored.setOnAction(new IgnoredPdfFilter());
		showIgnored.prefWidthProperty().bind(menu.prefWidthProperty());
		showIgnored.setMaxWidth(Double.MAX_VALUE);
		TextField searchField = new TextField();
		searchField.prefWidthProperty().bind(menu.prefWidthProperty());
		searchField.setPromptText("Datei suchen");
		searchField.setId("searchField");
		searchField.setOnKeyPressed(new FileSearcher(searchField));
		Button settings = new Button();
		settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("settings.png"))));
		settings.setId("settingsBtn");
		settings.setOnAction(new SettingsMenu());

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(5);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(15);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(30);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(20);
		ColumnConstraints col5 = new ColumnConstraints();
		col5.setPercentWidth(25);
		ColumnConstraints col6 = new ColumnConstraints();
		col6.setPercentWidth(5);
		menu.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);

		menu.add(collapseTree, 0, 0);
		menu.add(signIn, 1, 0);
		menu.add(showLocalNotThere, 2, 0);
		menu.add(showIgnored, 3, 0);
		menu.add(searchField, 4, 0);
		menu.add(settings, 5, 0);

		background.setTop(stack);

		SplitPane splitPane = new SplitPane();
		splitPane.setId("splitPane");

		rootItem = new TreeItem<Directory>(new Directory("Übersicht", null, null));
		rootItem.setExpanded(true);
		courses = new TreeView<Directory>(rootItem);
		courses.setOnMouseClicked(new TreePopupShower(courses));
		courses.setShowRoot(false);
		courses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		items = FXCollections.observableArrayList();
		ListView<Directory> listView = new ListView<Directory>(items);
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		splitPane.setDividerPositions(0.6f, 0.4f);

		splitPane.getItems().addAll(courses, listView);

		background.setCenter(splitPane);

		GridPane statusFooter = new GridPane();
		statusFooter.setPadding(new Insets(10, 0, 0, 0));
		statusFooterText = new Label("");
		statusFooterText.setId("statusFooterText");
		statusFooter.add(statusFooterText, 0, 0);
		background.setBottom(statusFooter);

		setStatusText(new FileStorageProvider().getActualisationDate(), false);
		update();

		scene = new Scene(background);
		scene.getStylesheets().add("skin/DashboardStyle.css");
		setScene();
		stage.show();
		stage.setTitle("Ilias");
	}

	public static void setScene(Scene scene, double minWidth) {
		stage.setMinWidth(minWidth);
		stage.setScene(scene);
		stage.sizeToScene();
	}

	public static void setScene() {
		stage.setMinWidth(950);
		stage.setScene(scene);
		stage.sizeToScene();
	}

	public static void update() {
		courses.getRoot().getChildren().clear();
		new JTreeContentFiller().addKurseToTree(rootItem, FileSystem.getAllFiles());
	}

	public static void fadeInLogin() {
		loginFader2.fadeIn();
	}

	public static void clearResultList() {
		items.clear();
	}

	public static void addToResultList(final PDF pdf) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				items.add(pdf);
			}
		});
	}

	public static void setMenuTransparent(boolean b) {
		menu.setMouseTransparent(b);
	}

	public static void showLoader(boolean show) {
		signIn.setMouseTransparent(show);
		if (show) {
			signIn.setOpacity(0.5);
		} else {
			signIn.setOpacity(1);
		}
	}

	public static void setTitle(String title) {
		stage.setTitle(title);
	}

	public static void setStatusText(String text, boolean alert) {
		TranslateTransition t = new TranslateTransition(Duration.millis(600), statusFooterText);
		t.setInterpolator(Interpolator.EASE_BOTH);
		t.setFromX(statusFooterText.getLayoutX() - 500);
		t.setToX(statusFooterText.getLayoutX());
		if (alert) {
			statusFooterText.setStyle(statusFooterText.getStyle() + "-fx-text-fill: red");
		}
		statusFooterText.setText(text);
		t.play();
	}

	public static TreeItem<Directory> getSelectedTreeItem() {
		return courses.getSelectionModel().getSelectedItem();
	}
}
