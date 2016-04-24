package studportControl;

import java.io.*;

import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;

import org.apache.http.*;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.*;

import view.*;

public class TranscriptDownloader implements Runnable {
	private final DefaultHttpClient client;
	private final String filename;
	private final String downloadPdfUrl;
	private HttpResponse response;
	private HttpContext context;
	private final Studierendenportal studport;
	private final Dashboard dashboard;

	public TranscriptDownloader(Dashboard dashboard, DefaultHttpClient client,
			String downloadPdfUrl, String filename, Studierendenportal studport) {
		this.dashboard = dashboard;
		this.client = client;
		this.downloadPdfUrl = downloadPdfUrl;
		this.filename = filename;
		this.studport = studport;
	}

	@Override
	public void run() {
	}

	private String openSaveDirectoryDialog() {
		String userSelectedPath;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(filename);
		fileChooser.setInitialFileName(filename);
		fileChooser.getExtensionFilters()
				.add(new ExtensionFilter("Adobe Acrobat Document", ".pdf"));
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
