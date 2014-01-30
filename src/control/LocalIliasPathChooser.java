package control;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Settings;
import view.SettingsMenu;

public class LocalIliasPathChooser implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Lokaler Ilias-Ordner");
		final String loadLocalIliasFolderPath = Settings.getInstance().loadLocalIliasFolderPath();
		if (!loadLocalIliasFolderPath.equals(".")) {
			directoryChooser.setInitialDirectory(new File(loadLocalIliasFolderPath));
		}

		final File file = directoryChooser.showDialog(new Stage());

		if (file != null) {
			Settings.getInstance().storeLocalIliasFolderPath(file.getAbsolutePath());
		}
		SettingsMenu.updateLocalIliasFolderPath();
	}
}
