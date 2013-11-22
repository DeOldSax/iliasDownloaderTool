package view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class Background extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Image image;

	public Background() {
		image = new ImageFactory().createImage("background.JPG");
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
