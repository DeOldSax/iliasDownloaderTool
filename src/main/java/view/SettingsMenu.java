package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.persistance.Flags;
import model.persistance.Settings;

public class SettingsMenu implements EventHandler<ActionEvent> {
	private static Button localIliasPath;
	private final GridPane gridPane;
	private Button autoUpdate;
	private Button autoLogin;
	private static Scene scene;
	private static boolean promptUpdater;
	private static FadeTransition t;
	private static Dashboard dashboard;

	public SettingsMenu(Dashboard dashboard) {
		SettingsMenu.dashboard = dashboard;
		gridPane = new GridPane();
		gridPane.setPadding(new Insets(50, 50, 50, 50));
		gridPane.setHgap(20);
		gridPane.setVgap(20);
		initDialog();
		scene = new Scene(gridPane);
		scene.getStylesheets().add("skin/SettingsMenuStyle.css");
	}

	@Override
	public void handle(ActionEvent event) {
		show();
	}

	public static void show() {
		changeLocalIliasFolderButton();
		dashboard.setScene(scene, 900);
	}

	private void initDialog() {

		final Button goBackTodashboard = new Button("zurück");
		goBackTodashboard.setId("goBackButton");
		goBackTodashboard.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dashboard.setScene();
			}
		});
		gridPane.add(goBackTodashboard, 0, 0);

		Label selectIliasLocalBtn = new Label("Mein Lokaler Ilias Ordner:            ");
		gridPane.add(selectIliasLocalBtn, 0, 2);

		localIliasPath = new Button();
		localIliasPath.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showFileChooser();
			}
		});

		Button help = new Button("?");
		help.setId("help");
		final String helpText = "Der lokale ILIAS-Ordner ist der Ordner, in dem du auf deinem Computer die PDF-Dateien aus dem ILIAS speicherst.\nDiese Angabe wird ben\u00F6tigt, damit ein Abgleich stattfinden kann, welche Dateien du bereits besitzt und welche noch nicht.\nDie Benennung deiner Unterordner oder Dateien spielt dabei keine Rolle.";
		final Tooltip tooltip = new Tooltip(helpText);
		help.setTooltip(tooltip);

		Text txtrDerLokaleIlias = new Text();
		txtrDerLokaleIlias.setText(helpText);

		HBox boxX = new HBox();
		boxX.setSpacing(20);
		boxX.getChildren().addAll(localIliasPath, help);

		gridPane.add(boxX, 1, 2);

		Label startActions = new Label("Bei jedem Start ausführen:          ");
		autoLogin = new Button("Anmelden");
		final EventHandler<ActionEvent> toggleButton = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toggleButtonColor(event);
			}
		};
		autoLogin.setOnAction(toggleButton);
		autoLogin.setId("autoBtn");
		autoUpdate = new Button("Aktualisieren");
		autoUpdate.setId("autoBtn");
		autoUpdate.setOnAction(toggleButton);
		HBox box = new HBox();
		box.setSpacing(20);
		box.getChildren().addAll(autoLogin, autoUpdate);
		Flags flags = Settings.getInstance().getFlags();
		if (flags.isAutoLogin()) {
			autoLogin.setStyle("-fx-background-color: linear-gradient(steelblue,royalblue)");
		}
		if (flags.autoUpdate()) {
			autoUpdate.setStyle("-fx-background-color: linear-gradient(steelblue,royalblue)");
		}

		gridPane.add(startActions, 0, 4);
		gridPane.add(box, 1, 4);

		Label contactDeveloper = new Label("Noch Fragen?:         ");
		Button emailAdress = new Button("Entwickler kontaktieren");
		emailAdress.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openEmailDialog();
			}

		});
		Button faq = new Button("FAQ");
		faq.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openIliasWiki();
			}

		});
		HBox box2 = new HBox();
		box2.setSpacing(20);
		box2.getChildren().addAll(faq, emailAdress);
		gridPane.add(contactDeveloper, 0, 5);
		gridPane.add(box2, 1, 5);
	}

	private void showFileChooser() {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Lokaler Ilias Ordner");

		final String localIliasFolderPath = Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath();
		if (!localIliasFolderPath.equals(".")) {
			directoryChooser.setInitialDirectory(new File(localIliasFolderPath));
		}

		final File selectedFile = directoryChooser.showDialog(new Stage());

		if (selectedFile != null) {
			Settings.getInstance().getIliasFolderSettings().setLocalIliasFolderPath(selectedFile.getAbsolutePath());
			Settings.getInstance().getFlags().setLocalIliasPathStored(true);
			localIliasPath.setText(selectedFile.getAbsolutePath());
			updateLocalIliasFolderPath();
		} else if (!localIliasFolderPath.equals(".")) {
			localIliasPath.setText(localIliasFolderPath);
		} else {
			localIliasPath.setText("Ilias Ordner auswählen");
			Settings.getInstance().getFlags().setLocalIliasPathStored(false);
		}
		changeLocalIliasFolderButton();
	}

	private static void updateLocalIliasFolderPath() {
		if (promptUpdater) {
			dashboard.iliasTreeReloaded(true);
			promptUpdater = false;
		}
	}

	public static void activatePromptUpdater() {
		promptUpdater = true;
	}

	private void openEmailDialog() {
		try {
			Desktop.getDesktop().mail(new URI("mailto:DeOldSax@gmx.de?subject=Bugreport/Verbesserungsvorschlag/Frage"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void openIliasWiki() {
		try {
			Desktop.getDesktop().browse(new URI("https://github.com/DeOldSax/iliasDownloaderTool/wiki"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void changeLocalIliasFolderButton() {
		if (Settings.getInstance().getFlags().isLocalIliasPathStored()) {
			localIliasPath.setStyle("-fx-background-color:linear-gradient(steelblue,royalblue)");
			localIliasPath.setText(Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath());
			getBlinkyTransition().stop();
			localIliasPath.setOpacity(1);
		} else {
			if (Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath().equals(".")) {
				localIliasPath.setText("Ilias Ordner auswählen");
			}
			localIliasPath.setStyle("-fx-background-color: linear-gradient(red, darkred)");
			getBlinkyTransition().play();
		}
	}

	private static FadeTransition getBlinkyTransition() {
		if (t == null) {
			t = new FadeTransition(Duration.millis(500), localIliasPath);
			t.setToValue(0.1);
			t.setFromValue(1.0);
			t.setCycleCount(Timeline.INDEFINITE);
			t.setAutoReverse(true);
		}
		return t;
	}

	private void toggleButtonColor(ActionEvent event) {
		Flags flags = Settings.getInstance().getFlags();
		Button button = (Button) event.getSource();
		if (button.equals(autoLogin)) {
			if (flags.isAutoLogin()) {
				flags.setAutoLogin(false);
				button.setStyle(null);
				button.setId("button");
			} else {
				flags.setAutoLogin(true);
				button.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
		if (button.equals(autoUpdate)) {
			if (flags.autoUpdate()) {
				flags.setAutoUpdate(false);
				button.setStyle(null);
				button.setId("button");
			} else {
				flags.setAutoUpdate(true);
				button.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
	}
}
