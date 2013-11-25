package view;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.Adresse;
import model.LocalDataReader;
import worker.IliasStarter;

public class CustomNodeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Icon normalPdfIcon;
	private final Icon unreadPdfIcon;
	private final Icon notInLocalFolderPdfIcon;
	private final Icon folderIcon;
	private final Icon folderIconUnread;
	private final List<Integer> localPdfSizes;
	private final List<Adresse> allPdfs;
	private final List<Adresse> allFolders;

	private final List<Adresse> allKurse;

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
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (leaf) {
			setIcon(normalPdfIcon);
		}
		if (adresseIsUnreadPdf(value)) {
			setIcon(unreadPdfIcon);
		}
		if (adresseIsNotOnLocalFolder(value)) {
			setIcon(notInLocalFolderPdfIcon);
		}
		if (!leaf || adresseIsFolder(value)) {
			// if (folderContainsUnreadSubContent(value)) {
			// setIcon(folderIconUnread);
			// break;
			// }
			setIcon(folderIcon);

		}
		return this;
	}

	private boolean adresseIsNotOnLocalFolder(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Adresse pdf = (Adresse) node.getUserObject();
		for (Adresse adresse : allPdfs) {
			if (adresse.equals(pdf)) {
				return !localPdfSizes.contains(adresse.getSize());
			}
		}
		return false;
	}

	private boolean adresseIsUnreadPdf(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Adresse pdf = (Adresse) node.getUserObject();
		for (Adresse adresse : allPdfs) {
			if (adresse.equals(pdf)) {
				return !adresse.isGelesen();
			}
		}
		return false;
	}

	private boolean folderContainsUnreadSubContent(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Adresse pdf = (Adresse) node.getUserObject();
		for (Adresse adresse : allPdfs) {
			if (adresse.equals(pdf)) {

			}
		}
		return false;
	}

	private boolean adresseIsFolder(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Adresse dir = (Adresse) node.getUserObject();
		for (Adresse adresse : allFolders) {
			if (adresse.equals(dir)) {
				return adresse.isFolder();
			}
		}
		// for (Adresse adresse : allKurse) {
		// if (adresse.getName().equals(name)) {
		// return true;
		// }
		// }
		return false;
	}
}
