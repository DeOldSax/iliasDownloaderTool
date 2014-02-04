package model;

import java.util.ArrayList;
import java.util.List;

public class IliasTreeProvider {
	private static List<IliasFolder> allFiles = null;

	public static List<IliasPdf> getAllPdfFiles() {
		List<IliasPdf> allPdfs = new ArrayList<IliasPdf>();
		pdfFilter(getTree(), allPdfs);
		return allPdfs;
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

	private static void pdfFilter(final List<? extends IliasTreeNode> nodes, final List<IliasPdf> allPdfs) {
		for (IliasTreeNode node : nodes) {
			if (node instanceof IliasPdf) {
				allPdfs.add((IliasPdf) node);
			} else if (node instanceof IliasFolder) {
				pdfFilter(((IliasFolder) node).getChildNodes(), allPdfs);
			}
		}
	}
}
