package view;

import java.io.File;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.IliasFile;
import model.persistance.IliasTreeStorage;
import model.persistance.Settings;
import model.persistance.User;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import control.IliasStarter;
import control.LocalFileStorage;
import control.LoginProvider;
import control.VersionValidator;

public class Dashboard extends Application {

	private Dashboard dashboard;
	private static Stage stage;
	private static Scene scene;
	private LoginFader loginFader;
	private SplitPane splitPane;
	private StackPane stackPane;
	private GridPane actualisationTimePane;
	private BorderPane background;
	private WebView webView;
	private static Label lastUpdateTime;
	private static ResultList resultList;
	private Button settingsBtn;
	private Button refreshButton;
	private static Label statusFooterText;
	private static CoursesTreeView courses;
	private Button signIn;
	private GridPane actionBar;
	private LoginFader loginFader2;
	private ImageView refreshIcon;
	private ImageView refreshIconBlack;
	private boolean loaderRunning;
	private TextField searchField;
	private Button showLocalNotThere;
	private Button showIgnored;
	private RotateTransition refreshTransition;

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
		refreshIcon = new ImageView("img/loader.png");
		refreshIconBlack = new ImageView("img/loaderIconBlack.png");
		Dashboard.stage = stage;
		background = new BorderPane();
		background.setPadding(new Insets(20, 50, 20, 50));

		actionBar = new GridPane();

		final GridPane login = new GridPane();
		login.setId("loginBackground");
		login.setHgap(10);
		login.setVgap(5);

		Button goBack = new Button("X");
		goBack.setId("loginButtonCancel");
		loginFader2 = new LoginFader(this, -500, login);
		goBack.setOnAction(loginFader2);
		TextField username = new TextField();
		username.setId("userField");
		username.setPromptText("Benutzererkennung");
		username.setText(Settings.getInstance().getUser().getName());
		PasswordField password = new PasswordField();
		password.setText(Settings.getInstance().getUser().getPassword());
		password.setId("userField");
		password.setPromptText("Passwort");
		RadioButton savePwd = new RadioButton("Speichern");
		savePwd.setSelected(true);
		Button loginBtn = new Button("Login");
		loginFader = new LoginFader(this, -500, login);
		loginBtn.setId("loginButton");
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
		lastUpdateTime = new Label(IliasTreeStorage.getActualisationDate());
		lastUpdateTime.setId("lastUpdateTimeLbl");
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
		collapseTree.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				courses.collapse();
			}
		});
		collapseTree.setTooltip(new Tooltip("Alle Ordner schließen"));
		collapseTree.setGraphic(new ImageView("img/collapse.png"));
		collapseTree.prefWidthProperty().bind(actionBar.prefWidthProperty());
		refreshButton = new Button();
		refreshButton.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				refreshButton.setGraphic(refreshIconBlack);
			};
		});
		refreshButton.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				refreshButton.setGraphic(refreshIcon);
			};
		});

		refreshTransition = new RotateTransition(Duration.millis(1000), refreshButton);
		final Tooltip tooltip = new Tooltip("Aktualisieren");
		refreshButton.setTooltip(tooltip);
		refreshButton.setId("loader");
		refreshButton.setGraphic(refreshIcon);
		refreshButton.setAlignment(Pos.CENTER);
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
						new Thread(new Runnable() {
							@Override
							public void run() {
								new IliasStarter(dashboard).loadIliasTree();
							}
						}).start();
						LocalFileStorage.getInstance().refresh();
					}
				}
			}
		});
		signIn = new Button("Anmelden");
		signIn.setOnAction(new LoginFader(this, 500, login));
		signIn.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showLocalNotThere = new Button("Lokal nicht vorhandene Dateien " + "(" + resultList.getUnsynchronizedPdfs().size() + ")");
		showLocalNotThere.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showLocalNotThere.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				resultList.showUnsynchronizedPdfs();
			}
		});
		showIgnored = new Button("Ignorierte Dateien " + "(" + resultList.getIgnoredIliasPdfs().size() + ")");
		showIgnored.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				resultList.showIgnoredFiles();
			}
		});
		showIgnored.prefWidthProperty().bind(actionBar.prefWidthProperty());
		showIgnored.setMaxWidth(Double.MAX_VALUE);
		searchField = new TextField();
		searchField.setId("searchTextField");
		searchField.setPromptText("Datei suchen");
		searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				resultList.showPdfMatches(searchField.getText());
			};
		});
		searchField.prefWidthProperty().bind(actionBar.prefWidthProperty());
		settingsBtn = new Button();
		settingsBtn.setGraphic(new ImageView("img/settings.png"));
		settingsBtn.setId("settingsBtn");
		settingsBtn.setOnAction(new SettingsMenu(dashboard));

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
		actionBar.add(settingsBtn, 6, 0);

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

//		StudportBar studportBar = new StudportBar(this);

		GridPane footer = new GridPane();
		footer.setVgap(20);
		footer.add(statusFooter, 0, 0);
//		footer.add(studportBar, 0, 1);
		background.setBottom(footer);

		scene = new Scene(background);
		scene.getStylesheets().add("skin/DashboardStyle.css");
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
								// loader.setGraphic(loaderGif);
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

	public void setScene(Scene scene, double minWidth) {
		stage.setMinWidth(minWidth);
		stage.sizeToScene();
		stage.setScene(scene);
	}

	public static void setScene() {
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
		IliasTreeStorage.setActualisationDate();
		lastUpdateTime.setText(IliasTreeStorage.getActualisationDate());
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
		signIn.setStyle("-fx-background-color: linear-gradient(lime, limegreen)");
	}

	public void setTitle(final String title) {
		stage.setTitle(title);
	}

	public static void setStatusText(final String text, boolean alert) {
		final TranslateTransition t = new TranslateTransition(Duration.millis(600), statusFooterText);
		t.setInterpolator(Interpolator.EASE_BOTH);
		t.setFromX(statusFooterText.getLayoutX() - 500);
		t.setToX(statusFooterText.getLayoutX());
		if (alert) {
			statusFooterText.setStyle("-fx-text-fill: linear-gradient(lime, limegreen)");
		} else {
			statusFooterText.setStyle("-fx-text-fill: white");
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
				statusFooterText.setStyle("-fx-text-fill: white");
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
		showLocalNotThere.setText("Lokal nicht vorhandene Dateien " + "(" + String.valueOf(number) + ")");
	}

	public void setNumberofIngoredPdfs(int number) {
		showIgnored.setText("Ignorierte Dateien " + "(" + String.valueOf(number) + ")");
	}

	private void setLoaderButtonActivated(boolean activate) {
		if (activate) {
			refreshTransition.setByAngle(360f);
			refreshTransition.setCycleCount(Timeline.INDEFINITE);
			refreshTransition.play();
		} else {
			refreshTransition.stop();
			refreshButton.setStyle(null);
			refreshButton.setId("loader");
		}
	}
}
