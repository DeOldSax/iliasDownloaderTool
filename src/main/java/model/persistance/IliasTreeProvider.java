package model.persistance;

import java.util.ArrayList;
import java.util.List;

import model.IliasFile;
import model.IliasFolder;
import model.IliasTreeNode;

public class IliasTreeProvider {
	private static List<IliasFolder> allFiles = null;

	public static List<IliasFile> getAllIliasFiles() {
		List<IliasFile> allFiles = new ArrayList<IliasFile>();
		pdfFilter(getTree(), allFiles);
		return allFiles;
	}

	public static List<IliasFolder> getTree() {
		if (allFiles == null) {
			allFiles = IliasTreeStorage.getTree();
			if (allFiles == null) {
				allFiles = new ArrayList<IliasFolder>();
			}
		}
		return allFiles;
	}

	public static void setTree(List<IliasFolder> allFiles) {
		IliasTreeStorage.storeAllFiles(allFiles);
		IliasTreeProvider.allFiles = allFiles;
	}

	private static void pdfFilter(final List<? extends IliasTreeNode> nodes, final List<IliasFile> allIliasFiles) {
		for (IliasTreeNode node : nodes) {
			if (node instanceof IliasFile) {
				allIliasFiles.add((IliasFile) node);
			} else if (node instanceof IliasFolder) {
				pdfFilter(((IliasFolder) node).getChildNodes(), allIliasFiles);
			}
		}
	}
}
