package control;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.StorageProvider;
import view.SettingsMenu;

public class LocalIliasPathChooser implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Lokaler Ilias-Ordner");
		final String loadLocalIliasFolderPath = new StorageProvider().loadLocalIliasFolderPath();
		if (!loadLocalIliasFolderPath.equals(".")) {
			directoryChooser.setInitialDirectory(new File(loadLocalIliasFolderPath));
		}

		final File file = directoryChooser.showDialog(new Stage());

		if (file != null) {
			new StorageProvider().storeLocalIliasFolderPath(file.getAbsolutePath());
		}
		SettingsMenu.updateLocalIliasFolderPath();
	}
}
