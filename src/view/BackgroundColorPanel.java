package view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BackgroundColorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Image image;

	public BackgroundColorPanel() {
		image = new ImageFactory().createImage("backgroundColorPanel.png");
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
