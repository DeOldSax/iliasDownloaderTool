package worker;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class FAQOpener extends MouseAdapter {
	@Override
	public void mouseClicked(MouseEvent e) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/DeOldSax/iliasDownloaderTool/wiki"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
			return;
		}
		JOptionPane.showMessageDialog(null, "https://github.com/DeOldSax/iliasDownloaderTool/wiki", null, JOptionPane.INFORMATION_MESSAGE);
	}
}
