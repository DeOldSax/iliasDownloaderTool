package control;

import iliasControl.Ilias;
import iliasControl.IliasCourseFinder;
import iliasControl.IliasPdfFinder;

import java.util.List;

import javafx.application.Platform;
import model.Directory;
import model.PDF;
import model.StorageProvider;
import studportControl.Studierendenportal;
import view.Dashboard;
import view.SettingsMenu;

public class IliasStarter {

	private Ilias ilias;
	private String htmlContent;
	private List<Directory> kurse;
	private List<PDF> allPdfs;
	private final String username;
	private final String password;
	private List<Directory> allDirs;
	private Studierendenportal studierendenportal;

	public IliasStarter(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean login() {
		final boolean usernameOrPasswordWrong = doLogin(username, password);
		if (usernameOrPasswordWrong) {
			Dashboard.fadeInLogin();
			return false;
		}
		Dashboard.setTitle("Ilias - Angemeldet als: " + username + "@student.kit.edu");
		studierendenportal = new Studierendenportal(username, password);
		new Thread(studierendenportal).start();
		watchForFolders();
		Dashboard.showLoader(false);
		return true;
	}

	private boolean doLogin(String username, String password) {
		ilias = new Ilias();
		htmlContent = ilias.login(username, password);
		return htmlContent.equals("0");
	}

	private void watchForFolders() {
		final IliasPdfFinder iliasPdfFinder = new IliasPdfFinder();
		final IliasCourseFinder iliasCourseFinder = new IliasCourseFinder();
		kurse = iliasCourseFinder.getSubjects(htmlContent);
		iliasPdfFinder.findAllPdfs(kurse);
		allPdfs = iliasPdfFinder.getAllPdfs();
		FileSystem.setAllPdfFiles(allPdfs);
		while (iliasPdfFinder.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		FileSystem.setAllFiles(iliasPdfFinder.getKurse());

		if (new StorageProvider().localIliasPathIsAlreadySet()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Dashboard.update();
				}
			});
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				SettingsMenu.show();
			}
		});

		while (!studierendenportal.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		allDirs = iliasPdfFinder.getAllDirs();
	}

	public List<Directory> getKurse() {
		return kurse;
	}

	public List<PDF> getAllPdfs() {
		return allPdfs;
	}

	public List<Directory> getAllFolder() {
		return allDirs;
	}

	public String getUserName() {
		return username;
	}

	public Studierendenportal getStudierendenportal() {
		return studierendenportal;
	}
}
