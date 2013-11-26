package model;

public class PDF extends Directory {
	private final int size;
	private boolean read = true;

	public PDF(String name, String url, Directory parentDirectory, int size) {
		super(name, url, parentDirectory);
		this.size = size;
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

}
