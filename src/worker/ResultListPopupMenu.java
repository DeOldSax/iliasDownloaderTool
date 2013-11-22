package worker;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import model.Adresse;

public class ResultListPopupMenu extends MouseAdapter {
	private static final String HERUNTERLADEN = "herunterladen";
	private final JPopupMenu menu;
	private final IliasStarter iliasStarter;
	private JList<String> list;

	public ResultListPopupMenu(IliasStarter iliasStarter) {
		this.iliasStarter = iliasStarter;
		this.menu = new JPopupMenu();

		final JMenuItem downloadMenuItem = new JMenuItem(HERUNTERLADEN);
		downloadMenuItem.setOpaque(true);
		downloadMenuItem.setBackground(Color.WHITE);
		downloadMenuItem.addMouseListener(new Closer());
		menu.add(downloadMenuItem);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		list = (JList<String>) e.getSource();

		if (SwingUtilities.isRightMouseButton(e) && !(list.getSelectedValuesList().size() == 0)) {
			showPopMenu(e);
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

	private class Closer extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			menu.setVisible(false);
			List<String> selectedValuesList = list.getSelectedValuesList();
			for (String name : selectedValuesList) {
				final int indexOf = name.indexOf("[");
				String filename = name.substring(0, indexOf - 1);
				new PdfDownloader().download(findAdresse(filename));
			}
		}
	}

	private Adresse findAdresse(String filename) {
		for (Adresse adresse : iliasStarter.getAllPdfs()) {
			if (adresse.getName().equals(filename)) {
				return adresse;
			}
		}
		return null;
	}
}
