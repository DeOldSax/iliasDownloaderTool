package control;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Directory;
import model.PDF;
import view.Dashboard;

public class ListPopUpShower extends PopupShower implements EventHandler<MouseEvent> {
	private final ListView<Directory> listView;
	private ContextMenu menu;

	public ListPopUpShower(ListView<Directory> listView) {
		this.listView = listView;
	}

	@Override
	public void handle(MouseEvent event) {
		final Directory selectedDirectory = Dashboard.getSelectedDirectory();
		Dashboard.expandTreeItem(selectedDirectory);
		if (event.getButton() == MouseButton.SECONDARY) {
			if (!(selectedDirectory instanceof PDF)) {
				return;
			}
			final PDF pdf = (PDF) selectedDirectory;
			menu = createMenu(pdf, event);
			menu.show(listView, event.getScreenX(), event.getScreenY());
			return;
		}
		if (menu != null) {
			menu.hide();
		}
	}
}
