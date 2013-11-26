package model;

import java.util.List;

public class Folder extends Directory {
	private final boolean hasUnreadSubContent = true;

	public Folder(String name, String url, Directory parentDirectory) {
		super(name, url, parentDirectory);
	}

	public boolean hasUnreadSubContent() {
		// TODO unread Subcontent?
		final List<Directory> childDirectory = this.getChildFolders();
		if (childDirectory != null) {

		}
		return hasUnreadSubContent;
	}
}
