package model;

import java.util.List;

public class Folder extends Directory {

	public Folder(String name, String url, Directory parentDirectory) {
		super(name, url, parentDirectory);
	}

	public boolean hasUnreadOrNotOnLocalFolderSubContent() {
		// final Map<Integer, String> allLocalPDFSizesAsMap = new
		// LocalDataReader().getAllLocalPDFSizesAsMap();
		final List<Directory> childDirectory = this.getChildFolders();
		if (childDirectory == null) {
			return false;
		}
		for (Directory directory : childDirectory) {
			if (directory instanceof PDF && !((PDF) directory).isRead()) {
				return true;
			}
			// if (directory instanceof PDF &&
			// !(allLocalPDFSizesAsMap.get(((PDF) directory).getSize()) !=
			// null)) {
			// return true;
			// }
			if (directory instanceof Folder) {
				return ((Folder) directory).hasUnreadOrNotOnLocalFolderSubContent();
			}
		}
		return false;
	}
}
