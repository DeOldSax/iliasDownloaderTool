package laf;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Button extends JLabel {

	private static final long serialVersionUID = 6678947082955370336L;
	private static final Color DEFAULT_COLOR = Color.WHITE;

	public Button(String text) {
		super(text);
		setHorizontalAlignment(SwingConstants.CENTER);

		this.setFocusable(true);
		this.setOpaque(true);
		this.setBackground(DEFAULT_COLOR);
		this.setForeground(Color.GRAY);
		this.setFont(new Font("Calibri", Font.BOLD, 15));
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setBackground(Color.BLUE);
				label.setForeground(Color.LIGHT_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setBackground(DEFAULT_COLOR);
				label.setForeground(Color.GRAY);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel) e.getSource();
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}
		});
	}
}
