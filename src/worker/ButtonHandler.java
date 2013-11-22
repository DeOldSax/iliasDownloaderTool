package worker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import model.Adresse;
import model.LocalDataReader;
import view.CustomNodeRenderer;
import view.DownloaderToolWindow;
import view.LocalFolderService;

public class ButtonHandler implements MouseListener {

	private final String status;
	private final JTree tree;
	private final DefaultMutableTreeNode node;
	private final List<Adresse> allPdf;
	private final DownloaderToolWindow window;
	private final IliasStarter iliasStarter;

	public ButtonHandler(String status, JTree tree, DefaultMutableTreeNode node, IliasStarter iliasStarter, DownloaderToolWindow window) {
		this.status = status;
		this.tree = tree;
		this.node = node;
		this.iliasStarter = iliasStarter;
		this.window = window;
		this.allPdf = iliasStarter.getAllPdfs();

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (status.equals("ungelesen")) {
			expandAllUnread();
		}
		if (status.equals("nicht_vorhanden")) {
			expandAllMissing();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	private void expandAllMissing() {
		window.clearResultList();
		collapseAll(tree);
		tree.setCellRenderer(new CustomNodeRenderer(iliasStarter));
		tree.getSelectionModel().clearSelection();
		final List<Double> allLocalPdfSizes = new LocalDataReader().searchPdf(LocalFolderService.getLocalIliasPathString());
		for (Adresse adresse : allPdf) {
			if (adresse.getName().contains("or2_")) {
				new String();
			}
			if (!allLocalPdfSizes.contains(adresse.getSize())) {
				final TreePath treePath = findTreePath(node, adresse.getName());
				tree.scrollPathToVisible(treePath);
				window.addToResultList(adresse.getName(), adresse.getParentFolder());
			}
		}
	}

	private void expandAllUnread() {
		window.clearResultList();
		collapseAll(tree);
		tree.setCellRenderer(new CustomNodeRenderer(iliasStarter));
		tree.getSelectionModel().clearSelection();
		for (Adresse pdf : allPdf) {
			if (!pdf.isGelesen()) {
				final TreePath path = findTreePath(node, pdf.getName());
				tree.getSelectionModel().setSelectionPath(path);
				tree.scrollPathToVisible(path);
				window.addToResultList(pdf.getName(), pdf.getParentFolder());
			}
		}
	}

	private TreePath findTreePath(DefaultMutableTreeNode root, String name) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			String adresseName = (String) node.getUserObject();
			if (adresseName.equals(name)) {
				return new TreePath(node.getPath());
			}
		}
		return null;
	}

	public void collapseAll(JTree tree) {
		int row = tree.getRowCount() - 1;
		while (row >= 0) {
			tree.collapseRow(row);
			row--;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
