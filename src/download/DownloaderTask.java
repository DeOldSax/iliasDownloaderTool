package download;

import java.io.File;
import java.util.List;
import utils.WinUtils;
import control.LocalPdfStorage;
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
					download(pdf, ".pdf");
				}
			}
		}

		return null;
	}

	private void download(IliasPdf pdf, String type) {
		String targetPath = LocalPdfStorage.getInstance().suggestDownloadPath(pdf);
		String name = WinUtils.makeFileNameValid(pdf.getName()); 

		switch (mode) {
		case AUTO:
			targetPath = targetPath + "\\" + name + ".pdf";
			new Thread(new IliasPdfDownloaderTask(pdf, targetPath)).start();
			break;
		case NORMAL:
			askForStoragePosition(name, targetPath, type, pdf);
			break;
		}
	}

	private void askForStoragePosition(String name, final String targetPath, String type, final IliasPdf pdf) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(targetPath));
		fileChooser.setInitialFileName(name + type);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final File selectedFile = fileChooser.showSaveDialog(new Stage());
				String path = targetPath;
				if (selectedFile != null) {
					path = selectedFile.getAbsolutePath();
					if (!path.endsWith(".pdf")) {
						path = path + ".pdf";
					}
					new Thread(new IliasPdfDownloaderTask(pdf, path)).start();
				}
			}
		});
	}
}
