package control;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Directory;
import model.Forum;
import model.PDF;
import view.Dashboard;

public class TreePopupShower extends PopupShower implements EventHandler<MouseEvent> {
	private ContextMenu menu;
	private final TreeView<Directory> courses;
	private final MenuItem openForumItem;

	public TreePopupShower(TreeView<Directory> courses) {
		this.courses = courses;
		menu = new ContextMenu();
		openForumItem = new MenuItem("Im Browser öffnen");
		openForumItem.setOnAction(new ForumOpener());
	}

	@Override
	public void handle(MouseEvent event) {
		final Directory selectedTreeItem = Dashboard.getSelectedDirectory();
		if (event.getClickCount() == 2) {
			if (selectedTreeItem instanceof Forum) {
				new ForumOpener().open();
			}
		}

		if (event.getButton() == MouseButton.SECONDARY) {
			menu.getItems().clear();
			if (selectedTreeItem instanceof Forum) {
				menu.getItems().add(openForumItem);
				menu.show(courses, event.getScreenX(), event.getScreenY());
				return;
			}
			if (!(selectedTreeItem instanceof PDF)) {
				menu.hide();
				return;
			}
			final PDF pdf = (PDF) selectedTreeItem;
			menu = createMenu(pdf, event);
			menu.show(courses, event.getScreenX(), event.getScreenY());
		} else {
			menu.hide();
		}
	}
}
