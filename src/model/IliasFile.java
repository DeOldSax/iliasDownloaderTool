package model;


public class IliasFile extends IliasTreeNode {
	private static final long serialVersionUID = -6286982393008142116L;
	
	private final int size;
	
	public IliasFile(String name, String url, IliasFolder parentFolder, int size) {
		super(name, url, parentFolder);
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
			Settings.getInstance().storeIgnoredFileSize(createStoreKey(), getSize());
		} else {
			Settings.getInstance().removeIgnoredFileSize(createStoreKey());
		}
	}
	
	private String createStoreKey() {
		String key = getUrl();
		final int beginIndex = key.indexOf("ref_id=");
		final int endIndex = key.indexOf("&cmd=sendfile");
		key = key.substring(beginIndex + 7, endIndex);
		return key;
	}
}
