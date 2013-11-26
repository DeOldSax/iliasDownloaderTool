package main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import view.LookAndFeelChanger;

public class VersionValidator {
	private final int YES = 0;
	private final String VERSION = "v0.1.0";
	private String LATEST_VERSION;

	public boolean validate() {
		LookAndFeelChanger.changeToNative();
		// TODO call Version in git

		DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet request = new HttpGet("https://github.com/repos/DeOldSax/iliasDownloaderTool/v0.1.0");
		// final HttpGet request = new
		// HttpGet("/repos/:owner/:repo/releases/:id");
		HttpResponse response = null;
		String content = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		request.releaseConnection();

		if (VERSION.equals(LATEST_VERSION)) {
			return false;
		}

		int answer = JOptionPane.showOptionDialog(null, "    Version Herunterladen?", "Neue Version Verfügbar!", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "Ja", "Später" }, null);
		if (answer == YES) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/DeOldSax/iliasDownloaderTool/releases/latest"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				// TODO better option to copy link
				JOptionPane.showMessageDialog(null, "https://github.com/DeOldSax/iliasDownloaderTool/releases/latest", "follow link",
						JOptionPane.INFORMATION_MESSAGE);
			}
			return true;
		}
		return false;
	}
}
