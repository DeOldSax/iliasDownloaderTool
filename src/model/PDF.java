package model;

public class PDF extends Directory {
	private final int size;
	private boolean read = true;
	private final StorageProvider storageProvider;

	public PDF(String name, String url, Directory parentDirectory, int size) {
		super(name, url, parentDirectory);
		this.size = size;
		storageProvider = new StorageProvider();
	}

	public int getSize() {
		return size;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
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
}
