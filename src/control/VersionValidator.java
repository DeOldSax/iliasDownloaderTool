package control;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VersionValidator {
	private final int YES = 0;
	private final String VERSION = "v0.2.0";
	private String LATEST_VERSION;

	public boolean validate() {

		DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet request = new HttpGet("https://github.com/DeOldSax/iliasDownloaderTool/releases/latest");
		HttpResponse response = null;
		try {
			response = client.execute(request);
			String content = EntityUtils.toString(response.getEntity());
			Document doc = Jsoup.parse(content);
			final Elements select = doc.select("span");
			for (Element element : select) {
				if (element.attr("class").equals("css-truncate-target")) {
					LATEST_VERSION = element.text();
					if (!LATEST_VERSION.startsWith("v")) {
						LATEST_VERSION = VERSION;
					}
				}
			}

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
				JOptionPane.showMessageDialog(null, "https://github.com/DeOldSax/iliasDownloaderTool/releases/latest", "follow link",
						JOptionPane.INFORMATION_MESSAGE);
			}
			return true;
		}
		return false;
	}
}