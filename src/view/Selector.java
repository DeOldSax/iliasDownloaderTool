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
		if (btn.getText().equals("Anmelden")) {
			if (storageProvider.autoLogin()) {
				storageProvider.setAutoLogin(false);
				btn.setStyle("-fx-background-color: 	#3d3d3d;");
			} else {
				storageProvider.setAutoLogin(true);
				btn.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
		if (btn.getText().equals("Aktualisieren")) {
			if (storageProvider.autoUpdate()) {
				storageProvider.setAutoUpdate(false);
				btn.setStyle("-fx-background-color: 	#3d3d3d;");
			} else {
				storageProvider.setAutoUpdate(true);
				btn.setStyle("-fx-background-color: 	linear-gradient(steelblue,royalblue)");
			}
			return;
		}
	}
}
