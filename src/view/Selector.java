package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import model.Settings;

public class Selector implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		Settings settings = Settings.getInstance();
		Button btn = (Button) event.getSource();
		if (btn.getText().equals("Anmelden")) {
			if (settings.autoLogin()) {
				settings.setAutoLogin(false);
				btn.setStyle("-fx-background-color: 	#3d3d3d;");
			} else {
				settings.setAutoLogin(true);
				btn.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
		if (btn.getText().equals("Aktualisieren")) {
			if (settings.autoUpdate()) {
				settings.setAutoUpdate(false);
				btn.setStyle("-fx-background-color: 	#3d3d3d;");
			} else {
				settings.setAutoUpdate(true);
				btn.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
	}
}
