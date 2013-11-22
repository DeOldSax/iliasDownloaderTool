package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import worker.IliasStarter;

public class LocalFolderService implements ActionListener {
	private static String localIliasPathString;
	private static String localDownloadPathString;
	private JFrame dialog;

	private Container c;
	private JTextField localIliasPath, downloadPath;
	private final IliasStarter iliasStarter;

	public LocalFolderService() {
		iliasStarter = null;
	}

	public LocalFolderService(IliasStarter iliasStarter) {
		this.iliasStarter = iliasStarter;
		localDownloadPathString = "";
		localIliasPathString = "";
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		showDialog();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void showDialog() {
		dialog = new JFrame();
		c = dialog.getContentPane();
		dialog.setUndecorated(true);
		dialog.setType(Type.UTILITY);
		dialog.getContentPane().setBackground(new Color(102, 205, 170));
		dialog.getContentPane().setForeground(new Color(46, 139, 87));
		dialog.setForeground(new Color(46, 139, 87));
		dialog.getContentPane().setLayout(null);

		localIliasPath = new JTextField(localIliasPathString);
		localIliasPath.setHorizontalAlignment(SwingConstants.CENTER);
		localIliasPath.setFont(new Font("Calibri", Font.BOLD, 14));
		localIliasPath.setBounds(10, 46, 396, 31);
		localIliasPath.setBorder(null);
		c.add(localIliasPath);

		downloadPath = new JTextField();
		downloadPath.setBorder(null);
		downloadPath.setHorizontalAlignment(SwingConstants.CENTER);
		downloadPath.setFont(new Font("Calibri", Font.BOLD, 14));
		downloadPath.setText(localDownloadPathString);
		downloadPath.setBounds(10, 128, 396, 31);
		downloadPath.addActionListener(new Starter());
		dialog.getContentPane().add(downloadPath);
		downloadPath.setColumns(10);

		JButton btnStarten = new JButton("OK");
		btnStarten.addActionListener(new Starter());
		btnStarten.setBackground(new Color(32, 178, 170));
		btnStarten.setBounds(169, 180, 89, 23);
		dialog.getContentPane().add(btnStarten);

		JLabel lblMeinLokaleIlias = new JLabel("Mein Lokale Ilias Ordner:");
		lblMeinLokaleIlias.setForeground(Color.BLACK);
		lblMeinLokaleIlias.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMeinLokaleIlias.setBounds(10, 11, 170, 24);
		dialog.getContentPane().add(lblMeinLokaleIlias);

		JLabel lblHierMchteIch = new JLabel("Hier m\u00F6chte ich meine Downloads speichern:");
		lblHierMchteIch.setForeground(Color.BLACK);
		lblHierMchteIch.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblHierMchteIch.setBounds(10, 98, 298, 19);
		dialog.getContentPane().add(lblHierMchteIch);

		JButton btnffnen = new JButton("\u00F6ffnen...");
		btnffnen.setBackground(new Color(32, 178, 170));
		btnffnen.setBounds(317, 13, 89, 23);
		dialog.getContentPane().add(btnffnen);

		JButton btnffnen_1 = new JButton("\u00F6ffnen...");
		btnffnen_1.setBackground(new Color(32, 178, 170));
		btnffnen_1.setBounds(318, 94, 89, 23);
		dialog.getContentPane().add(btnffnen_1);
		dialog.setSize(418, 210);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setResizable(false);
	}

	public static String getLocalIliasPathString() {
		return localIliasPathString;
	}

	public static String getLocalDownloadPathString() {
		return localDownloadPathString;
	}

	private class Starter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			localDownloadPathString = downloadPath.getText().replace("\\", "/");
			File validationFile = new File(localDownloadPathString);
			localIliasPathString = localIliasPath.getText().replace("\\", "/");
			File validationFile2 = new File(localIliasPathString);
			if (!validationFile.exists()) {
				JOptionPane.showMessageDialog(null, "ungültiger Download Pfad", null, JOptionPane.ERROR_MESSAGE);

			}
			if (!validationFile2.exists()) {
				JOptionPane.showMessageDialog(null, "ungültiger Ilias Ordner Pfad", null, JOptionPane.ERROR_MESSAGE);
			}

			dialog.setVisible(false);
			if (iliasStarter != null) {
				new DownloaderToolWindow(iliasStarter);
			}
		}
	}
}
