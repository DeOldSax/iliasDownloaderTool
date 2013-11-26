package worker;

import iliasWorker.Ilias;
import iliasWorker.IliasCourseFinder;
import iliasWorker.IliasPdfFinder;

import java.awt.EventQueue;
import java.util.List;

import model.Directory;
import model.PDF;
import view.LocalFolderService;
import view.LoginLoader;
import view.LoginWindow;

public class IliasStarter implements Runnable {

	private Ilias ilias;
	private String htmlContent;
	private List<Directory> kurse;
	private List<PDF> allPdfs;
	private final String username;
	private final String password;
	private LoginLoader loginLoader;
	private List<Directory> allDirs;

	public IliasStarter(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		work();
	}

	public void work() {
		loginLoader = new LoginLoader();
		final boolean loginFailed = doLogin(username, password);
		if (loginFailed) {
			loginLoader.stopRunning();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					new LoginWindow();
				}
			});
			try {
				this.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return;
		}
		watchForFolders();
	}

	private boolean doLogin(String username, String password) {
		ilias = new Ilias(loginLoader);
		htmlContent = ilias.login(username, password);
		return htmlContent.equals("0");
	}

	private void watchForFolders() {
		final IliasPdfFinder iliasPdfFinder = new IliasPdfFinder();
		final IliasCourseFinder iliasCourseFinder = new IliasCourseFinder();
		kurse = iliasCourseFinder.getSubjects(htmlContent);
		loginLoader.changeStatusMessage(kurse.size() + " Kurse auf Schreibtisch gefunden...");
		iliasPdfFinder.findAllPdfs(kurse);
		allPdfs = iliasPdfFinder.getAllPdfs();
		while (iliasPdfFinder.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		allDirs = iliasPdfFinder.getAllDirs();
		final LocalFolderService localFolderPath = new LocalFolderService(this);
		loginLoader.stopRunning();
		localFolderPath.showDialog();
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
}
