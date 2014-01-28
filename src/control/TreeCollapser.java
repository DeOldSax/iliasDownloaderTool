package control;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import model.Directory;
import view.Dashboard;

public class TreeCollapser implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) {
		act(true);
	}

	public void act(boolean clearResultList) {
		collapse(Dashboard.getCoursesView().getRoot().getChildren());
		if (clearResultList) {
			Dashboard.clearResultList();
		}
	}

	private void collapse(ObservableList<TreeItem<Directory>> items) {
		for (TreeItem<Directory> item : items) {
			item.setExpanded(false);
			collapse(item.getChildren());
		}
		Dashboard.setStatusText("");
	}
}
