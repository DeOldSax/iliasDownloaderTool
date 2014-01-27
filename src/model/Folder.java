package model;

public class Folder extends Directory {
	private boolean containsPdfsLocalNotThere;

	public Folder(String name, String url, Directory parentDirectory) {
		super(name, url, parentDirectory);
	}

	public void setContainsPdfsLocalNothThere(boolean b) {
		this.containsPdfsLocalNotThere = true;
		if (this.getParentDirectory() != null) {
			((Folder) this.getParentDirectory()).setContainsPdfsLocalNothThere(true);
		}
	}

	public boolean containsPdfsLocalNotThere() {
		return containsPdfsLocalNotThere;
	}
}
