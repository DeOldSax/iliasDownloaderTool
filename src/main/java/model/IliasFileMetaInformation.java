package model;

public class IliasFileMetaInformation {

	private String sizeLabel;
	private String fileExtension;

	public IliasFileMetaInformation(String sizeLabel, String fileExtension) {
		this.sizeLabel = sizeLabel;
		this.fileExtension = fileExtension;
	}

	public String getSizeLabel() {
		return sizeLabel;
	}

	public String getFileExtension() {
		return fileExtension;
	}

}
