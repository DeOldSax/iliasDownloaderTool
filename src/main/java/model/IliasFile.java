package model;

import java.util.regex.*;

import javafx.scene.image.*;
import model.persistance.*;

import org.apache.log4j.*;

import utils.*;
import control.*;

public class IliasFile extends IliasTreeNode {

	private static final long serialVersionUID = -6286982393008142116L;
	private final int size;
	private final String extension;
	private final String sizeLabel;
	
	public IliasFile(String name, String url, IliasFolder parentFolder, int size, String sizeLabel,
			String extension) {
		super(name, url, parentFolder);
		this.size = size;
		this.extension = extension;
		this.sizeLabel = sizeLabel;
	}

	public int getSize() {
		return size;
	}

	public boolean isIgnored() {
		return Settings.getInstance().getFileStates().isIgnored(createStoreKey()) != -1;
	}

	public void setIgnored(boolean b) {
		if (b) {
			Settings.getInstance().getFileStates()
					.storeIgnoredFileSize(createStoreKey(), getSize());
		} else {
			Settings.getInstance().getFileStates().removeIgnoredFileSize(createStoreKey());
		}
	}

	private String createStoreKey() {
		String url = super.getUrl();
		Matcher matcher = Pattern.compile("(.*?)(\\d+)(.*)").matcher(url);
		String digits = "";

		if (matcher.find()) {
			digits = matcher.group(2);
		} else {
			Logger.getLogger(getClass()).warn("StoreKey digits not found!");
		}

		return digits;
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
	 * Returns the files {@link #extension}. e. g. "pdf" or "txt" <br>
	 * <b>NOT</b> .pdf !
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
