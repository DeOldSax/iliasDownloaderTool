package control;

import java.awt.Desktop;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import model.Directory;
import model.PDF;

public class LocalFolderOpener implements EventHandler<ActionEvent> {

	private final TreeView<Directory> courses;

	public LocalFolderOpener(TreeView<Directory> courses) {
		this.courses = courses;
	}

	@Override
	public void handle(ActionEvent event) {
		PDF pdf = (PDF) courses.getSelectionModel().getSelectedItem().getValue();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(pdf.getParentFolderOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
