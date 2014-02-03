package model;

import java.util.ArrayList;
import java.util.List;

public class IliasFolder extends IliasTreeNode {
	private static final long serialVersionUID = 6134132849917192741L;
	private final List<IliasTreeNode> childFolders;

	public IliasFolder(String name, String url, IliasFolder parentDirectory) {
		super(name, url, parentDirectory);
		this.childFolders = new ArrayList<IliasTreeNode>();
	}

	public List<IliasTreeNode> getChildNodes() {
		return childFolders;
	}
}
