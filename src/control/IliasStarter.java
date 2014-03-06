package control;

import javafx.application.Platform;
import model.IliasTreeProvider;
import model.Settings;
import view.Dashboard;
import view.SettingsMenu;

public class IliasStarter {
	private static String loginStatusMessage;
	private String username = null;
	private String password = null;
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
		Ilias ilias = new Ilias();
		loginStatusMessage = ilias.login(username, password);
		if (loginStatusMessage.equals("0")) {
			dashboard.showLoader(false);
			dashboard.setSigInTransparent(true);
			dashboard.fadeInLogin();
			dashboard.setStatusText("Falsches Passwort!", true);
			return false;
		}
		if (loginStatusMessage.equals("1")) {
			System.out.println("connection failed");
			dashboard.setStatusText("Verbindung fehlgeschlagen!", true);
			dashboard.showLoader(false);
			return false;
		}
		dashboard.setTitle("Ilias - Angemeldet als " + username);
		dashboard.setStatusText("Angemeldet als: " + username, false);
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
		loginStatusMessage = ilias.login(username, password);
		return loginStatusMessage.equals("0");
	}

	public void loadIliasTree() {
		final IliasScraper Scraper = new IliasScraper(dashboard);
		Scraper.run(loginStatusMessage);
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
			dashboard.setStatusText("Aktualisierung abgebrochen.", false);
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
		dashboard.showLoader(false);
	}
}
