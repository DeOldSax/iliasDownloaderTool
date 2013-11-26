package worker;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import model.PDF;

public class ResultListPopupMenu extends MouseAdapter {
	private static final String HERUNTERLADEN = "herunterladen";
	private final JPopupMenu menu;
	private final IliasStarter iliasStarter;
	private JList<PDF> list;

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
		list = (JList<PDF>) e.getSource();

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
			List<PDF> selectedValuesList = list.getSelectedValuesList();
			for (PDF pdf : selectedValuesList) {
				new PdfDownloader().download(findAdresse(pdf));
			}
		}
	}

	private PDF findAdresse(PDF pdf) {
		for (PDF adresse : iliasStarter.getAllPdfs()) {
			if (adresse.equals(pdf)) {
				return adresse;
			}
		}
		return null;
	}
}
