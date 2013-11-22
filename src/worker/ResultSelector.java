package worker;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ResultSelector extends KeyAdapter implements MouseListener {
	private final DefaultMutableTreeNode allePdf;
	private final JTree tree;
	private final JList<String> resultList;

	public ResultSelector(DefaultMutableTreeNode allePdf, JTree tree, JList<String> resultList) {
		this.allePdf = allePdf;
		this.tree = tree;
		this.resultList = resultList;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (resultList.getModel().getSize() == 0) {
			return;
		}
		if (SwingUtilities.isLeftMouseButton(e)) {
			openNodeInTree(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		resultList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		resultList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (resultList.getModel().getSize() == 0) {
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_UP || arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			openNodeInTree(arg0);
		}
	}

	private void openNodeInTree(InputEvent e) {
		resultList.setSelectionForeground(Color.WHITE);
		final JList model = (JList<String>) e.getSource();
		collapseAll(tree);
		@SuppressWarnings("unchecked")
		final JList<String> source = model;
		final int selectedIndex = source.getSelectedIndex();
		String name = source.getModel().getElementAt(selectedIndex);
		name = name.substring(0, name.indexOf("[") - 1);

		final TreePath path = find(allePdf, name);
		tree.scrollPathToVisible(path);
		tree.getSelectionModel().clearSelection();
		tree.getSelectionModel().setSelectionPath(path);
	}

	private TreePath find(DefaultMutableTreeNode root, String name) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(name)) {
				return new TreePath(node.getPath());
			}
		}
		return null;
	}

	public void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void collapseAll(JTree tree) {
		int row = tree.getRowCount() - 1;
		while (row >= 0) {
			tree.collapseRow(row);
			row--;
		}
	}
}
