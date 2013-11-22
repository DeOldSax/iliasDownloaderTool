package view;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CloseIcon extends JPanel {

	private static final long serialVersionUID = 1L;
	private final ImageIcon doorOpen;
	private final ImageIcon doorClosed;
	private final ImageIcon doorClosed2Color;
	private final JLabel label;

	public CloseIcon(Color background) {
		doorOpen = new ImageIcon(new ImageFactory().createImage("door_open.png"));
		doorClosed = new ImageIcon(new ImageFactory().createImage("door_closed.png"));
		doorClosed2Color = new ImageIcon(new ImageFactory().createImage("door_opened2Color.png"));
		this.addMouseListener(new IconChanger());
		label = new JLabel();
		label.setIcon(doorClosed);
		this.add(label);
		this.setOpaque(true);
		this.setBackground(background);
	}

	public class IconChanger extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			System.exit(0);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			label.setIcon(doorOpen);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			label.setIcon(doorClosed);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			label.setIcon(doorClosed2Color);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			label.setIcon(doorClosed);
		}
	}
}
