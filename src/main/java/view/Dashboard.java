package view;

import java.io.*;
import java.security.*;

import javafx.animation.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import javafx.stage.*;
import javafx.util.*;
import model.*;
import model.persistance.*;

import org.apache.log4j.*;
import org.bouncycastle.jce.provider.*;
import org.controlsfx.control.textfield.*;

import control.*;

public class Dashboard extends Application {

	private Dashboard dashboard;
	private Stage stage;
	private Scene scene;
	private LoginFader loginFader;
	private SplitPane splitPane;
	private StackPane stackPane;
	private GridPane actualisationTimePane;
	private BorderPane background;
	private WebView webView;
	private static Label lastUpdateTime;
	private static ResultList resultList;
	private Button settingsButton;
	private Button refreshButton;
	private static Label statusFooterText;
	private static CoursesTreeView courses;
	private Button signIn;
	private GridPane actionBar;
	private LoginFader loginFader2;
	private boolean loaderRunning;
	private CustomTextField searchField;
	private Button showLocalNotThere;
	private Button showIgnored;
	private RotateTransition refreshTransition;

	static {
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
	}

	public static void main(String[] args) {
		new File(System.getProperty("user.home") + "/.ilias/ilias.log").delete();
		PropertyConfigurator.configure(Dashboard.class.getResourceAsStream("log4j.properties"));
		Logger.getLogger(Dashboard.class).warn("Start IliasDownloaderTool.");

		boolean newVersionCalled = new VersionValidator().validate();
		if (newVersionCalled) {
			System.exit(0);
		}

		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		dashboard = this;
		stage.getIcons().add(new Image("img/folder.png"));
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Settings settings = Settings.getInstance();
				settings.getFlags().setLogin(false);
				settings.getFlags().setUpdateCanceled(false);
				settings.store();
				System.exit(0);
			};
		});
		this.stage = stage;
		background = new BorderPane();
		background.setPadding(new Insets(20, 50, 20, 50));

		actionBar = new GridPane();

		final GridPane login = new GridPane();
		login.setId("loginBackground");
		login.setHgap(10);
		login.setVgap(5);

		Button goBack = new Button();
		goBack.setId("loginButtonCancel");
		loginFader2 = new LoginFader(this, -500, login);
		goBack.setOnAction(loginFader2);
		TextField username = new TextField();
		username.setId("textField");
		username.setPromptText("Benutzererkennung");
		username.setText(Settings.getInstance().getUser().getName());
		PasswordField password = new PasswordField();
		password.setText(Settings.getInstance().getUser().getPassword());
		password.setId("textField");
		password.setPromptText("Passwort");
		RadioButton savePwd = new RadioButton("Speichern");
		savePwd.setSelected(true);
		Button loginBtn = new Button();
		loginFader = new LoginFader(this, -500, login);
		loginBtn.setId("loginButtonGO");
		loginFader.getT().setOnFinished(new LoginProvider(this, username, password, savePwd));
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

		courses = new CoursesTreeView(this);
		resultList = new ResultList(this);

		actualisationTimePane = new GridPane();
		actualisationTimePane.setHgap(10);
		actualisationTimePane.setVgap(5);
		lastUpdateTime = new Label(IliasTreeStorage.getUpdateTime());
		lastUpdateTime.setId("lastUpdateTimeLabel");
		actualisationTimePane.add(new Label(), 0, 0);
		actualisationTimePane.add(new Label(), 0, 1);
		actualisationTimePane.add(lastUpdateTime, 0, 2);

		stackPane = new StackPane();
		stackPane.getChildren().add(actualisationTimePane);
		stackPane.getChildren().add(login);
		stackPane.getChildren().add(actionBar);

		actionBar.prefWidthProperty().bind(background.widthProperty());
		actionBar.setHgap(10);

		Button collapseTree = new Button();
		collapseTree.setId("collapseButton");
		collapseTree.getStyleClass().add("doButton");
		collapseTree.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				courses.collapse();
			}
		});
		collapseTree.setTooltip(new Tooltip("Alle Ordner schließen"));
		collapseTree.prefWidthProperty().bind(actionBar.prefWidthProperty());
		refreshButton = new Button();

		refreshTransition = new RotateTransition(Duration.millis(1000), refreshButton);
		final Tooltip tooltip = new Tooltip("Aktualisieren");
		refreshButton.setTooltip(tooltip);
		refreshButton.setId("loaderButton");
		refreshButton.getStyleClass().add("doButton");
		refreshButton.setMouseTransparent(true);
		refreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (loaderRunning) {
					Settings.getInstance().getFlags().setUpdateCanceled(true);
					showLoader(false);
				} else {
					showLoader(true);
					if (Settings.getInstance().getFlags().isUserLoggedIn()) {
						new Thread(new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								new IliasStarter(dashboard).loadIliasTree();
								LocalFileStorage.getInstance().refresh();
								return null;
							}
						}).start();
						// LocalFileStorage.getInstance().refresh();
					}
				}
			}
		});
		signIn = new Button("Anmelden");
		signIn.setId("loginButton");
		signIn.setOnAction(new LoginFader(this, 500, login));
		signIn.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showLocalNotThere = new Button("Lokal nicht vorhandene Dateien " + "("
				+ resultList.getUnsynchronizedPdfs().size() + ")");
		showLocalNotThere.getStyleClass().add("actionButton");
		showLocalNotThere.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showLocalNotThere.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				resultList.showUnsynchronizedPdfs();
			}
		});
		showIgnored = new Button("Ignorierte Dateien " + "("
				+ resultList.getIgnoredIliasPdfs().size() + ")");
		showIgnored.getStyleClass().add("actionButton");
		showIgnored.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				resultList.showIgnoredFiles();
			}
		});
		showIgnored.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showIgnored.setMaxWidth(Double.MAX_VALUE);
		searchField = new CustomTextField();
		ImageView loupe = new ImageView();
		loupe.setId("loupe");
		searchField.setRight(loupe);
		searchField.setId("textField");
		searchField.setPromptText("Datei suchen");
		searchField.setOnKeyReleased(event -> {
			resultList.showPdfMatches(searchField.getText());
		});
		searchField.setOnMouseEntered(event -> {
			loupe.setId("loupeHover");
		});
		searchField.setOnMouseExited(event -> {
			if (!searchField.isFocused()) {
				loupe.setId("loupe");
			}
		});
		searchField.focusedProperty().addListener(changed -> {
			if (searchField.isFocused()) {
				loupe.setId("loupeHover");
			} else {
				loupe.setId("loupe");
			}
		});
		searchField.prefWidthProperty().bind(actionBar.prefWidthProperty());
		settingsButton = new Button();
		settingsButton.setId("settingsButton");
		settingsButton.getStyleClass().add("doButton");

		settingsButton.setOnAction(event -> {
			SettingsMenu menu = new SettingsMenu(dashboard);
			menu.show(settingsButton);
		});

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(5);
		ColumnConstraints col15 = new ColumnConstraints();
		col15.setPercentWidth(5);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(15);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(30);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(20);
		ColumnConstraints col5 = new ColumnConstraints();
		col5.setPercentWidth(20);
		ColumnConstraints col6 = new ColumnConstraints();
		col6.setPercentWidth(5);
		actionBar.getColumnConstraints().addAll(col1, col15, col2, col3, col4, col5, col6);

		actionBar.add(collapseTree, 0, 0);
		actionBar.add(refreshButton, 1, 0);
		actionBar.add(signIn, 2, 0);
		actionBar.add(showLocalNotThere, 3, 0);
		actionBar.add(showIgnored, 4, 0);
		actionBar.add(searchField, 5, 0);
		actionBar.add(settingsButton, 6, 0);

		background.setTop(stackPane);

		splitPane = new SplitPane();
		splitPane.setId("splitPane");

		splitPane.setDividerPositions(0.6f, 0.4f);

		splitPane.getItems().addAll(courses, resultList.getPane());

		background.setCenter(splitPane);

		GridPane statusFooter = new GridPane();
		statusFooter.setPadding(new Insets(10, 0, 0, 0));
		statusFooterText = new Label("");
		statusFooterText.setId("statusFooterText");
		statusFooter.add(statusFooterText, 0, 0);

		GridPane footer = new GridPane();
		footer.setVgap(20);
		footer.add(statusFooter, 0, 0);
		background.setBottom(footer);

		scene = new Scene(background);
		scene.getStylesheets().add("skin/lightgreen.css");
		setScene();
		stage.setTitle("Ilias");
		LocalFileStorage.getInstance().refresh();
		iliasTreeReloaded(false);
		stage.show();

		if (Settings.getInstance().getFlags().isAutoLogin()) {
			signIn.setMouseTransparent(true);
			setStatusText("", false);
			showLoader(true);
			setMenuTransparent(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Settings newSettings = Settings.getInstance();
					User user = newSettings.getUser();
					String name = user.getName();
					String password = user.getPassword();
					final IliasStarter iliasStarter = new IliasStarter(dashboard, name, password);
					final boolean loginSuccessfull = iliasStarter.login();
					if (loginSuccessfull && newSettings.getFlags().autoUpdate()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								setLoaderButtonActivated(true);
								loaderRunning = true;
							}
						});
						iliasStarter.loadIliasTree();
					}
				}
			}).start();
		}
	}

	public void setScene() {
		stage.setMinWidth(1100);
		stage.setMinHeight(500);
		stage.setScene(scene);
		stage.sizeToScene();
	}

	public void iliasTreeReloaded(final boolean showFinishText) {
		courses.update();
		resultList.refresh();
		if (showFinishText) {
			setStatusText("Aktualisierung beendet.", true);
			updateUpdateTime();
		}
	}

	public static void updateUpdateTime() {
		IliasTreeStorage.setUpdateTime();
		lastUpdateTime.setText(IliasTreeStorage.getUpdateTime());
	}

	public void fadeInLogin() {
		loginFader2.fadeIn();
	}

	public void setMenuTransparent(final boolean b) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				actionBar.setMouseTransparent(b);
			}
		});
	}

	public void setSigInTransparent(final boolean b) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				signIn.setMouseTransparent(b);
			}
		});
	}

	public void showLoader(final boolean show) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (show) {
					setLoaderButtonActivated(true);
					loaderRunning = true;
					refreshButton.getTooltip().setText("Aktualisieren abbrechen");
				} else {
					refreshButton.getTooltip().setText("Aktualisieren");
					setLoaderButtonActivated(false);
					loaderRunning = false;
					refreshButton.setMouseTransparent(false);
					signIn.setText("Angemeldet");
					signIn.setOpacity(1);
				}
			}
		});
	}

	public void setSignInColor() {
		signIn.setId("loginButtonActive");
	}

	public void setTitle(final String title) {
		stage.setTitle(title);
	}

	public static void setStatusText(final String text, boolean alert) {
		final TranslateTransition t = new TranslateTransition(Duration.millis(600),
				statusFooterText);
		t.setInterpolator(Interpolator.EASE_BOTH);
		t.setFromX(statusFooterText.getLayoutX() - 500);
		t.setToX(statusFooterText.getLayoutX());
		if (alert) {
			statusFooterText.setId("statusFooterAlertText");
		} else {
			statusFooterText.setId("statusFooterText");
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				statusFooterText.setText(text);
			}
		});
		t.play();
	}

	public void setStatusText(final String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				statusFooterText.setId("statusFooterText");
				statusFooterText.setText(text);
			}
		});
	}

	public static void updateGraphicInTree(final IliasFile file) {
		courses.fileStatusChanged(file);
	}

	public void pdfIgnoredStateChanged(IliasFile file) {
		if (file.isIgnored()) {
			setStatusText(file.getName() + " wurde auf ignorieren gesetzt.", false);
		} else {
			setStatusText(file.getName() + " wird nicht mehr ignoriert.", false);
		}
		courses.fileStatusChanged(file);
	}

	public void browse(String url) {
		webView = new WebView();
		background.setCenter(webView);
		final GridPane webControllerPane = new GridPane();
		webControllerPane.add(new Label(), 0, 0);
		webControllerPane.add(new Label(), 0, 1);
		final Button button = new Button("X");
		button.setId("closeBrowser");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stackPane.getChildren().remove(webControllerPane);
				background.setCenter(splitPane);
			}
		});
		webControllerPane.add(button, 0, 3);

		stackPane.getChildren().add(webControllerPane);

		background.setCenter(webView);
		WebEngine engine = webView.getEngine();
		engine.load(url);
	}

	public static void fileDownloaded(IliasFile file) {
		setStatusText("Download abgeschlossen", false);
		updateGraphicInTree(file);
		resultList.fileSynchronizedStateChanged(file);
	}

	public ResultList getResultList() {
		return resultList;
	}

	public CoursesTreeView getCoursesTreeView() {
		return courses;
	}

	public GridPane getActionBar() {
		return actionBar;
	}

	public String getSearchFieldInput() {
		return searchField.getText();
	}

	public void setNumberOfUnsynchronizedPdfs(int number) {
		showLocalNotThere.setText("Lokal nicht vorhandene Dateien " + "(" + String.valueOf(number)
				+ ")");
	}

	public void setNumberofIngoredPdfs(int number) {
		showIgnored.setText("Ignorierte Dateien " + "(" + String.valueOf(number) + ")");
	}

	private void setLoaderButtonActivated(boolean activate) {
		if (activate) {
			refreshTransition.setByAngle(360f);
			refreshTransition.setCycleCount(Timeline.INDEFINITE);
			refreshTransition.play();
			refreshButton.setId("loaderButtonActive");
		} else {
			refreshTransition.stop();
			refreshButton.setId("loaderButton");
		}
		refreshButton.getStyleClass().add("doButton");
	}

	public void showSettingsPrompt() {
		SettingsMenu menu = new SettingsMenu(dashboard);
		menu.changeLocalIliasFolderButton();
		menu.activatePromptUpdater();
		menu.show(settingsButton);
	}
}
