package view;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Directory;
import model.Folder;
import model.Forum;
import model.PDF;

public class JTreeContentFiller {
	public void addKurseToTree(DefaultMutableTreeNode top, List<Directory> kurse) {
		for (Directory directory : kurse) {
			DefaultMutableTreeNode topsChilds = new DefaultMutableTreeNode(directory);
			top.add(topsChilds);
			for (Directory subDir : directory.getChildFolders()) {
				final DefaultMutableTreeNode subDirNode = new DefaultMutableTreeNode(subDir);
				if (subDir instanceof PDF || subDir instanceof Forum) {
					topsChilds.add(subDirNode);
				}
				if (subDir instanceof Folder) {
					addKurseToTree(subDirNode, subDir.getChildFolders());
					topsChilds.add(subDirNode);
				}
			}
		}
	}

	public void addKurseUngelesenToTree(DefaultMutableTreeNode top, List<Directory> kurse) {
		for (Directory adresse : kurse) {
			if ((adresse instanceof Folder || adresse instanceof Forum) && noFurtherSubContent(adresse)) {
				return;
			}
			DefaultMutableTreeNode topsChilds = new DefaultMutableTreeNode(adresse);
			if (!((PDF) adresse).isRead() && adresse instanceof PDF || adresse instanceof Folder || adresse instanceof Forum) {
				top.add(topsChilds);
			}
			for (Directory subDir : adresse.getChildFolders()) {
				final DefaultMutableTreeNode subDirNode = new DefaultMutableTreeNode(subDir);
				if (subDir instanceof Folder || subDir instanceof Forum && noFurtherSubContent(subDir)) {
					return;
				}
				if (subDir instanceof PDF && !((PDF) subDir).isRead()) {
					topsChilds.add(subDirNode);
				}
				if (subDir instanceof Folder || subDir instanceof Forum) {
					addKurseToTree(subDirNode, subDir.getChildFolders());
					topsChilds.add(subDirNode);
				}
			}
		}
	}

	private boolean noFurtherSubContent(Directory directory) {
		if (directory.getChildFolders().isEmpty()) {
			return true;
		}
		return false;
	}
}
