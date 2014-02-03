package model;


public class IliasPdf extends IliasTreeNode {
	private static final long serialVersionUID = -2841996600829969452L;
	private final int size;

	public IliasPdf(String name, String url, IliasFolder parentDirectory, int size) {
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
