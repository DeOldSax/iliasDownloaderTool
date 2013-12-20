package worker;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import model.Forum;
import model.PDF;
import model.StorageProvider;

public class PdfNodePopupMenu extends MouseAdapter {
	private static final String HERUNTERLADEN = "herunterladen";
	private static final String OEFFNEN = "öffnen";
	private static final String IGNORE = "ignorieren";
	private static final String REMOVE_IGNORE = "ignorieren aufheben";
	private JPopupMenu menu;
	private TreePath[] selectionPaths;
	private final JMenuItem downloadMenuItem;
	private final JMenuItem openForumMenuItem;
	private final JMenuItem ignorePdf;
	private final StorageProvider storageProvider;
	private final JMenuItem removeIgnore;

	public PdfNodePopupMenu() {
		storageProvider = new StorageProvider();
		this.menu = new JPopupMenu();
		downloadMenuItem = new JMenuItem(HERUNTERLADEN);
		downloadMenuItem.setOpaque(true);
		downloadMenuItem.setBackground(Color.WHITE);
		downloadMenuItem.addMouseListener(new Closer());
		openForumMenuItem = new JMenuItem(OEFFNEN);
		openForumMenuItem.setOpaque(true);
		openForumMenuItem.setBackground(Color.WHITE);
		openForumMenuItem.addMouseListener(new Closer());
		ignorePdf = new JMenuItem(IGNORE);
		ignorePdf.setOpaque(true);
		ignorePdf.setBackground(Color.WHITE);
		ignorePdf.addMouseListener(new Closer());
		removeIgnore = new JMenuItem(REMOVE_IGNORE);
		removeIgnore.setOpaque(true);
		removeIgnore.setBackground(Color.WHITE);
		removeIgnore.addMouseListener(new Closer());

	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			JTree tree = (JTree) event.getSource();
			int row = tree.getClosestRowForLocation(event.getX(), event.getY());
			tree.setSelectionRow(row);
			showPopMenu(event);
		}
	}

	/**
	 * @param e
	 * @wbp.parser.entryPoint
	 */
	private void showPopMenu(MouseEvent e) {
		this.menu = new JPopupMenu();
		JTree tree = (JTree) e.getSource();
		selectionPaths = tree.getSelectionPaths();
		for (TreePath path : selectionPaths) {
			final Object selectedNode = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			if (selectedNode instanceof Forum) {
				menu.add(openForumMenuItem);
			}
			if (selectedNode instanceof PDF) {
				menu.add(downloadMenuItem);
				if (((PDF) selectedNode).isIgnored()) {
					menu.add(removeIgnore);
				} else {
					menu.add(ignorePdf);
				}
			}
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
		menu.setVisible(true);
	}

	private class Closer extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent event) {
			menu.setVisible(false);
			JMenuItem selectedAction = (JMenuItem) event.getSource();
			if (selectedAction.getText().equals(HERUNTERLADEN)) {
				for (TreePath path : selectionPaths) {
					final PDF pdf = (PDF) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					new PdfDownloader().download(pdf);
				}
			}
			if (selectedAction.getText().equals(OEFFNEN)) {
				for (TreePath path : selectionPaths) {
					final Forum forum = (Forum) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					try {
						Desktop.getDesktop().browse(new URI(forum.getUrl()));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
			if (selectedAction.getText().equals(IGNORE)) {
				for (TreePath path : selectionPaths) {
					final PDF pdf = (PDF) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					storageProvider.storeIgnoredPdfSize(pdf);
				}
			}
			if (selectedAction.getText().equals(REMOVE_IGNORE)) {
				for (TreePath path : selectionPaths) {
					final PDF pdf = (PDF) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					storageProvider.removeIgnoredPdfSize(pdf);
				}
			}
		}
	}
}
