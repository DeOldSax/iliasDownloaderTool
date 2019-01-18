package control;

import java.util.*;
import java.util.concurrent.atomic.*;

import model.*;
import model.persistance.*;

import org.apache.log4j.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import utils.*;
import view.*;

public class IliasScraper {
	public AtomicInteger threadCount;
	public AtomicInteger fileCounter;
	private List<IliasFolder> iliasTree;
	private final Dashboard dashboard;

	public IliasScraper(Dashboard dashboard) {
		this.dashboard = dashboard;
		threadCount = new AtomicInteger(0);
		fileCounter = new AtomicInteger(0);
	}

	public void run(String dashboardHtml) {
		this.iliasTree = getCourses(dashboardHtml);

		for (IliasFolder parent : iliasTree) {
			if (!Settings.getInstance().getFlags().updateCanceled()) {
				startThread(parent);
			}
		}

	}

	private void startThread(IliasFolder parent) {
		threadCount.incrementAndGet();
		new Thread(new IliasScraperThread(this, parent)).start();
	}

	public List<IliasFolder> getIliasTree() {
		return iliasTree;
	}

	private List<IliasFolder> getCourses(String dashboardHtml) {
		List<IliasFolder> courses = new ArrayList<IliasFolder>();
		String s = null;
		Document doc = Jsoup.parse(dashboardHtml);
		List<Element> temp = doc.select("h4");

		for (Element x : temp) {
			s = s + x.toString();
		}

		doc = Jsoup.parse(s);
		temp = doc.select("a[href]");

		String BASE_URI = IliasManager.getInstance().getBaseUri();

		for (Element aTag : temp) {
			aTag.setBaseUri(BASE_URI);
			String url = aTag.attr("abs:href");

			// first condition is for kit + tueb, second for uni stuttgart
			if (url.toLowerCase().contains("baseClass=ilrepositorygui") || url.toLowerCase().contains("stuttgart_crs")) {
				String name = IliasCourseFormatter.formatCourseName(aTag.text());
				courses.add(new IliasFolder(name, url, null));
			}
		}
		return courses;
	}

	private class IliasScraperThread implements Runnable {
		String BASE_URI = IliasManager.getInstance().getBaseUri();
		private final IliasScraper iliasScraper;
		private final IliasFolder parent;

		private IliasScraperThread(IliasScraper iliasScraper, IliasFolder parent) {
			this.iliasScraper = iliasScraper;
			this.parent = parent;
		}

		@Override
		public void run() {
			if (!Settings.getInstance().getFlags().updateCanceled()) {

				List<Element> directory = openFolder(parent);
				for (Element dir : directory) {
					if (Settings.getInstance().getFlags().updateCanceled()) {
						break;
					}
					dir.setBaseUri(BASE_URI);

					// TODO check group folder
					final boolean linkToFolder = dir.attr("href").contains("cmd=view")
							|| dir.attr("href").toLowerCase().contains("stuttgart_fold");
					final boolean linkToFile = dir.attr("href").contains("download");
					final boolean linkToForum = dir.attr("href").contains("cmd=showThreads");
					final boolean linkToHyperlink = false;

					if (linkToFile) {
						fileCounter.incrementAndGet();
						updateStatusText();
						createFile(parent, dir);
					} else if (linkToForum) {
						createForum(parent, dir);
					} else if (linkToFolder) {
						IliasFolder newFolder = createFolder(parent, dir);
						iliasScraper.startThread(newFolder);
					} else if (linkToHyperlink) {
						// TODO implement
					}
				}
			}

			iliasScraper.threadCount.decrementAndGet();
		}

		private void updateStatusText() {
			dashboard.setStatusText(fileCounter.toString() + " Dateien wurden bereits überprüft.");
		}

		private IliasForum createForum(IliasFolder parent, Element dir) {
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final IliasForum forum = new IliasForum(name, link, parent);
			return forum;
		}

		private IliasFolder createFolder(IliasFolder kurs, Element dir) {
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final IliasFolder folder = new IliasFolder(name, link, kurs);
			return folder;
		}

		private IliasFile createFile(IliasFolder parentFolder, Element dir) {
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final int fileSize = new IliasConnector().getFileSize(link);
			final IliasFileMetaInformation metaInf = suggestMetaInformation(dir);
			final IliasFile iliasFile = new IliasFile(name, link, parentFolder, fileSize, metaInf.getSizeLabel(),
					metaInf.getFileExtension());
			return iliasFile;
		}

		private List<Element> openFolder(IliasTreeNode kurs) {
			List<Element> directory;
			final String newHtmlContent = new IliasConnector().requestGet(kurs.getUrl());
			Document doc = Jsoup.parse(newHtmlContent);
			directory = doc.select("h4").select("a");
			return directory;
		}

		private IliasFileMetaInformation suggestMetaInformation(Element dir) {
			String sizeLabel = "unknown";
			String fileExtension = "unknown";
			Elements siblingElements = dir.parent().parent().siblingElements();
			for (Element element : siblingElements) {
				if (element.attr("class").contains("il_ItemProperties")) {
					Elements children = element.children();
					for (int i = 0; i < children.size(); i++) {
						String text = children.get(i).text().replace("\u00a0", "").trim();
						if (text.matches("(\\d)(.*)(B|b)(.*)")) {
							sizeLabel = text;
							fileExtension = children.get(i - 1).text().replace("\u00a0", "").trim();
						}
					}
					return new IliasFileMetaInformation(sizeLabel, fileExtension);
				}
			}
			Logger.getLogger(getClass()).warn("ERROR: File Extension could not be found[2]");
			return new IliasFileMetaInformation(sizeLabel, fileExtension);
		}
	}
}
