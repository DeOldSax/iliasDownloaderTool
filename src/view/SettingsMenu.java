package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import model.Settings;
import control.LocalIliasPathChooser;

public class SettingsMenu implements EventHandler<ActionEvent> {
	private static Button localIliasPath;
	private final Settings settings;
	private final GridPane gridPane;
	private static Scene scene;
	private static boolean promptUpdater;

	public SettingsMenu() {
		settings = Settings.getInstance();
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
		Dashboard.setScene(scene, 950);
	}

	private void initDialog() {

		final Button goBackToDashboard = new Button("zurück");
		goBackToDashboard.setId("goBackButton");
		goBackToDashboard.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Dashboard.setScene();
			}
		});
		gridPane.add(goBackToDashboard, 0, 0);

		Label selectIliasLocalBtn = new Label("Mein Lokaler Ilias Ordner:            ");
		gridPane.add(selectIliasLocalBtn, 0, 1);

		String loadLocalIliasFolderPath = settings.loadLocalIliasFolderPath();
		if (loadLocalIliasFolderPath.equals(".")) {
			loadLocalIliasFolderPath = "..\\Kit\\Semester XY";
		}
		localIliasPath = new Button(loadLocalIliasFolderPath);
		localIliasPath.setOnAction(new LocalIliasPathChooser());
		gridPane.add(localIliasPath, 1, 1);

		Button help = new Button("?");
		help.setId("help");
		final String helpText = "Der lokale ILIAS-Ordner ist der Ordner, in dem du auf deinem Computer die PDF-Dateien aus dem ILIAS speicherst.\nDiese Angabe wird ben\u00F6tigt, damit ein Abgleich stattfinden kann, welche Dateien du bereits besitzt und welche noch nicht.\nDie Benennung deiner Unterordner oder Dateien spielt dabei keine Rolle.";
		gridPane.add(help, 2, 1);
		final Tooltip tooltip = new Tooltip(helpText);
		help.setTooltip(tooltip);

		Text txtrDerLokaleIlias = new Text();
		txtrDerLokaleIlias.setText(helpText);

		Label startActions = new Label("Bei jedem Start ausführen:          ");
		final Button doLogin = new Button("Anmelden");
		doLogin.setOnAction(new Selector());
		Button doUpdate = new Button("Aktualisieren");
		doUpdate.setOnAction(new Selector());
		HBox box = new HBox();
		box.setSpacing(20);
		box.getChildren().addAll(doLogin, doUpdate);
		if (settings.autoLogin()) {
			doLogin.setStyle("-fx-background-color: linear-gradient(steelblue,royalblue)");
		}
		if (settings.autoUpdate()) {
			doUpdate.setStyle("-fx-background-color: linear-gradient(steelblue,royalblue)");
		}

		gridPane.add(startActions, 0, 3);
		gridPane.add(box, 1, 3);

		Label contactDeveloper = new Label("Noch Fragen?:         ");
		Button emailAdress = new Button("DeOldSax@gmx.de");
		emailAdress.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().mail(new URI("mailto:DeOldSax@gmx.de?subject=Bugreport/Verbesserungsvorschlag/Frage"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		Button faq = new Button("FAQ");
		faq.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/DeOldSax/iliasDownloaderTool/wiki"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		HBox box2 = new HBox();
		box2.setSpacing(20);
		box2.getChildren().addAll(faq, emailAdress);
		gridPane.add(contactDeveloper, 0, 4);
		gridPane.add(box2, 1, 4);
	}

	public static void updateLocalIliasFolderPath() {
		final Settings settings = Settings.getInstance();
		localIliasPath.setText(settings.loadLocalIliasFolderPath());
		settings.setLocalIliasPathTrue();
		if (promptUpdater) {
			Dashboard.update(true);
			promptUpdater = false;
		}
	}

	public static void activatePromptUpdater() {
		promptUpdater = true;
	}
}
