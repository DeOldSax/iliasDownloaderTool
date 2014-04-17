package control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;

import view.Dashboard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.IliasPdf;
import model.IliasTreeNode;
import model.IliasTreeProvider;

public class DownloaderTask extends Task<Void> {
	private IliasTreeNode node;
	private DownloadMode mode;

	public DownloaderTask(final IliasTreeNode node, DownloadMode mode) {
		this.node = node;
		this.mode = mode;
	}
	
	public DownloaderTask(final IliasTreeNode node) {
		this(node, DownloadMode.NORMAL); 
	}

	@Override
	protected Void call() throws Exception {
		final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();

		for (IliasPdf pdf : allPdfFiles) {
			if (node instanceof IliasPdf) {
				if (pdf.getUrl().equals(node.getUrl())) {
					new Thread(new FileDownloader(pdf, ".pdf")).start();
				}
			}
		}
		return null;
	}
	
	private class FileDownloader extends Task<Void>{
		private HttpGet request;
		private HttpResponse response;
		private BasicHttpContext context;
		private HttpEntity entity;
		private final IliasPdf pdf;
		private String targetPath;
		private final String type;
		private String name;

		public FileDownloader(IliasPdf pdf, String type) {
			this.pdf = pdf;
			this.targetPath = LocalPdfStorage.getInstance().suggestDownloadPath(pdf);
			this.type = type;
			name = pdf.getName().replace(":", " - ").replace("/", "+");
		}

		@Override
		protected Void call() throws Exception {
			switch (mode) {
			case AUTO:
				new Thread(new Runnable() {
					@Override
					public void run() {
						download();
					}
				}).start();
				break;
			case NORMAL:
				askForStoragePosition();
				break;
			}
			return null; 
		}

		private void askForStoragePosition() {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(targetPath));
			fileChooser.setInitialFileName(name + type);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					final File selectedFile = fileChooser.showSaveDialog(new Stage());
					if (selectedFile != null) {
						targetPath = selectedFile.getAbsolutePath();
						if (!targetPath.endsWith(".pdf")) {
							targetPath = targetPath + ".pdf";
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								download();
							}
						}).start();
					}
				}
			});
		}

		private void download() {
			try {
				request = new HttpGet(pdf.getUrl());

				response = Ilias.getClient().execute(request, context);
				entity = response.getEntity();

				BufferedInputStream in = new BufferedInputStream(entity.getContent());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetPath)));
				System.out.println("store file: " + targetPath);

				int inByte;
				while ((inByte = in.read()) != -1) {
					out.write(inByte);
				}

				in.close();
				out.close();

				request.releaseConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LocalPdfStorage.getInstance().addPdf(pdf, targetPath);
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Dashboard.fileDownloaded(pdf);
				}
			});
		}
	}
}
