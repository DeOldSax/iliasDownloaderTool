package control;

import java.util.List;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import model.Directory;
import model.Folder;
import model.Forum;
import model.PDF;

public class JTreeContentFiller {
	public void addKurseToTree(TreeItem<Directory> rootItem, List<Directory> kurse) {
		for (Directory directory : kurse) {
			TreeItem<Directory> topsChilds = new TreeItem<Directory>(directory);
			if (directory.getParentDirectory() == null || directory instanceof Folder) {
				topsChilds.setGraphic(new ImageView("view/folder.png"));
				rootItem.getChildren().add(topsChilds);
			}
			if (directory instanceof PDF && !((PDF) directory).isIgnored()) {
				topsChilds.setGraphic(new ImageView("view/pdf.png"));
				rootItem.getChildren().add(topsChilds);
			}
			for (Directory subDir : directory.getChildFolders()) {
				final TreeItem<Directory> subDirNode = new TreeItem<Directory>(subDir);

				if (subDir instanceof PDF && !((PDF) subDir).isIgnored()) {
					subDirNode.setGraphic(new ImageView("view/pdf.png"));
					topsChilds.getChildren().add(subDirNode);
				}
				if (subDir instanceof Forum) {
					topsChilds.getChildren().add(subDirNode);
				}
				if (subDir instanceof Folder) {
					subDirNode.setGraphic(new ImageView("view/folder.png"));
					addKurseToTree(subDirNode, subDir.getChildFolders());
					topsChilds.getChildren().add(subDirNode);
				}
			}
		}
	}
}
