package iliasWorker;

import java.util.LinkedList;
import java.util.List;

import model.Adresse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import view.LoginLoader;

public class IliasPdfFinder {

	private final List<Adresse> allPdfs;
	private final List<Adresse> allDirs;
	private final LoginLoader loginLoader;

	public IliasPdfFinder(LoginLoader loginLoader) {
		this.loginLoader = loginLoader;
		allPdfs = new LinkedList<Adresse>();
		allDirs = new LinkedList<Adresse>();
	}

	public List<Adresse> findAllPdfs(List<Adresse> kurse) {
		for (Adresse kurs : kurse) {
			showInGui(kurs);
			List<Element> directory = openFolder(kurs);
			for (Element dir : directory) {
				final boolean dirIstPdfFile = dir.attr("href").contains("cmd=sendfile");
				if (dirIstPdfFile) {
					dir.setBaseUri("https://ilias.studium.kit.edu/");
					final double size = new IliasConnector().requestHead(dir.attr("abs:href"));
					Adresse newPdfFile = createAdresse(kurs, dir, false, true, size);
					allPdfs.add(newPdfFile);

					List<Element> elemse = dir.parent().parent().siblingElements().select("div").select("span");
					for (Element el : elemse) {
						final boolean istUngelesen = el.attr("class").contains("il_ItemAlertProperty");
						if (istUngelesen) {
							newPdfFile.setGelesen(false);
						}
					}
				}

				final boolean linkToFolder = dir.attr("href").contains("goto_produktiv_fold_");
				if (linkToFolder) {
					LinkedList<Adresse> tempo = new LinkedList<Adresse>();
					Adresse newFolder = createAdresse(kurs, dir, true, false, 0.0);
					tempo.add(newFolder);
					allDirs.add(newFolder);
					// System.out.println("nächster Ordner: " +
					// newFolder.getName());
					findAllPdfs(tempo);
				}
			}
		}
		return allPdfs;
	}

	private void showInGui(Adresse kurs) {
		StringBuilder message = new StringBuilder();
		final Adresse parentFolder = kurs.getParentFolder();
		message.append("durchsuche Ordner ").append(kurs.getName());
		if (parentFolder != null) {
			message.append(" in ").append(parentFolder.getName());
		}
		loginLoader.changeStatusMessage(message.toString());
	}

	private Adresse createAdresse(Adresse kurs, Element dir, boolean folder, boolean pdf, double size) {
		dir.setBaseUri("https://ilias.studium.kit.edu/");
		final String fileName = dir.text();
		final String downloadLink = dir.attr("abs:href");
		Adresse newFileOrDir = new Adresse(fileName, downloadLink, kurs, folder, pdf, size);
		return newFileOrDir;
	}

	private List<Element> openFolder(Adresse kurs) {
		List<Element> directory;
		final String newHtmlContent = new IliasConnector().requestGet(kurs.getUrl());
		Document doc = Jsoup.parse(newHtmlContent);
		directory = doc.select("h4").select("a");
		return directory;
	}

	public List<Adresse> getAllPdfs() {
		return allPdfs;
	}

	public List<Adresse> getAllDirs() {
		return allDirs;
	}

}
