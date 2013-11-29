package worker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;

import model.PDF;
import model.StorageProvider;
import view.DownloaderToolWindow;

public class ButtonHandler extends MouseAdapter {

	private final String status;
	private final JTree tree;
	private final DownloaderToolWindow window;
	private final IliasStarter iliasStarter;
	private final List<PDF> allPdf;
	private final StorageProvider storageProvider;

	public ButtonHandler(String status, JTree tree, IliasStarter iliasStarter, DownloaderToolWindow window) {
		this.status = status;
		this.tree = tree;
		this.iliasStarter = iliasStarter;
		this.window = window;
		allPdf = iliasStarter.getAllPdfs();
		storageProvider = new StorageProvider();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (status.equals("ignorierte")) {
			showIgnoredPdfs();
			return;
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
			JOptionPane.showMessageDialog(null, "     keine gefunden!", null, JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
