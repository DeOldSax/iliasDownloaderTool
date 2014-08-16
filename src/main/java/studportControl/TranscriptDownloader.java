package studportControl;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.persistance.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import view.Dashboard;
import control.IliasClientCloner;

public class TranscriptDownloader implements Runnable {
	private final DefaultHttpClient client;
	private final String filename;
	private final String downloadPdfUrl;
	private HttpResponse response;
	private HttpContext context;
	private final Studierendenportal studport;
	private final Dashboard dashboard;

	public TranscriptDownloader(Dashboard dashboard, DefaultHttpClient client, String downloadPdfUrl, String filename,
			Studierendenportal studport) {
		this.dashboard = dashboard;
		this.client = client;
		this.downloadPdfUrl = downloadPdfUrl;
		this.filename = filename;
		this.studport = studport;
	}

	@Override
	public void run() {
		final DefaultHttpClient clonedClient = IliasClientCloner.cloneClient(client);

		try {
			final HttpGet httpGet = new HttpGet(downloadPdfUrl);
			response = clonedClient.execute(httpGet, context);

			String saveDirectory = Settings.getInstance().getIliasFolderSettings().getLocalIliasFolderPath();
			if (saveDirectory == null || !new File(saveDirectory).exists() || saveDirectory.equals(".")) {
				saveDirectory = openSaveDirectoryDialog();
				if (saveDirectory == null) {
					return;
				}
			}

			File pdfTranscript = new File(saveDirectory + "/" + filename + ".pdf");

			if (!response.getLastHeader("Content-Type").getValue().contains("pdf")) {
				System.out.println("TranscriptDownloader:52:download failed");
				dashboard.setStatusText("Download fehlgeschlagen", true);
			}

			final InputStream pdfContent = response.getEntity().getContent();

			BufferedInputStream in = new BufferedInputStream(pdfContent);

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pdfTranscript));

			int inByte;
			while ((inByte = in.read()) != -1) {
				out.write(inByte);
			}

			in.close();
			out.close();

			httpGet.releaseConnection();

			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(pdfTranscript);
				} catch (IOException e) {
					studport.stopDownload();
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			studport.stopDownload();
			dashboard.setStatusText("Download fehlgeschlagen", true);
			System.out.println("TranscriptDownloader:88:download failed");
		}
		studport.stopDownload();
		dashboard.setStatusText("Notenauszug wurde im lokalen Ilias Ordner gespeichert.");
	}

	private String openSaveDirectoryDialog() {
		String userSelectedPath;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(filename);
		fileChooser.setInitialFileName(filename);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Adobe Acrobat Document", ".pdf"));
		final File selectedFile = fileChooser.showSaveDialog(null);

		if (selectedFile == null) {
			studport.stopDownload();
			return null;
		}

		userSelectedPath = selectedFile.getAbsolutePath();
		if (!userSelectedPath.endsWith(".pdf")) {
			userSelectedPath = userSelectedPath + ".pdf";
		}
		return userSelectedPath;
	}
}
