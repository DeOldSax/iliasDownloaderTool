package worker;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import model.Adresse;

public class PdfNodePopupMenu extends MouseAdapter {
	private static final String HERUNTERLADEN = "herunterladen";
	private final JPopupMenu menu;
	private final IliasStarter iliasStarter;
	private TreePath[] selectionPaths;

	public PdfNodePopupMenu(IliasStarter iliasStarter) {
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
		if (SwingUtilities.isRightMouseButton(e)) {
			showPopMenu(e);
		}
	}

	/**
	 * @param e
	 * @wbp.parser.entryPoint
	 */
	private void showPopMenu(MouseEvent e) {
		JTree selectedNode = (JTree) e.getSource();
		selectionPaths = selectedNode.getSelectionPaths();

		menu.show(e.getComponent(), e.getX(), e.getY());
		menu.setVisible(true);
	}

	private class Closer extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			menu.setVisible(false);
			JMenuItem selectedAction = (JMenuItem) e.getSource();
			if (selectedAction.getText().equals(HERUNTERLADEN)) {
				for (TreePath path : selectionPaths) {
					final String filename = path.getLastPathComponent().toString();
					new PdfDownloader().download(findAdresse(filename));
				}
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
