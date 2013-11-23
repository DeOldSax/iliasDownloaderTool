package model;

import java.util.LinkedList;
import java.util.List;

public class Adresse {
	private String url = null;
	private String name = null;
	private boolean gelesen = true;
	private Adresse parentFolder = null;
	private List<Adresse> childFolders = null;
	private final boolean folder;
	private final boolean pdf;
	private final double size;

	public Adresse(String name, String url, Adresse parentFolder, boolean folder, boolean pdf, double size) {
		this.name = name;
		this.url = url;
		this.pdf = pdf;
		this.folder = folder;
		this.size = size;
		if (parentFolder != null) {
			parentFolder.getChildFolders().add(this);
		}
		this.parentFolder = parentFolder;
		this.childFolders = new LinkedList<Adresse>();
	}

	public Adresse() {
		this.size = 0.0;
		this.pdf = false;
		this.folder = false;
	}

	public double getSize() {
		return size;
	}

	public boolean isFolder() {
		return folder;
	}

	public boolean isPdf() {
		return pdf;
	}

	public boolean isGelesen() {
		return gelesen;
	}

	public void setGelesen(boolean gelesen) {
		this.gelesen = gelesen;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Adresse getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(Adresse parentFolder) {
		this.parentFolder = parentFolder;
	}

	public List<Adresse> getChildFolders() {
		return childFolders;
	}

	public void setChildFolders(List<Adresse> childFolders) {
		this.childFolders = childFolders;
	}

	@Override
	public String toString() {
		return name;
	}
}
