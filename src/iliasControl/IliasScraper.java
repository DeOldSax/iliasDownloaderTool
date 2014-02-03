package iliasControl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import model.IliasFolder;
import model.IliasForum;
import model.IliasPdf;
import model.IliasTreeNode;
import model.Settings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import view.Dashboard;

public class IliasScraper {
	public AtomicInteger threadCount;
	public AtomicInteger pdfCounter;
	private List<IliasFolder> iliasTree;

	public IliasScraper() {
		threadCount = new AtomicInteger(0);
		pdfCounter = new AtomicInteger(0);
	}

	public void run(String dashboardHtml) {
		this.iliasTree = getCourses(dashboardHtml);
		startThread(iliasTree);
	}

	private void startThread(List<IliasFolder> iliasTree) {
		if (Settings.getInstance().updateCanceled()) {
			return;
		}
		threadCount.incrementAndGet();
		new Thread(new IliasScraperThread(this, iliasTree)).start();
	}

	public List<IliasFolder> getIliasTree() {
		return iliasTree;
	}

	public List<IliasFolder> getCourses(String dashboardHtml) {
		List<IliasFolder> courses = new ArrayList<IliasFolder>();
		String s = null;
		Document doc = Jsoup.parse(dashboardHtml);
		List<Element> temp = doc.select("h4");

		for (Element x : temp) {
			s = s + x.toString();
		}

		doc = Jsoup.parse(s);
		temp = doc.select("a[href]");

		for (Element x : temp) {
			String url = x.attr("abs:href");
			if (url.contains("goto_produktiv_crs_")) {
				String name = x.text();
				if (name.contains("]")) {
					int index = x.text().indexOf("]");
					name = name.substring(index + 2);
				}
				courses.add(new IliasFolder(name, url, null));
			}
		}
		return courses;
	}

	private class IliasScraperThread implements Runnable {
		private final IliasScraper iliasPdfFinder;
		private final List<IliasFolder> kurse;

		private IliasScraperThread(IliasScraper iliasPdfFinder, List<IliasFolder> kurse) {
			this.iliasPdfFinder = iliasPdfFinder;
			this.kurse = kurse;
		}

		@Override
		public void run() {
			for (IliasFolder parent : kurse) {
				if (Settings.getInstance().updateCanceled()) {
					break;
				}
				List<Element> directory = openFolder(parent);
				for (Element dir : directory) {
					if (Settings.getInstance().updateCanceled()) {
						break;
					}
					final boolean dirIstPdfFile = dir.attr("href").contains("cmd=sendfile");
					final boolean linkToFolder = dir.attr("href").contains("goto_produktiv_fold_")
							|| dir.attr("href").contains("goto_produktiv_grp_");
					final boolean linkToForum = dir.attr("href").contains("goto_produktiv_frm_");
					if (dirIstPdfFile) {
						dir.setBaseUri("https://ilias.studium.kit.edu/");
						final int size = new IliasConnector().requestHead(dir.attr("abs:href"));
						IliasPdf newPdfFile = createPDF(parent, dir, size);

						Dashboard.setStatusText(pdfCounter.toString() + " Dateien wurden bereits überprüft.");

						List<Element> elemse = dir.parent().parent().siblingElements().select("div").select("span");
						for (Element el : elemse) {
							final boolean istUngelesen = el.attr("class").contains("il_ItemAlertProperty");
							if (istUngelesen) {
								newPdfFile.setRead(false);
							}
						}
					}
					if (linkToForum) {
						createForum(parent, dir);
					}
					if (linkToFolder) {
						List<IliasFolder> tempo = new ArrayList<IliasFolder>();
						IliasFolder newFolder = createFolder(parent, dir);
						tempo.add(newFolder);
						iliasPdfFinder.startThread(tempo);
					}
				}
			}
			iliasPdfFinder.threadCount.decrementAndGet();
		}

		private IliasForum createForum(IliasFolder parent, Element dir) {
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final IliasForum forum = new IliasForum(name, link, parent);
			return forum;
		}

		private IliasFolder createFolder(IliasFolder kurs, Element dir) {
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String downloadLink = dir.attr("abs:href");
			final IliasFolder folder = new IliasFolder(name, downloadLink, kurs);
			return folder;
		}

		private IliasPdf createPDF(IliasFolder parentDirectory, Element dir, int size) {
			pdfCounter.incrementAndGet();
			dir.setBaseUri("https://ilias.studium.kit.edu/");
			final String name = dir.text();
			final String downloadLink = dir.attr("abs:href");
			final IliasPdf pdf = new IliasPdf(name, downloadLink, parentDirectory, size);
			return pdf;
		}

		private List<Element> openFolder(IliasTreeNode kurs) {
			List<Element> directory;
			final String newHtmlContent = new IliasConnector().requestGet(kurs.getUrl());
			Document doc = Jsoup.parse(newHtmlContent);
			directory = doc.select("h4").select("a");
			return directory;
		}
	}
}
