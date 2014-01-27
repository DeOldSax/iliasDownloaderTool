package control;

import java.awt.Desktop;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.PDF;
import view.Dashboard;

public class LocalFolderOpener implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		PDF pdf = (PDF) Dashboard.getSelectedDirectory();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(pdf.getParentFolderOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
