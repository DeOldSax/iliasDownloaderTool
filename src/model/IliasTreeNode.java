package model;

import java.io.Serializable;

import javafx.scene.image.ImageView;

public abstract class IliasTreeNode implements Serializable {
	private static final long serialVersionUID = -5666402232004312659L;
	private final String url;
	private final String name;
	private String nameChangedByUser;
	private final IliasFolder parentFolder;

	public IliasTreeNode(String name, String url, IliasFolder parentFolder) {
		this.name = name;
		this.url = url;
		if (parentFolder != null) {
			parentFolder.getChildNodes().add(this);
		}
		this.parentFolder = parentFolder;
		this.nameChangedByUser = null;
	}
	
	public abstract ImageView getGraphic(); 

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public void setNameChangedByUser(String nameChangedByUser) {
		this.nameChangedByUser = nameChangedByUser;
	}

	public IliasFolder getParentFolder() {
		return parentFolder;
	}

	public IliasTreeNode getRootCourse() {
		return getRoot(this);
	}

	private IliasTreeNode getRoot(IliasTreeNode directory) {
		if (directory.getParentFolder() == null) {
			return directory;
		}
		return getRoot(directory.getParentFolder());
	}

	@Override
	public String toString() {
		if (nameChangedByUser != null) {
			return nameChangedByUser;
		}
		return name;
	}
}
