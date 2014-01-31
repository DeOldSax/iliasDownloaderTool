package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Directory implements Serializable {
	private static final long serialVersionUID = -5666402232004312659L;
	private final String url;
	private final String name;
	private String nameChangedByUser;
	private final Directory parentFolder;
	private final List<Directory> childFolders;

	public Directory(String name, String url, Directory parentDirectory) {
		this.name = name;
		this.url = url;
		if (parentDirectory != null) {
			parentDirectory.getChildFolders().add(this);
		}
		this.parentFolder = parentDirectory;
		this.childFolders = new ArrayList<Directory>();
		this.nameChangedByUser = null;
	}

	public List<Directory> getChildFolders() {
		return childFolders;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public void setNameChangedByUser(String nameChangedByUser) {
		this.nameChangedByUser = nameChangedByUser;
	}

	public Directory getParentDirectory() {
		return parentFolder;
	}

	public Directory getRootCourse() {
		return getRoot(this);
	}

	private Directory getRoot(Directory directory) {
		if (directory.getParentDirectory() == null) {
			return directory;
		}
		return getRoot(directory.getParentDirectory());
	}

	@Override
	public String toString() {
		if (nameChangedByUser != null) {
			return nameChangedByUser;
		}
		return name;
	}
}
