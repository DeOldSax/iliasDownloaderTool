package model;

import java.io.File;

public class PDF extends Directory {
	private static final long serialVersionUID = -2841996600829969452L;
	private final int size;
	private File fileOnLocalDisk = null;
	private boolean localNotThere = true;

	public PDF(String name, String url, Directory parentDirectory, int size) {
		super(name, url, parentDirectory);
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public boolean isIgnored() {
		return Settings.getInstance().isIgnored(createStoreKey()) != -1;
	}

	public void setIgnored(boolean b) {
		if (b) {
			Settings.getInstance().storeIgnoredPdfSize(createStoreKey(), getSize());
		} else {
			Settings.getInstance().removeIgnoredPdfSize(createStoreKey());
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
		this.localNotThere = b;
		Settings.getInstance().storeLocalNotThere(createStoreKey(), b);
		((Folder) this.getParentDirectory()).setContainsPdfsLocalNothThere(b);
	}

	public boolean isLocalNotThere() {
		final boolean loadLocalNotThere = Settings.getInstance().loadLocalNotThere(createStoreKey());
		localNotThere = loadLocalNotThere;
		return loadLocalNotThere;
	}

	public void setRead(boolean b) {

	}

	private String createStoreKey() {
		String key = getUrl();
		final int beginIndex = key.indexOf("ref_id=");
		final int endIndex = key.indexOf("&cmd=sendfile");
		key = key.substring(beginIndex + 7, endIndex);
		return key;
	}
}
