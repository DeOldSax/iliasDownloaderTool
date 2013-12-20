package worker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTree;

import model.PDF;
import view.DownloaderToolWindow;
import view.LocalFolderService;

public class ButtonHandler extends MouseAdapter {

	private final String status;
	private final JTree tree;
	private final DownloaderToolWindow window;
	private final IliasStarter iliasStarter;
	private final List<PDF> allPdf;
	private MouseEvent event;

	public ButtonHandler(String status, JTree tree, IliasStarter iliasStarter, DownloaderToolWindow window) {
		this.status = status;
		this.tree = tree;
		this.iliasStarter = iliasStarter;
		this.window = window;
		allPdf = iliasStarter.getAllPdfs();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		this.event = event;
		if (status.equals("ignorierte")) {
			showIgnoredPdfs();
			return;
		}
		if (status.equals("lokalerIliasOrdner")) {
			LocalFolderService.setVisible(true);
		}
		new Thread(new ResultListWorker(status, tree, iliasStarter, window)).run();
	}

	private void showIgnoredPdfs() {
		boolean addedPdf = false;
		window.clearResultList();
		for (PDF pdf : allPdf) {
			if (pdf.isIgnored()) {
				addedPdf = true;
				window.addToResultList(pdf);
			}
		}
		if (!addedPdf) {
			InformationWindow.initWindow("Keine ignorierten Dateien vorhanden!", "OK", null, event);
			// JOptionPane.showMessageDialog(null, "     keine gefunden!", null,
			// JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
