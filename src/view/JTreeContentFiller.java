package view;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Adresse;

public class JTreeContentFiller {
	public void addKurseToTree(DefaultMutableTreeNode top, List<Adresse> kurse) {
		for (Adresse adresse : kurse) {
			DefaultMutableTreeNode topsChilds = new DefaultMutableTreeNode(adresse.getName());
			top.add(topsChilds);
			for (Adresse subDir : adresse.getChildFolders()) {
				final DefaultMutableTreeNode subDirNode = new DefaultMutableTreeNode(subDir.getName());
				if (subDir.isPdf()) {
					topsChilds.add(subDirNode);
				}
				if (subDir.isFolder()) {
					addKurseToTree(subDirNode, subDir.getChildFolders());
					topsChilds.add(subDirNode);
				}
			}
		}
	}

	public void addKurseUngelesenToTree(DefaultMutableTreeNode top, List<Adresse> kurse) {
		for (Adresse adresse : kurse) {
			if (adresse.isFolder() && noFurtherSubContent(adresse)) {
				return;
			}
			DefaultMutableTreeNode topsChilds = new DefaultMutableTreeNode(adresse.getName());
			if (!adresse.isGelesen() && adresse.isPdf() || adresse.isFolder()) {
				top.add(topsChilds);
			}
			for (Adresse subDir : adresse.getChildFolders()) {
				final DefaultMutableTreeNode subDirNode = new DefaultMutableTreeNode(subDir.getName());
				if (subDir.isFolder() && noFurtherSubContent(subDir)) {
					return;
				}
				if (subDir.isPdf() && !subDir.isGelesen()) {
					topsChilds.add(subDirNode);
				}
				if (subDir.isFolder()) {
					addKurseToTree(subDirNode, subDir.getChildFolders());
					topsChilds.add(subDirNode);
				}
			}
		}
	}

	private boolean noFurtherSubContent(Adresse adresse) {
		if (adresse.getChildFolders().isEmpty()) {
			return true;
		}
		return false;
	}
}
