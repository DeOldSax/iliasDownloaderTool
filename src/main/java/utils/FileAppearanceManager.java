package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javafx.scene.image.ImageView;

import org.apache.log4j.Logger;

public class FileAppearanceManager {
	
	private static FileAppearanceManager instance; 
	private final String PROPERTIES_FILE_PATH = "fileAppearance.properties";
	private final String NORMAL_DESCRIPTOR = "normal"; 
	private final String IGNORED_DESCRIPTOR = "ignored"; 
	private final String NOTSYNCHRONIZED_DESCRIPTOR = "notSynchronized"; 
	private final String DEFAULT = "default"; 
	private List<String> fileExtensions; 
	private Map<String, String> normalPictures; 
	private Map<String, String> notSynchronizedPictures; 
	private Map<String, String> ignoredPictures; 
	
	private FileAppearanceManager() {
		fileExtensions = new ArrayList<String>(); 
		normalPictures = new HashMap<String, String>(); 
		notSynchronizedPictures = new HashMap<String, String>(); 
		ignoredPictures = new HashMap<String, String>(); 
		
		try {
			readPropertyFile();
		} catch (IOException e) {
			Logger.getLogger(getClass()).debug("Error: FileAppearanceConfiguration file not found.");
		} 
	}
	
	public ImageView getNormalPicture(String extension) {
		return getPicture(normalPictures, extension); 
	}
	
	public ImageView getNotSynchronizedPicture(String extension) {
		return getPicture(notSynchronizedPictures, extension); 
	}
	
	public ImageView getIgnoredPicture(String extension) {
		return getPicture(ignoredPictures, extension); 
	}

	private void readPropertyFile() throws FileNotFoundException, IOException {
		Properties p = new Properties(); 
		p.load(FileAppearanceManager.class.getResourceAsStream(PROPERTIES_FILE_PATH));
		for (int i = 0; true; i++) {
			String extension = p.getProperty(String.valueOf(i)); 
			if (extension != null) {
				fileExtensions.add(extension); 
			} else {
				break; 
			}
		}

		for (int i = 0; i < fileExtensions.size(); i++) {
			String pathToNormal = p.getProperty(String.valueOf(i) + NORMAL_DESCRIPTOR);
			normalPictures.put(fileExtensions.get(i), pathToNormal); 
			
			String pathToIgnored = p.getProperty(String.valueOf(i) + IGNORED_DESCRIPTOR); 
			ignoredPictures.put(fileExtensions.get(i), pathToIgnored); 
			
			String pathToNotSynchronized = p.getProperty(String.valueOf(i) + NOTSYNCHRONIZED_DESCRIPTOR); 
			notSynchronizedPictures.put(fileExtensions.get(i), pathToNotSynchronized); 
		}
	}
	
	private ImageView getPicture(Map<String, String> map, String extension) {
		String pathToImage = map.get(extension);
		if (pathToImage != null) {
			return new ImageView(pathToImage); 
		}
		return new ImageView(map.get(DEFAULT)); 
	}
	
	public static FileAppearanceManager getInstance() {
		if (instance == null) {
			instance = new FileAppearanceManager(); 
		}
		return instance;
	}
	
}
