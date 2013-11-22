package view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFactory {
	public Image createImage(String filename) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
