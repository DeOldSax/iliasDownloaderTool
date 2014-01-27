package control;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import model.Directory;
import model.Folder;
import model.Forum;
import model.PDF;

public class TreeViewContentFiller {
	private List<Integer> localPdfSizes = null;

	public void addKurseToTree(TreeItem<Directory> rootItem, List<Directory> kurse) {
		for (Directory directory : kurse) {
			TreeItem<Directory> topsChilds = new TreeItem<Directory>(directory);
			if (directory.getParentDirectory() == null || directory instanceof Folder) {
				topsChilds.setGraphic(new ImageView("view/folder.png"));
				rootItem.getChildren().add(topsChilds);
			}
			if (directory instanceof PDF) {
				topsChilds.setGraphic(new ImageView("img/pdf.png"));
				rootItem.getChildren().add(topsChilds);
				checkIfContainsPdfNotLocal(rootItem, directory, topsChilds);
			}
			for (final Directory subDir : directory.getChildFolders()) {
				final TreeItem<Directory> subDirNode = new TreeItem<Directory>(subDir);

				if (subDir instanceof PDF) {
					subDirNode.setGraphic(new ImageView("img/pdf.png"));
					topsChilds.getChildren().add(subDirNode);
					checkIfContainsPdfNotLocal(topsChilds, subDir, subDirNode);
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

	private void checkIfContainsPdfNotLocal(TreeItem<Directory> rootItem, Directory directory, TreeItem<Directory> item) {
		if (localPdfSizes == null) {
			localPdfSizes = new LocalDataReader().getAllLocalPDFSizes();
		}
		if (((PDF) directory).isIgnored()) {
			item.setGraphic(new ImageView("img/pdf_ignored.png"));
			return;
		}
		if (!(localPdfSizes.contains(((PDF) directory).getSize()))) {
			rootItem.setGraphic(new ImageView("img/folder_pdf_not_there.png"));
			item.setGraphic(new ImageView("img/pdf_local_not_there.png"));
			((PDF) item.getValue()).setLocalNotThere(true);
		}
	}

	public void markCourses(TreeItem<Directory> rootItem) {
		mark(rootItem.getChildren());
	}

	private void mark(ObservableList<TreeItem<Directory>> children) {
		for (TreeItem<Directory> item : children) {
			if (item.getValue() instanceof Folder) {
				if (((Folder) item.getValue()).containsPdfsLocalNotThere()) {
					item.setGraphic(new ImageView("img/folder_pdf_not_there.png"));
					mark(item.getChildren());
				}
			}
		}
	}
}
