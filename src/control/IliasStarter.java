package control;

import javafx.application.Platform;
import model.IliasTreeProvider;
import model.Settings;
import studportControl.Studierendenportal;
import view.Dashboard;
import view.SettingsMenu;

public class IliasStarter {
	private static String htmlContent;
	private String username = null;
	private String password = null;
	private static Studierendenportal studierendenportal;
	private final Dashboard dashboard;

	public IliasStarter(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public IliasStarter(Dashboard dashboard, String username, String password) {
		this.dashboard = dashboard;
		this.username = username;
		this.password = password;
	}

	public boolean login() {
		final boolean usernameOrPasswordWrong = doLogin(username, password);
		if (usernameOrPasswordWrong) {
			dashboard.showLoader(false);
			dashboard.setSigInTransparent(true);
			dashboard.fadeInLogin();
			dashboard.setStatusText("Falsches Passwort!", true);
			return false;
		}
		dashboard.setTitle("Ilias - Angemeldet als " + username);
		dashboard.setStatusText("Angemeldet als: " + username, false);
		studierendenportal = new Studierendenportal(username, password);
		new Thread(studierendenportal).start();
		Settings.getInstance().setLogIn(true);
		dashboard.showLoader(false);
		dashboard.setSignInColor();
		if (!Settings.getInstance().autoUpdate()) {
			dashboard.setStatusText("Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!", false);
		}
		dashboard.setSigInTransparent(true);
		return true;
	}

	private boolean doLogin(String username, String password) {
		Ilias ilias = new Ilias();
		htmlContent = ilias.login(username, password);
		return htmlContent.equals("0");
	}

	public void loadIliasTree() {
		final IliasScraper Scraper = new IliasScraper(dashboard);
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
					dashboard.iliasTreeReloaded(true);
					dashboard.showLoader(false);
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
		dashboard.showLoader(false);
	}
}
