package control;

import java.util.List;

import javafx.application.Platform;
import model.IliasTreeNode;
import model.IliasPdf;
import model.IliasTreeProvider;

public class Downloader {
	public void download(final IliasTreeNode directory) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();

				for (IliasPdf pdf : allPdfFiles) {
					if (directory instanceof IliasPdf) {
						if (pdf.getUrl().equals(directory.getUrl())) {
							Platform.runLater(new FileDownloader(pdf, ".pdf"));
						}
					}
				}
			}
		});
	}
}
