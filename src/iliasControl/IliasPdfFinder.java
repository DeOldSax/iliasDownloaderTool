package iliasControl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import model.Directory;
import model.Folder;
import model.Forum;
import model.PDF;
import model.Settings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import view.Dashboard;

public class IliasPdfFinder {

	private final List<PDF> allPdfs;
	private final List<Directory> allDirs;
	public AtomicInteger threadCount;
	private List<Directory> kurse;

	public IliasPdfFinder() {
		allPdfs = new ArrayList<PDF>();
		allDirs = new ArrayList<Directory>();
		threadCount = new AtomicInteger(0);
	}

	public void findAllPdfs(final List<Directory> kurse) {
		this.kurse = kurse;
		startScanner(kurse);
	}

	private void startScanner(List<Directory> kurse) {
		if (Settings.getInstance().updateCanceled()) {
			return;
		}
		threadCount.incrementAndGet();
		new Thread(new IliasDirectoryScanner(this, kurse)).start();
	}

	public List<PDF> getAllPdfs() {
		return allPdfs;
	}

	public List<Directory> getAllDirs() {
		return allDirs;
	}

	public List<Directory> getKurse() {
		return kurse;
	}

	private class IliasDirectoryScanner implements Runnable {

		private final IliasPdfFinder iliasPdfFinder;
		private final List<Directory> kurse;

		private IliasDirectoryScanner(IliasPdfFinder iliasPdfFinder, List<Directory> kurse) {
			this.iliasPdfFinder = iliasPdfFinder;
			this.kurse = kurse;
		}

		@Override
		public void run() {
			for (Directory kurs : kurse) {
				if (Settings.getInstance().updateCanceled()) {
					break;
				}
				List<Element> directory = openFolder(kurs);
				for (Element dir : directory) {
					final boolean dirIstPdfFile = dir.attr("href").contains("cmd=sendfile");
					final boolean linkToFolder = dir.attr("href").contains("goto_produktiv_fold_")
							|| dir.attr("href").contains("goto_produktiv_grp_");
					final boolean linkToForum = dir.attr("href").contains("goto_produktiv_frm_");
					if (dirIstPdfFile) {
						dir.setBaseUri("https://ilias.studium.kit.edu/");
						final int size = new IliasConnector().requestHead(dir.attr("abs:href"));
						PDF newPdfFile = createPDF(kurs, dir, size);
						allPdfs.add(newPdfFile);

						Dashboard.setStatusText(String.valueOf(allPdfs.size()) + " Dateien wurden bereits überprüft.");

						List<Element> elemse = dir.parent().parent().siblingElements().select("div").select("span");
						for (Element el : elemse) {
							final boolean istUngelesen = el.attr("class").contains("il_ItemAlertProperty");
							if (istUngelesen) {
								newPdfFile.setRead(false);
							}
						}
					}
					if (linkToForum) {
						final Forum forum = createForum(kurs, dir);
						allDirs.add(forum);
					}
					if (linkToFolder) {
						List<Directory> tempo = new ArrayList<Directory>();
						Folder newFolder = createFolder(kurs, dir);
						tempo.add(newFolder);
						allDirs.add(newFolder);
						iliasPdfFinder.startScanner(tempo);
					}
				}
			}
			iliasPdfFinder.threadCount.decrementAndGet();
		}

		private Forum createForum(Directory kurs, Element dir) {
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final Forum forum = new Forum(name, link, kurs);
			return forum;
		}

		private Folder createFolder(Directory kurs, Element dir) {
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String downloadLink = dir.attr("abs:href");
			final Folder folder = new Folder(name, downloadLink, kurs);
			return folder;
		}

		private PDF createPDF(Directory parentDirectory, Element dir, int size) {
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String downloadLink = dir.attr("abs:href");
			final PDF pdf = new PDF(name, downloadLink, parentDirectory, size);
			return pdf;
		}

		private List<Element> openFolder(Directory kurs) {
			List<Element> directory;
			final String newHtmlContent = new IliasConnector().requestGet(kurs.getUrl());
			Document doc = Jsoup.parse(newHtmlContent);
			directory = doc.select("h4").select("a");
			return directory;
		}
	}
}
