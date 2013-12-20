package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import laf.Button;
import model.StorageProvider;
import worker.IliasStarter;

public class LocalFolderService implements ActionListener {
	private static String localIliasPathString;
	private static JFrame dialog;

	private BackgroundColorPanel background;
	private Container c;
	private JLabel localIliasPath;
	private final IliasStarter iliasStarter;
	private final StorageProvider storageProvider;
	private final DownloaderToolWindow downloaderToolWindow;

	public LocalFolderService(IliasStarter iliasStarter, DownloaderToolWindow downloaderToolWindow) {
		this.iliasStarter = iliasStarter;
		this.downloaderToolWindow = downloaderToolWindow;
		storageProvider = new StorageProvider();
		localIliasPathString = storageProvider.loadLocalIliasFolderPath();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		initDialog();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void initDialog() {
		LookAndFeelChanger.changeToNative();
		dialog = new JFrame();
		c = dialog.getContentPane();
		Image image = new ImageFactory().createImage("folder_icon_lfs.jpg");
		dialog.setIconImage(image);
		background = new BackgroundColorPanel();
		c.add(background);
		background.setLayout(null);

		String loadLocalIliasFolderPath = storageProvider.loadLocalIliasFolderPath();
		if (loadLocalIliasFolderPath.equals(".")) {
			loadLocalIliasFolderPath = "..\\Kit\\Semester XY";
		}
		localIliasPath = new JLabel(loadLocalIliasFolderPath);
		background.add(localIliasPath);
		localIliasPath.setHorizontalAlignment(SwingConstants.CENTER);
		localIliasPath.setFont(new Font("Calibri", Font.BOLD, 14));
		localIliasPath.setBounds(10, 69, 409, 31);
		localIliasPath.setBorder(null);

		Button okButton = new Button("OK");
		background.add(okButton);
		okButton.addMouseListener(new Starter());
		okButton.setBounds(331, 258, 89, 23);

		Button downloadPathButton = new Button("Mein Lokaler Ilias Ordner");
		downloadPathButton.setText("Lokaler ILIAS-Ordner");
		background.add(downloadPathButton);
		downloadPathButton.setBounds(10, 11, 409, 32);
		downloadPathButton.addMouseListener(new LocalIliasFolderChooser(dialog, localIliasPath));

		JTextPane txtrDerLokaleIlias = new JTextPane();
		txtrDerLokaleIlias.setEditable(false);
		txtrDerLokaleIlias.setOpaque(true);
		txtrDerLokaleIlias.setBackground(Color.WHITE);
		txtrDerLokaleIlias.setForeground(Color.GRAY);
		txtrDerLokaleIlias.setFont(new Font("Calibri", Font.BOLD, 15));
		txtrDerLokaleIlias.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		txtrDerLokaleIlias
				.setText("Der lokale ILIAS-Ordner ist der Ordner, in dem du auf deinem Computer die PDF-Dateien aus dem ILIAS speicherst. Diese Angabe wird ben\u00F6tigt, damit ein Abgleich stattfinden kann, welche Dateien du bereits besitzt und welche noch nicht. Die Benennung deiner Unterordner oder Dateien spielt dabei keine Rolle.");
		txtrDerLokaleIlias.setBounds(10, 127, 409, 118);

		background.add(txtrDerLokaleIlias);

		dialog.setSize(434, 320);
		dialog.setTitle("Lokaler ILIAS-Ordner");
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setResizable(false);
	}

	public static void setVisible(boolean b) {
		dialog.setVisible(b);
	}

	public static String getLocalIliasPathString() {
		return new StorageProvider().loadLocalIliasFolderPath();
	}

	private class Starter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (SwingUtilities.isLeftMouseButton(event)) {
				act();
			}
		}

		private void act() {
			localIliasPathString = localIliasPath.getText().replace("\\", "/");
			File validationFile2 = new File(localIliasPathString);
			if (!validationFile2.exists()) {
				JOptionPane.showMessageDialog(null, "Ungültiger Pfad!", null, JOptionPane.ERROR_MESSAGE);
			} else {
				if (iliasStarter == null) {
					JOptionPane.showMessageDialog(null, "Fehler aufgetreten!", null, JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					return;
				}
				storageProvider.storeLocalIliasFolderPath(localIliasPathString);
				new StorageProvider().setLocalIliasPathTrue();
				dialog.setVisible(false);
				if (!DownloaderToolWindow.isRunning()) {
					downloaderToolWindow.setVisible(true);
				}
			}
		}
	}
}
