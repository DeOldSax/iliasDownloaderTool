package view;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.Directory;
import model.Folder;
import model.Forum;
import model.LocalDataReader;
import model.PDF;
import worker.IliasStarter;

public class CustomNodeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Icon normalPdfIcon;
	private final Icon unreadPdfIcon;
	private final Icon notInLocalFolderPdfIcon;
	private final Icon folderIcon;
	private final Icon folderIconUnread;
	private final Icon forumIcon;
	private final List<Integer> localPdfSizes;
	private final List<PDF> allPdfs;
	private final List<Directory> allFolders;

	private final List<Directory> allKurse;

	public CustomNodeRenderer(IliasStarter iliasStarter) {
		allPdfs = iliasStarter.getAllPdfs();
		localPdfSizes = new LocalDataReader().searchPdf(LocalFolderService.getLocalIliasPathString());
		allFolders = iliasStarter.getAllFolder();
		allKurse = iliasStarter.getKurse();
		normalPdfIcon = new ImageIcon(CustomNodeRenderer.class.getResource("pdf_icon.png"));
		unreadPdfIcon = new ImageIcon(CustomNodeRenderer.class.getResource("pdf_icon_unread.png"));
		notInLocalFolderPdfIcon = new ImageIcon(CustomNodeRenderer.class.getResource("pdf_icon_notLocal.png"));
		folderIcon = new ImageIcon(CustomNodeRenderer.class.getResource("folder_icon.jpg"));
		folderIconUnread = new ImageIcon(CustomNodeRenderer.class.getResource("folder_icon_unread.jpg"));
		forumIcon = new ImageIcon(CustomNodeRenderer.class.getResource("forum_icon.gif"));
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
			setIcon(folderIcon);
		}

		// if (!leaf || adresseIsFolder(value)) {
		// // if (folderContainsUnreadSubContent(value)) {
		// // setIcon(folderIconUnread);
		// // break;
		// // }
		//
		// }
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

	private boolean folderContainsUnreadSubContent(Object value) {
		if (!(value instanceof Folder)) {
			return false;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Folder folder = (Folder) node.getUserObject();
		for (PDF pdf : allPdfs) {
			if (pdf.equals(folder)) {

			}
		}
		return false;
	}

	private boolean adresseIsFolder(Object value) {
		return value instanceof Folder;
		// DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		// Adresse dir = (Adresse) node.getUserObject();
		// for (Adresse adresse : allFolders) {
		// if (adresse.equals(dir)) {
		// return adresse.isFolder();
		// }
		// }
		// // for (Adresse adresse : allKurse) {
		// // if (adresse.getName().equals(name)) {
		// // return true;
		// // }
		// // }
		// return false;
	}
}
