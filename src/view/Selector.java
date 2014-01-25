package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import model.StorageProvider;

public class Selector implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final StorageProvider storageProvider = new StorageProvider();
		Button btn = (Button) event.getSource();
		if (!btn.getStyle().contains("-fx-background-color: 	linear-gradient(steelblue,royalblue)")) {
			btn.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			if (btn.getText().equals("Anmelden")) {
				storageProvider.setAutoLogin(true);
			}
			if (btn.getText().equals("Aktualisieren")) {
				storageProvider.setAutoUpdate(true);
			}
		} else {
			btn.setStyle("-fx-background-color: 	#3d3d3d;");
			if (btn.getText().equals("Anmelden")) {
				storageProvider.setAutoLogin(false);
			}
			if (btn.getText().equals("Aktualisieren")) {
				storageProvider.setAutoUpdate(false);
			}
		}
	}
}
