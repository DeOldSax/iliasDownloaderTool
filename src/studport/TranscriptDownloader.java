package studport;

import iliasWorker.ClientCloner;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import view.LocalFolderService;
import worker.InformationWindow;

public class TranscriptDownloader implements Runnable {
	private final DefaultHttpClient client;
	private final String filename;
	private final String downloadPdfUrl;
	private HttpResponse response;
	private HttpContext context;

	public TranscriptDownloader(DefaultHttpClient client, String downloadPdfUrl, String filename) {
		this.client = client;
		this.downloadPdfUrl = downloadPdfUrl;
		this.filename = filename;
	}

	@Override
	public void run() {
		final String saveDirectory = getSaveDirectory();

		if (saveDirectory == null) {
			return;
		}

		final DefaultHttpClient clonedClient = ClientCloner.cloneClient(client);

		try {
			final HttpGet httpGet = new HttpGet(downloadPdfUrl);
			response = clonedClient.execute(httpGet, context);

			File pdfTranscript = new File(saveDirectory);
			BufferedInputStream in = new BufferedInputStream(response.getEntity().getContent());

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pdfTranscript));

			int inByte;
			while ((inByte = in.read()) != -1) {
				out.write(inByte);
			}

			InformationWindow.createInformationMessage(filename, true);
			in.close();
			out.close();

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(pdfTranscript);
			}

			httpGet.releaseConnection();

		} catch (Exception e) {
			InformationWindow.createErrorMessage("Downloader Thread Exception_D aufgetreten!");
			e.printStackTrace();
		}
	}

	private String getSaveDirectory() {
		String userSelectedPath;
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setSelectedFile(new File(LocalFolderService.getLocalIliasPathString() + "/" + filename + ".pdf"));

		final int returnValue = fileChooser.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			userSelectedPath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!userSelectedPath.endsWith(".pdf")) {
				userSelectedPath = userSelectedPath + ".pdf";
			}
		} else {
			return null;
		}
		return userSelectedPath;
	}
}
