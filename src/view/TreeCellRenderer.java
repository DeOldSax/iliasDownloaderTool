package view;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.Folder;
import model.Forum;
import model.PDF;
import worker.IliasStarter;
import worker.LocalDataReader;

public class TreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Icon normalPdfIcon;
	private final Icon unreadPdfIcon;
	private final Icon notInLocalFolderPdfIcon;
	private final Icon folderIcon;
	private final Icon folderIconUnread;
	private final Icon forumIcon;
	private final List<Integer> localPdfSizes;
	private final List<PDF> allPdfs;

	public TreeCellRenderer(IliasStarter iliasStarter) {
		allPdfs = iliasStarter.getAllPdfs();
		localPdfSizes = new LocalDataReader().getAllLocalPDFSizes();
		normalPdfIcon = new ImageIcon(TreeCellRenderer.class.getResource("pdf_icon.png"));
		unreadPdfIcon = new ImageIcon(TreeCellRenderer.class.getResource("pdf_icon_unread.png"));
		notInLocalFolderPdfIcon = new ImageIcon(TreeCellRenderer.class.getResource("pdf_icon_notLocal.png"));
		folderIcon = new ImageIcon(TreeCellRenderer.class.getResource("folder_icon.jpg"));
		folderIconUnread = new ImageIcon(TreeCellRenderer.class.getResource("folder_icon_unread.jpg"));
		forumIcon = new ImageIcon(TreeCellRenderer.class.getResource("forum_icon.gif"));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

		if (node.getUserObject() instanceof PDF) {
			setIcon(normalPdfIcon);
		}

		if (adresseIsUnreadPdf(node)) {
			setIcon(unreadPdfIcon);
		}

		if (adresseIsNotOnLocalFolder(node)) {
			setIcon(notInLocalFolderPdfIcon);
		}

		if (node.getUserObject() instanceof Forum) {
			setIcon(forumIcon);
		}

		if (node.getUserObject() instanceof Folder) {
			Folder folder = (Folder) node.getUserObject();
			if (folder.hasUnreadOrNotOnLocalFolderSubContent()) {
				setIcon(folderIconUnread);
			} else {
				setIcon(folderIcon);
			}
		}
		return this;
	}

	private boolean adresseIsNotOnLocalFolder(DefaultMutableTreeNode node) {
		if (!(node.getUserObject() instanceof PDF)) {
			return false;
		}
		PDF pdf = (PDF) node.getUserObject();
		for (PDF adresse : allPdfs) {
			if (adresse.equals(pdf)) {
				return !localPdfSizes.contains(adresse.getSize());
			}
		}
		return false;
	}

	private boolean adresseIsUnreadPdf(DefaultMutableTreeNode node) {
		if (!(node.getUserObject() instanceof PDF)) {
			return false;
		}
		return !((PDF) node.getUserObject()).isRead();
	}
}
