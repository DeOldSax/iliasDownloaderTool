package model;

import java.io.File;

public class PDF extends Directory {
	private final int size;
	private final StorageProvider storageProvider;
	private File fileOnLocalDisk = null;
	private boolean lnt;

	public PDF(String name, String url, Directory parentDirectory, int size) {
		super(name, url, parentDirectory);
		this.size = size;
		storageProvider = new StorageProvider();
	}

	public int getSize() {
		return size;
	}

	public boolean isIgnored() {
		if (storageProvider.isIgnored(this) != -1) {
			return true;
		}
		return false;
	}

	public void setIgnored(boolean b) {
		if (b) {
			storageProvider.storeIgnoredPdfSize(this);
		} else {
			storageProvider.removeIgnoredPdfSize(this);
		}
	}

	public void setFileOnLocalDisk(File fileOnLocalDisk) {
		this.fileOnLocalDisk = fileOnLocalDisk;
	}

	public File getFileOnLocalDisk() {
		return fileOnLocalDisk;
	}

	public File getParentFolderOnLocalDisk() {
		return fileOnLocalDisk.getParentFile();
	}

	public void setLocalNotThere(boolean b) {
		this.lnt = b;
		((Folder) this.getParentDirectory()).setContainsPdfsLocalNothThere(true);
	}

	public boolean isLocalNotThere() {
		return lnt;
	}

	public void setRead(boolean b) {

	}
}
