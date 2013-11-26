package model;

import java.util.LinkedList;
import java.util.List;

public class Directory {
	private final String url;
	private final String name;
	private final Directory parentFolder;
	private final List<Directory> childFolders;

	public Directory(String name, String url, Directory parentDirectory) {
		this.name = name;
		this.url = url;
		if (parentDirectory != null) {
			parentDirectory.getChildFolders().add(this);
		}
		this.parentFolder = parentDirectory;
		this.childFolders = new LinkedList<Directory>();
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
		return name;
	}
}
