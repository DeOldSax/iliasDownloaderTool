package view;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import laf.Button;
import model.StorageProvider;
import worker.IliasStarter;

public class LocalFolderService implements ActionListener {
	private static String localIliasPathString;
	private static String localDownloadPathString;
	private JFrame dialog;

	private BackgroundColorPanel background;
	private Container c;
	private JLabel localIliasPath, downloadPath;
	private final IliasStarter iliasStarter;
	private final StorageProvider storageProvider;

	public LocalFolderService(IliasStarter iliasStarter) {
		this.iliasStarter = iliasStarter;
		localDownloadPathString = "";
		localIliasPathString = "";
		storageProvider = new StorageProvider();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		showDialog();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void showDialog() {

		LookAndFeelChanger.changeToNative();
		dialog = new JFrame();
		c = dialog.getContentPane();
		background = new BackgroundColorPanel();
		c.add(background);
		background.setLayout(null);

		localIliasPath = new JLabel(storageProvider.loadLocalIliasFolderPath());
		background.add(localIliasPath);
		localIliasPath.setHorizontalAlignment(SwingConstants.CENTER);
		localIliasPath.setFont(new Font("Calibri", Font.BOLD, 14));
		localIliasPath.setBounds(10, 58, 392, 31);
		localIliasPath.setBorder(null);

		downloadPath = new JLabel(storageProvider.loadDownloadPath());
		background.add(downloadPath);
		downloadPath.setBorder(null);
		downloadPath.setHorizontalAlignment(SwingConstants.CENTER);
		downloadPath.setFont(new Font("Calibri", Font.BOLD, 14));
		downloadPath.setText(storageProvider.loadDownloadPath());
		downloadPath.setBounds(6, 138, 396, 31);

		Button okButton = new Button("OK");
		background.add(okButton);
		okButton.addMouseListener(new Starter());
		okButton.setBounds(162, 180, 89, 23);

		Button localIliasFolderPathButton = new Button("Hier möchte ich meine Downloads speichern");
		background.add(localIliasFolderPathButton);
		localIliasFolderPathButton.setBounds(10, 98, 392, 32);
		localIliasFolderPathButton.addMouseListener(new DownloadFolderChooser(dialog, downloadPath));

		Button downloadPathButton = new Button("Mein Lokale Ilias Ordner");
		background.add(downloadPathButton);
		downloadPathButton.setBounds(10, 11, 392, 32);
		downloadPathButton.addMouseListener(new DownloadFolderChooser(dialog, localIliasPath));

		dialog.setSize(418, 250);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	public static String getLocalIliasPathString() {
		return localIliasPathString;
	}

	public static String getLocalDownloadPathString() {
		return localDownloadPathString;
	}

	private class Starter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (SwingUtilities.isLeftMouseButton(event)) {
				act();
			}
		}

		private void act() {
			localDownloadPathString = downloadPath.getText();
			localIliasPathString = localIliasPath.getText();
			dialog.setVisible(false);
			if (iliasStarter != null) {
				storageProvider.storeDownloadPath(localDownloadPathString);
				storageProvider.storeLocalIliasFolderPath(localIliasPathString);
				new DownloaderToolWindow(iliasStarter);
			} else {
				JOptionPane.showMessageDialog(null, "Fehler aufgetreten!", null, JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

	}
}
