package control;

import javafx.application.*;
import model.persistance.*;

import org.apache.log4j.*;

import plugin.IliasPlugin.LoginStatus;
import plugin.*;
import view.*;

public class IliasStarter {
	private String username = null;
	private String password = null;
	private Logger LOGGER = Logger.getLogger(getClass());
	private Dashboard dashboard;

	public IliasStarter(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public IliasStarter(Dashboard dashboard, String username, String password) {
		this.dashboard = dashboard;
		this.username = username;
		this.password = password;
	}

	public boolean login() {
		LoginStatus loginStatusMessage = IliasManager.getInstance().login(username, password);
		if (loginStatusMessage.equals(LoginStatus.WRONG_PASSWORD)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dashboard.showLoader(false);
					dashboard.setSigInTransparent(true);
					dashboard.fadeInLogin();
					dashboard.setStatusText("Falsches Passwort!", true);
				}
			});
			return false;
		}
		if (loginStatusMessage.equals(LoginStatus.CONNECTION_FAILED)) {
			LOGGER.warn("Connection failed!");
			dashboard.setStatusText("Verbindung fehlgeschlagen!", true);
			dashboard.showLoader(false);
			return false;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.setTitle("Ilias - Angemeldet als " + username);
				dashboard.setStatusText("Angemeldet als: " + username, false);
			}
		});
		Settings.getInstance().getFlags().setLogin(true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.showLoader(false);
				dashboard.setSignInColor();
				if (!Settings.getInstance().getFlags().autoUpdate()) {
					dashboard
							.setStatusText(
									"Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!",
									false);
				}
				dashboard.setSigInTransparent(true);
			}
		});
		return true;
	}

	public void loadIliasTree() {
		final IliasScraper Scraper = new IliasScraper(dashboard);
		Scraper.run(IliasManager.getInstance().getDashboardHTML());
		while (Scraper.threadCount.get() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getStackTrace());
			}
		}
		Flags flags = Settings.getInstance().getFlags();
		if (!(flags.updateCanceled())) {
			IliasTreeProvider.setTree(Scraper.getIliasTree());
			flags.setUpdateCanceled(false);
		} else {
			flags.setUpdateCanceled(false);
			dashboard.setStatusText("Aktualisierung abgebrochen.", false);
			return;
		}

		if (flags.isLocalIliasPathStored()) {
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
				dashboard.showSettingsPrompt();
			}
		});
		dashboard.showLoader(false);
	}
}
