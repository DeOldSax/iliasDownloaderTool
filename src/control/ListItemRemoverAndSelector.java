package control;

import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Directory;
import view.Dashboard;

public class ListItemRemoverAndSelector implements EventHandler<KeyEvent> {
	private final ListView<Directory> listView;

	public ListItemRemoverAndSelector(ListView<Directory> listView) {
		this.listView = listView;
	}

	@Override
	public void handle(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			new Ignorer().act();
			listView.getSelectionModel().selectNext();
		} else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			final Directory selectedDirectory = Dashboard.getSelectedDirectory();
			Dashboard.expandTreeItem(selectedDirectory);
		}
	}
}
