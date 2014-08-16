package model;

import javafx.scene.image.ImageView;
import model.persistance.NewSettings;
import utils.FileAppearanceManager;
import control.LocalFileStorage;

public class IliasFile extends IliasTreeNode {

	private static final long serialVersionUID = -6286982393008142116L;
	private final int size;
	private final String extension; 
	private final String sizeLabel; 
	
	public IliasFile(String name, String url, IliasFolder parentFolder, int size, String sizeLabel, String extension) {
		super(name, url, parentFolder);
		this.size = size;
		this.extension = extension; 
		this.sizeLabel = sizeLabel; 
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isIgnored() {
		return NewSettings.getInstance().getFileStates().isIgnored(createStoreKey()) != -1;
	}
	
	public void setIgnored(boolean b) {
		if (b) {
			NewSettings.getInstance().getFileStates().storeIgnoredFileSize(createStoreKey(), getSize());
		} else {
			NewSettings.getInstance().getFileStates().removeIgnoredFileSize(createStoreKey());
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
			return appearanceManager.getIgnoredPicture(getExtension());
		} else if (!(LocalFileStorage.getInstance().contains(this))) {
			return appearanceManager.getNotSynchronizedPicture(getExtension());
		} else {
			return appearanceManager.getNormalPicture(getExtension()); 
		}
	}

	/**
	 * Returns the files {@link #extension}.
	 * e. g. "pdf" or "txt" 
	 * <br><b>NOT</b> .pdf !
	 * 
	 * @return {@link #extension}
	 */
	public String getExtension() {
		return extension; 
	}
	
	public String getSizeLabel() {
		return sizeLabel; 
	}
}
