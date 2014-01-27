package control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Forum;
import view.Dashboard;

public class ForumOpener implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) {
		open();
	}

	public void open() {
		final Forum forum = (Forum) Dashboard.getSelectedDirectory();
		// if (Desktop.isDesktopSupported()) {
		// try {
		// Desktop.getDesktop().browse(new URI(forum.getUrl()));
		// } catch (IOException | URISyntaxException e) {
		// e.printStackTrace();
		// }
		// }
		Dashboard.browse(forum.getUrl());
	}
}
