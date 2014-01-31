package view;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.Directory;
import model.FileStorage;
import model.PDF;
import model.Settings;
import control.IgnoredPdfFilter;
import control.IliasStarter;
import control.LocalDataMatcher;
import control.LoginProvider;

public class Dashboard extends Application {

	private static Stage stage;
	private static Scene scene;
	private LoginFader loginFader;
	private static GridPane help;
	private static Label listHeader;
	private static SplitPane splitPane;
	private static StackPane stackPane;
	private static GridPane updatePane;
	private static BorderPane background;
	private static WebView webView;
	private static Label lastUpdateTime;
	private static ResultList resultList;
	private static Button settingsBtn;
	private static Button loader;
	private static Label statusFooterText;
	private static CoursesTreeView courses;
	private static Button signIn;
	private static GridPane menu;
	private static LoginFader loginFader2;
	private static ImageView loaderIcon;
	private static ImageView loaderGif;
	private static boolean loaderRunning;
	private static ParallelTransition tp;
	private static Settings settings;

	public static void main(String[] args) {
		// boolean newVersionCalled = new VersionValidator().validate();
		// if (newVersionCalled) {
		// System.exit(0);
		// }
		settings = Settings.getInstance();

		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(new Image("img/folder.png"));
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				settings.setLogIn(false);
				settings.setUpdateCanceled(false);
				System.exit(0);
			};
		});
		loaderIcon = new ImageView("img/loader.png");
		loaderGif = new ImageView("img/loader.gif");
		Dashboard.stage = stage;
		background = new BorderPane();
		background.setPadding(new Insets(20, 50, 50, 50));

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
		username.setText(settings.getUsername());
		PasswordField password = new PasswordField();
		password.setText(settings.getPassword());
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

		updatePane = new GridPane();
		updatePane.setHgap(10);
		updatePane.setVgap(5);
		lastUpdateTime = new Label(FileStorage.getActualisationDate());
		lastUpdateTime.setId("lastUpdateTimeLbl");
		updatePane.add(new Label(), 0, 0);
		updatePane.add(new Label(), 0, 1);
		updatePane.add(lastUpdateTime, 0, 2);

		stackPane = new StackPane();
		stackPane.getChildren().add(updatePane);
		stackPane.getChildren().add(login);
		stackPane.getChildren().add(menu);

		menu.prefWidthProperty().bind(background.widthProperty());
		menu.setHgap(10);

		Button collapseTree = new Button();
		collapseTree.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				courses.collapse();
			}
		});
		collapseTree.setTooltip(new Tooltip("Alle Ordner schlieﬂen"));
		collapseTree.setGraphic(new ImageView("img/collapse.png"));
		collapseTree.prefWidthProperty().bind(menu.prefWidthProperty());
		loader = new Button();
		final Tooltip tooltip = new Tooltip("Aktualisieren");
		loader.setTooltip(tooltip);
		loader.setId("loader");
		loader.setGraphic(loaderIcon);
		loader.setMouseTransparent(true);
		loader.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (loaderRunning) {
					settings.setUpdateCanceled(true);
					showLoader(false);
				} else {
					showLoader(true);
					if (settings.userIsLoggedIn()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								new IliasStarter().watchForFolders();
							}
						}).start();
					}
				}
			}
		});
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
		TextField searchField = new SearchTextField();
		searchField.prefWidthProperty().bind(menu.prefWidthProperty());
		settingsBtn = new Button();
		settingsBtn.setGraphic(new ImageView("img/settings.png"));
		settingsBtn.setId("settingsBtn");
		settingsBtn.setOnAction(new SettingsMenu());

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(5);
		ColumnConstraints col15 = new ColumnConstraints();
		col1.setPercentWidth(5);
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
		menu.getColumnConstraints().addAll(col1, col15, col2, col3, col4, col5, col6);

		menu.add(collapseTree, 0, 0);
		menu.add(loader, 1, 0);
		menu.add(signIn, 2, 0);
		menu.add(showLocalNotThere, 3, 0);
		menu.add(showIgnored, 4, 0);
		menu.add(searchField, 5, 0);
		menu.add(settingsBtn, 6, 0);

		background.setTop(stackPane);

		splitPane = new SplitPane();
		splitPane.setId("splitPane");

		courses = new CoursesTreeView();
		resultList = new ResultList(courses);

		splitPane.setDividerPositions(0.6f, 0.4f);

		help = new GridPane();
		BorderPane listPane = new BorderPane();

		help.setId("listHeader");
		listPane.setCenter(resultList);

		listHeader = new Label();
		listHeader.setId("listHeaderText");
		listHeader.setTextAlignment(TextAlignment.CENTER);
		listPane.setTop(help);
		help.add(listHeader, 0, 0);

		splitPane.getItems().addAll(courses, listPane);

		background.setCenter(splitPane);

		GridPane statusFooter = new GridPane();
		statusFooter.setPadding(new Insets(10, 0, 0, 0));
		statusFooterText = new Label("");
		statusFooterText.setId("statusFooterText");
		statusFooter.add(statusFooterText, 0, 0);
		background.setBottom(statusFooter);

		scene = new Scene(background);
		scene.getStylesheets().add("skin/DashboardStyle.css");
		setScene();
		stage.setTitle("Ilias");
		update(false);
		stage.show();

		if (settings.autoLogin()) {
			signIn.setMouseTransparent(true);
			Dashboard.setStatusText("", false);
			Dashboard.showLoader(true);
			Dashboard.setMenuTransparent(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					final IliasStarter iliasStarter = new IliasStarter(settings.getUsername(), settings.getPassword());
					final boolean loginSuccessfull = iliasStarter.login();
					if (loginSuccessfull && settings.autoUpdate()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								loader.setGraphic(loaderGif);
								loaderRunning = true;
							}
						});
						iliasStarter.watchForFolders();
					}
				}
			}).start();
		}
	}

	public static void setScene(Scene scene, double minWidth) {
		stage.setMinWidth(minWidth);
		stage.sizeToScene();
		stage.setScene(scene);
	}

	public static void setScene() {
		stage.setMinWidth(950);
		stage.setScene(scene);
		stage.sizeToScene();
	}

	public static void update(boolean showFinishText) {
		courses.update();
		if (showFinishText) {
			setStatusText("Aktualisierung beendet.", true);
			updateUpdateTime();
		}
	}

	public static void updateUpdateTime() {
		FileStorage.setActualisationDate();
		lastUpdateTime.setText(FileStorage.getActualisationDate());
	}

	public static void fadeInLogin() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				loginFader2.fadeIn();
			}
		});
	}

	public static void clearResultList() {
		resultList.clear();
	}

	public static void addToResultList(final PDF pdf) {
		resultList.add(pdf);
	}

	public static void setMenuTransparent(boolean b) {
		menu.setMouseTransparent(b);
	}

	public static void setSigInTransparent(boolean b) {
		signIn.setMouseTransparent(b);
	}

	public static void showLoader(final boolean show) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (show) {
					loader.setGraphic(loaderGif);
					loaderRunning = true;
				} else {
					loader.setGraphic(loaderIcon);
					loaderRunning = false;
					loader.setMouseTransparent(false);
					signIn.setText("Angemeldet");
					signIn.setOpacity(1);
				}
			}
		});
	}

	public static void setSignInColor() {
		signIn.setStyle("-fx-background-color: linear-gradient(lime, limegreen)");
	}

	public static void setTitle(String title) {
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

	public static void setStatusText(final String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				statusFooterText.setStyle("-fx-text-fill: white");
				statusFooterText.setText(text);
			}
		});
	}

	public static Directory getSelectedDirectory() {
		if (courses.isFocused()) {
			return courses.getSelectionModel().getSelectedItem().getValue();
		}
		return resultList.getSelectionModel().getSelectedItem();
	}

	public static void startDownloadAnimation() {
		final Label downloadIcon = new Label();
		final Label icon2 = new Label();
		menu.add(downloadIcon, 6, 0);
		menu.add(icon2, 6, 0);
		downloadIcon.setGraphic(new ImageView("img/downloadArrow.png"));
		icon2.setGraphic(new ImageView("img/downloadArrow.png"));

		tp = new ParallelTransition();
		final TranslateTransition t = new TranslateTransition(Duration.millis(2000), downloadIcon);
		t.setInterpolator(new Interpolator() {
			@Override
			protected double curve(double x) {
				return Math.sqrt(x);
			}
		});
		t.setFromX(downloadIcon.getLayoutX() + 50);
		t.setByY(stage.getHeight() - 80);
		final TranslateTransition t2 = new TranslateTransition(Duration.millis(2000), icon2);
		t2.setDelay(Duration.millis(100));
		t2.setInterpolator(new Interpolator() {
			@Override
			protected double curve(double x) {
				return Math.sqrt(x);
			}
		});
		t2.setFromX(downloadIcon.getLayoutX() + 50);
		t2.setByY(stage.getHeight() - 80);
		tp.getChildren().addAll(t, t2);
		tp.play();
	}

	public static void updateGraphicInTree(PDF pdf) {
		courses.updateGraphic(pdf);
	}

	public static void browse(String url) {
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

	public static void setListHeader(String text, String color) {
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
		help.setStyle(color);
		listHeader.setStyle(textColor);
	}
}
