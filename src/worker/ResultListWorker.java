package worker;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

import model.PDF;
import view.DownloaderToolWindow;
import view.TreeCellRenderer;

public class ResultListWorker implements Runnable {

	private final String status;
	private final JTree tree;
	private final List<PDF> allPdf;
	private final List<PDF> pdfAddToResultList;
	private final DownloaderToolWindow window;
	private final IliasStarter iliasStarter;
	private final MouseEvent event;

	public ResultListWorker(String status, JTree tree, IliasStarter iliasStarter, DownloaderToolWindow window, MouseEvent event) {
		this.status = status;
		this.tree = tree;
		this.iliasStarter = iliasStarter;
		this.window = window;
		this.event = event;
		this.allPdf = iliasStarter.getAllPdfs();
		pdfAddToResultList = new ArrayList<PDF>();
	}

	@Override
	public void run() {
		act();
	}

	public void act() {
		pdfAddToResultList.clear();
		if (status.equals("ungelesen")) {
			expandAllUnread();
		}
		if (status.equals("nicht_vorhanden")) {
			expandAllMissing();
		}
	}

	private void expandAllMissing() {
		tree.setCellRenderer(new TreeCellRenderer(iliasStarter));
		tree.getSelectionModel().clearSelection();
		final List<Integer> allLocalPdfSizes = new LocalDataReader().getAllLocalPDFSizes();
		for (PDF pdf : allPdf) {
			if (pdf.isIgnored()) {
				continue;
			}
			if (!allLocalPdfSizes.contains(pdf.getSize())) {
				pdfAddToResultList.add(pdf);
			}
		}
		showInResultList();
	}

	private void expandAllUnread() {
		tree.setCellRenderer(new TreeCellRenderer(iliasStarter));
		tree.getSelectionModel().clearSelection();
		for (PDF pdf : allPdf) {
			if (pdf.isIgnored()) {
				continue;
			}
			if (!pdf.isRead()) {
				pdfAddToResultList.add(pdf);
			}
		}
		showInResultList();
	}

	private void showInResultList() {
		window.clearResultList();
		if (pdfAddToResultList.isEmpty()) {
			InformationWindow.initWindow("Keine Dateien vorhanden!", "OK", null, event);
			return;
		}
		for (PDF pdfResult : pdfAddToResultList) {
			window.addToResultList(pdfResult);
		}
	}
}
