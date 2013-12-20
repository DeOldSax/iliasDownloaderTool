package worker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import laf.Button;
import view.BackgroundColorPanel;

public class InformationWindow extends MouseAdapter {
	private static JFrame dialog;
	private static Container c;
	private static BackgroundColorPanel background;
	private static JLabel label;
	private static Button button;

	public static void initWindow(String message, String buttonText, Component parent, MouseEvent event) {
		dialog = new JFrame();
		dialog.setUndecorated(true);
		c = dialog.getContentPane();
		background = new BackgroundColorPanel();
		background.setLayout(null);
		background.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
		c.add(background);
		label = new JLabel(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(new Rectangle(10, 22, 322, 32));
		label.setForeground(Color.BLACK);
		label.setFont(new Font("calibri", Font.BOLD, 16));
		background.add(label);
		button = new Button(buttonText);
		button.setBounds(new Rectangle(147, 76, 52, 33));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		background.add(button);
		dialog.setLocation(event.getXOnScreen() - 171, event.getYOnScreen() - 60);
		dialog.setResizable(false);
		dialog.setSize(342, 120);
		dialog.setVisible(true);
	}

	public static void createErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, null, JOptionPane.ERROR_MESSAGE);
	}

	public static void createInformationMessage(String filename, boolean split) {
		JOptionPane.showMessageDialog(null, "Download beendet. Ihr Notenauszug [" + filename + "] befindet sich auf Ihrem Computer!", null,
				JOptionPane.INFORMATION_MESSAGE);
	}
}
