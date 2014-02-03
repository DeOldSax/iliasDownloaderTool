package iliasControl;

import javafx.application.Platform;
import model.Settings;
import studportControl.Studierendenportal;
import view.Dashboard;
import view.SettingsMenu;
import control.IliasTreeProvider;

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
			Dashboard.setSigInTransparent(true);
			Dashboard.fadeInLogin();
			Dashboard.setStatusText("Falsches Passwort!", true);
			return false;
		}
		Dashboard.setTitle("Ilias - Angemeldet als " + username);
		Dashboard.setStatusText("Angemeldet als: " + username, false);
		studierendenportal = new Studierendenportal(username, password);
		new Thread(studierendenportal).start();
		Settings.getInstance().setLogIn(true);
		Dashboard.showLoader(false);
		Dashboard.setSignInColor();
		if (!Settings.getInstance().autoUpdate()) {
			Dashboard.setStatusText("Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!", false);
		}
		Dashboard.setSigInTransparent(true);
		return true;
	}

	private boolean doLogin(String username, String password) {
		Ilias ilias = new Ilias();
		htmlContent = ilias.login(username, password);
		return htmlContent.equals("0");
	}

	public void loadIliasTree() {
		final IliasScraper Scraper = new IliasScraper();
		Scraper.run(htmlContent);
		while (Scraper.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!(Settings.getInstance().updateCanceled())) {
			IliasTreeProvider.setTree(Scraper.getIliasTree());
			Settings.getInstance().setUpdateCanceled(false);
		} else {
			Settings.getInstance().setUpdateCanceled(false);
			Dashboard.setStatusText("Aktualisierung abgebrochen.", false);
			return;
		}

		if (Settings.getInstance().localIliasPathIsAlreadySet()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Dashboard.iliasTreeReloaded(true);
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
