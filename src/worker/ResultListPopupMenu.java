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
import model.StorageProvider;

public class ResultListPopupMenu extends MouseAdapter {
	private static final String DOWNLOAD = "herunterladen";
	private static final String IGNORE = "ignorieren";
	private static final String REMOVE_IGNORE = "ignorieren aufheben";
	private final JPopupMenu menu;
	private JList<SearchResult> list;
	private final JMenuItem ignorePdf;
	private final StorageProvider storageProvider;
	private final JMenuItem removeIgnore;

	public ResultListPopupMenu() {
		storageProvider = new StorageProvider();
		this.menu = new JPopupMenu();
		final JMenuItem downloadMenuItem = new JMenuItem(DOWNLOAD);
		downloadMenuItem.setOpaque(true);
		downloadMenuItem.setBackground(Color.WHITE);
		downloadMenuItem.addMouseListener(new Closer());
		ignorePdf = new JMenuItem(IGNORE);
		ignorePdf.setOpaque(true);
		ignorePdf.setBackground(Color.WHITE);
		ignorePdf.addMouseListener(new Closer());
		menu.add(downloadMenuItem);
		removeIgnore = new JMenuItem(REMOVE_IGNORE);
		removeIgnore.setOpaque(true);
		removeIgnore.setBackground(Color.WHITE);
		removeIgnore.addMouseListener(new Closer());
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
		List<SearchResult> selectedValuesList = list.getSelectedValuesList();
		for (SearchResult searchResult : selectedValuesList) {
			if (searchResult.getPdf().isIgnored()) {
				menu.add(removeIgnore);
				menu.remove(ignorePdf);
				menu.show(e.getComponent(), e.getX(), e.getY());
				menu.setVisible(true);
				return;
			}
		}
		menu.add(ignorePdf);
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
		public void mouseReleased(MouseEvent event) {
			JMenuItem selectedAction = (JMenuItem) event.getSource();
			menu.setVisible(false);
			if (selectedAction.getText().equals(DOWNLOAD)) {
				startDownload();
			}
			if (selectedAction.getText().equals(IGNORE)) {
				List<SearchResult> selectedValuesList = list.getSelectedValuesList();
				for (SearchResult searchResult : selectedValuesList) {
					storageProvider.storeIgnoredPdfSize(searchResult.getPdf());
				}
			}
			if (selectedAction.getText().equals(REMOVE_IGNORE)) {
				List<SearchResult> selectedValuesList = list.getSelectedValuesList();
				for (SearchResult searchResult : selectedValuesList) {
					storageProvider.removeIgnoredPdfSize(searchResult.getPdf());
				}
			}
		}
	}
}
