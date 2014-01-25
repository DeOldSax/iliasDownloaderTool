package control;

import java.util.List;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import model.Directory;
import model.Folder;
import model.Forum;
import model.PDF;

public class JTreeContentFiller {
	private List<Integer> localPdfSizes = null;

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
				checkIfContainsPdfNotLocal(rootItem, directory);
			}
			for (Directory subDir : directory.getChildFolders()) {
				final TreeItem<Directory> subDirNode = new TreeItem<Directory>(subDir);

				if (subDir instanceof PDF && !((PDF) subDir).isIgnored()) {
					subDirNode.setGraphic(new ImageView("view/pdf.png"));
					topsChilds.getChildren().add(subDirNode);
					checkIfContainsPdfNotLocal(topsChilds, subDir);
				}
				if (subDir instanceof Forum) {
					subDirNode.setGraphic(new ImageView("view/forum.png"));
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

	private void checkIfContainsPdfNotLocal(TreeItem<Directory> rootItem, Directory directory) {
		if (localPdfSizes == null) {
			localPdfSizes = new LocalDataReader().getAllLocalPDFSizes();
		}
		if (!(localPdfSizes.contains(((PDF) directory).getSize()))) {
			rootItem.setGraphic(new ImageView("img/folder_pdf_not_there.png"));
		}
	}
}
