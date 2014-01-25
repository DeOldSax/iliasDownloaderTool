package control;

import java.awt.Desktop;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.PDF;
import view.Dashboard;

public class Printer implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent event) {
		PDF pdf = (PDF) Dashboard.getSelectedItem();
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().print(pdf.getFileOnLocalDisk());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
