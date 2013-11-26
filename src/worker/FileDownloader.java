package worker;

import iliasWorker.Ilias;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import model.PDF;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;

import view.LookAndFeelChanger;

public class FileDownloader implements Runnable {
	private HttpGet request;
	private HttpResponse response;
	private BasicHttpContext context;
	private HttpEntity entity;
	private final PDF pdf;
	private String targetPath;
	private final String type;
	private final JFileChooser fileChooser;
	private String name;

	public FileDownloader(PDF pdf, String type) {
		LookAndFeelChanger.changeToNative();
		this.pdf = pdf;
		this.targetPath = new LocalDataReader().findLocalDownloadPath(pdf);
		this.type = type;
		fileChooser = new JFileChooser();
	}

	@Override
	public void run() {
		try {
			name = pdf.getName().replace(":", " - ").replace("/", "+");

			final boolean saved = openFileChooser();
			LookAndFeelChanger.changeToJava();

			if (saved) {
				request = new HttpGet(pdf.getUrl());

				response = Ilias.getClient().execute(request, context);
				entity = response.getEntity();

				BufferedInputStream in = new BufferedInputStream(entity.getContent());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetPath)));

				int inByte;
				while ((inByte = in.read()) != -1) {
					out.write(inByte);
				}

				in.close();
				out.close();

				request.releaseConnection();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean openFileChooser() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setCurrentDirectory(new File(targetPath));
		fileChooser.setSelectedFile(new File(name + "." + type));
		final int returnValue = fileChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			validate();
			return true;
		} else {
			return false;
		}
	}

	private void validate() {
		String userSelectedPath = fileChooser.getSelectedFile().getAbsolutePath();
		if (targetPath != userSelectedPath && userSelectedPath.endsWith(".pdf")) {
			targetPath = userSelectedPath;
		} else {
			targetPath = userSelectedPath + ".pdf";
		}
	}
}
