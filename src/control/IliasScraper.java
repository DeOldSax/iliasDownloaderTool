package control;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import model.IliasFile;
import model.IliasFolder;
import model.IliasForum;
import model.IliasTreeNode;
import model.Settings;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import view.Dashboard;

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
		String BASE_URI = "https://ilias.studium.kit.edu/";
		private final IliasScraper iliasScraper;
		private final List<IliasFolder> courses;

		private IliasScraperThread(IliasScraper iliasScraper, List<IliasFolder> courses) {
			this.iliasScraper = iliasScraper;
			this.courses = courses;
		}

		@Override
		public void run() {
			for (IliasFolder parent : courses) {
				if (Settings.getInstance().updateCanceled()) {
					break;
				}
				List<Element> directory = openFolder(parent);
				for (Element dir : directory) {
					if (Settings.getInstance().updateCanceled()) {
						break;
					}
					// if the file is not specified yet, use the "standard" file
					final boolean linkToFile = dir.attr("href").contains("cmd=sendfile");
					final String fileExtension = suggestFileExtension(dir); 
					final boolean linkToFolder = dir.attr("href").contains("goto_produktiv_fold_")
							|| dir.attr("href").contains("goto_produktiv_grp_");
					final boolean linkToForum = dir.attr("href").contains("goto_produktiv_frm_");
					final boolean linkToHyperlink = false; 

					int size = 0; 

					if (linkToFile) {
						updateStatusText();
						dir.setBaseUri(BASE_URI);
						String attr = dir.attr("abs:href");
						size = new IliasConnector().getFileSize(attr);
						createFile(parent, dir, size, fileExtension);
					} else if (linkToHyperlink) {
						//TODO implement
					} else if (linkToForum) {
						createForum(parent, dir);
					} else if (linkToFolder) {
						List<IliasFolder> tempo = new ArrayList<IliasFolder>();
						IliasFolder newFolder = createFolder(parent, dir);
						tempo.add(newFolder);
						iliasScraper.startThread(tempo);
					}
				}
			}
			iliasScraper.threadCount.decrementAndGet();
		}

		private void updateStatusText() {
			dashboard.setStatusText(fileCounter.toString() + " Dateien wurden bereits überprüft.");
		}

		private IliasForum createForum(IliasFolder parent, Element dir) {
			dir.setBaseUri(BASE_URI);
			final String name = dir.text();
			final String link = dir.attr("abs:href");
			final IliasForum forum = new IliasForum(name, link, parent);
			return forum;
		}

		private IliasFolder createFolder(IliasFolder kurs, Element dir) {
			dir.setBaseUri(BASE_URI);
			final String name = dir.text();
			final String downloadLink = dir.attr("abs:href");
			final IliasFolder folder = new IliasFolder(name, downloadLink, kurs);
			return folder;
		}
		
		private IliasFile createFile (IliasFolder parentFolder, Element dir, int size, String fileExtension) {
			fileCounter.incrementAndGet();
			dir.setBaseUri(BASE_URI);
			final String name = dir.text();
			System.out.println(fileExtension + "  " + fileExtension.length());
			final String downloadLink = dir.attr("abs:href");
			final IliasFile iliasFile = new IliasFile(name, downloadLink, fileExtension, parentFolder, size);
			return iliasFile;
		}

		private List<Element> openFolder(IliasTreeNode kurs) {
			List<Element> directory;
			final String newHtmlContent = new IliasConnector().requestGet(kurs.getUrl());
			Document doc = Jsoup.parse(newHtmlContent);
			directory = doc.select("h4").select("a");
			return directory;
		}
		
		private String suggestFileExtension(Element dir) {
			Elements siblingElements = dir.parent().parent().siblingElements();
			for (Element element : siblingElements) {
				if (element.attr("class").equals("il_ItemProperties")) {
					return element.child(0).text().trim().replace("\n", ""); 
				}
			}
			Logger.getLogger(getClass()).debug("ERROR: File Extension could not be found");
			return "unknown"; 
		}
	}
}
