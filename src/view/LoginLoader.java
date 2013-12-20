package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LoginLoader {
	JLabel message;
	Background panel;
	JLabel loaderCircle;
	JFrame loader;
	Container c;

	public LoginLoader() {
		Image image = new ImageFactory().createImage("door_closed.png");
		loader = new JFrame();
		loader.setIconImage(image);
		c = loader.getContentPane();
		loader.getContentPane().setLayout(null);
		loaderCircle = new JLabel(new ImageIcon(getClass().getResource("laden.gif")));
		loaderCircle.setBounds(0, 0, 794, 94);
		c.add(loaderCircle);
		panel = new Background();
		panel.setBounds(0, 94, 794, 38);
		message = new JLabel("Login...");
		panel.add(message, BorderLayout.CENTER);
		c.add(panel);
		loader.setSize(800, 160);
		loader.setTitle("Anmeldung");
		loader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loader.setResizable(false);
		loader.setLocationRelativeTo(null);

		loaderCircle.setOpaque(true);
		loaderCircle.setBackground(Color.WHITE);
		loaderCircle.setForeground(Color.BLACK);
		loaderCircle.setBorder(BorderFactory.createEmptyBorder(10, 1, 1, 1));

		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 10, 1));

		message.setOpaque(true);
		message.setBackground(Color.WHITE);
		message.setForeground(Color.BLACK);
		message.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		message.setFont(new Font("Arial", Font.BOLD, 12));

		loader.setVisible(true);
	}

	public void stopRunning() {
		loader.setVisible(false);
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void changeStatusMessage(String statusMessage) {
		message.setText(statusMessage);
	}
}
