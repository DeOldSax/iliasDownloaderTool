package control;

import java.awt.Desktop;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.PDF;
import view.Dashboard;

public class FileOpener implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		final PDF selectedDirectory = (PDF) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(selectedDirectory.getFileOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
