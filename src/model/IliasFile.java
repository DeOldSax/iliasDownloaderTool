package model;

import utils.FileAppearanceManager;
import control.LocalFileStorage;
import javafx.scene.image.ImageView;

public class IliasFile extends IliasTreeNode {
	private static final long serialVersionUID = -6286982393008142116L;
	
	private final int size;
	private String extension; 
	
	public IliasFile(String name, String url, String extension, IliasFolder parentFolder, int size) {
		super(name, url, parentFolder);
		this.extension = extension;
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

	@Override
	public final ImageView getGraphic() {
		FileAppearanceManager appearanceManager = FileAppearanceManager.getInstance();
		if (isIgnored()) {
			return appearanceManager.getIgnoredPicture(extension);
		} else if (!(LocalFileStorage.getInstance().contains(this))) {
			return appearanceManager.getNotSynchronizedPicture(extension);
		} else {
			return appearanceManager.getNormalPicture(extension); 
		}
	}

	/**
	 * Returns the files' {@link #extension}.
	 * e. g. "pdf" or "txt" 
	 * <br><b>NOT<b> .pdf !
	 * 
	 * @return {@link #extension}
	 */
	public String getExtension() {
		return extension; 
	}
}
