package worker;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import model.SearchResult;

public class ResultListPopupMenu extends MouseAdapter {
	private static final String DOWNLOAD = "herunterladen";
	private final JPopupMenu menu;
	private JList<SearchResult> list;

	public ResultListPopupMenu() {
		this.menu = new JPopupMenu();
		final JMenuItem downloadMenuItem = new JMenuItem(DOWNLOAD);
		downloadMenuItem.setOpaque(true);
		downloadMenuItem.setBackground(Color.WHITE);
		downloadMenuItem.addMouseListener(new Closer());
		menu.add(downloadMenuItem);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		list = (JList<SearchResult>) event.getSource();

		if (SwingUtilities.isRightMouseButton(event) && !(list.getSelectedValuesList().size() == 0)) {
			showPopMenu(event);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		list = (JList<SearchResult>) event.getSource();
		if (event.getClickCount() == 2 && !(list.getSelectedValuesList().size() == 0)) {
			startDownload();
		}
	}

	/**
	 * @param e
	 * @wbp.parser.entryPoint
	 */
	private void showPopMenu(MouseEvent e) {
		menu.show(e.getComponent(), e.getX(), e.getY());
		menu.setVisible(true);
	}

	private void startDownload() {
		List<SearchResult> selectedValuesList = list.getSelectedValuesList();
		for (SearchResult searchResult : selectedValuesList) {
			new PdfDownloader().download(searchResult.getPdf());
		}
	}

	private class Closer extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			menu.setVisible(false);
			startDownload();
		}

	}
}
