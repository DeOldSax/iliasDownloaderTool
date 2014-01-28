package control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class Ignorer implements EventHandler<ActionEvent> {
	private Directory selectedDirectory;

	@Override
	public void handle(ActionEvent event) {
		act();
	}

	public void act() {
		selectedDirectory = Dashboard.getSelectedDirectory();

		if (!(selectedDirectory instanceof PDF)) {
			return;
		}
		PDF pdf = (PDF) selectedDirectory;
		if (pdf.isIgnored()) {
			pdf.setIgnored(false);
			Dashboard.setStatusText(pdf.getName() + " wird nicht mehr ignoriert.", false);
		} else {
			pdf.setIgnored(true);
			Dashboard.setStatusText(pdf.getName() + " wurde auf ignorieren gesetzt.", false);
		}
		new TreeNodeGraphicChanger().changeGraphicInTreeView(pdf);
		new IgnoredPdfFilter().filter();
	}
}
