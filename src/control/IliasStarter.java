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
	private static String htmlContent;
	private String username = null;
	private String password = null;
	private static Studierendenportal studierendenportal;

	public IliasStarter() {

	}

	public IliasStarter(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean login() {
		final boolean usernameOrPasswordWrong = doLogin(username, password);
		if (usernameOrPasswordWrong) {
			Dashboard.showLoader(false);
			Dashboard.fadeInLogin();
			Dashboard.setStatusText("Falsches Passwort!", true);
			return false;
		}
		Dashboard.setTitle("Ilias - Angemeldet als: " + username);
		Dashboard.setStatusText("Angemeldet als: " + username, false);
		studierendenportal = new Studierendenportal(username, password);
		new Thread(studierendenportal).start();
		new StorageProvider().setLogIn(true);
		Dashboard.showLoader(false);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Dashboard.setStatusText("Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!", false);
		return true;
	}

	private boolean doLogin(String username, String password) {
		Ilias ilias = new Ilias();
		htmlContent = ilias.login(username, password);
		return htmlContent.equals("0");
	}

	public void watchForFolders() {
		List<Directory> kurse;
		List<PDF> allPdfs;
		final IliasPdfFinder iliasPdfFinder = new IliasPdfFinder();
		final IliasCourseFinder iliasCourseFinder = new IliasCourseFinder();
		kurse = iliasCourseFinder.getSubjects(htmlContent);
		iliasPdfFinder.findAllPdfs(kurse);
		allPdfs = iliasPdfFinder.getAllPdfs();
		while (iliasPdfFinder.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!(new StorageProvider().updateCanceled())) {
			FileSystem.setAllPdfFiles(allPdfs);
			FileSystem.setAllFiles(iliasPdfFinder.getKurse());
		}
		new StorageProvider().setUpdateCanceled(false);

		if (new StorageProvider().localIliasPathIsAlreadySet()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Dashboard.update();
					Dashboard.showLoader(false);
				}
			});
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				SettingsMenu.show();
				SettingsMenu.activatePromptUpdater();
			}
		});

		while (!studierendenportal.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Dashboard.showLoader(false);
	}
}
