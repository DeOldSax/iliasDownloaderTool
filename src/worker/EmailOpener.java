package worker;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class EmailOpener extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().mail(new URI("mailto:DeOldSax@gmx.de?subject=BugReport/Verbesserungsvorschlag"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "DeOldSax@gmx.de", null, JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
