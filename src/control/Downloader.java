package control;

import java.util.List;

import javafx.application.Platform;
import model.IliasTreeNode;
import model.IliasPdf;
import model.IliasTreeProvider;

public class Downloader {
	public void download(final IliasTreeNode node) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final List<IliasPdf> allPdfFiles = IliasTreeProvider.getAllPdfFiles();

				for (IliasPdf pdf : allPdfFiles) {
					if (node instanceof IliasPdf) {
						if (pdf.getUrl().equals(node.getUrl())) {
							Platform.runLater(new FileDownloader(pdf, ".pdf"));
						}
					}
				}
			}
		});
	}
}
