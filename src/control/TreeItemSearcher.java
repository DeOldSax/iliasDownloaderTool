package control;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Directory;
import model.PDF;

public class TreeItemSearcher {
	private final TreeView<Directory> courses;
	private PDF pdf;
	private TreeItem<Directory> returnValue = null;

	public TreeItemSearcher(TreeView<Directory> courses) {
		this.courses = courses;
	}

	public TreeItem<Directory> get(PDF pdf) {
		if (pdf == null) {
			return new TreeItem<Directory>();
		}
		this.pdf = pdf;
		return search(courses.getRoot().getChildren());
	}

	private TreeItem<Directory> search(ObservableList<TreeItem<Directory>> children) {
		for (TreeItem<Directory> treeItem : children) {
			if (treeItem.getValue() instanceof PDF) {
				if (treeItem.getValue().getUrl().equals(pdf.getUrl())) {
					returnValue = treeItem;
				}
			}
			search(treeItem.getChildren());
		}
		return returnValue;
	}
}
