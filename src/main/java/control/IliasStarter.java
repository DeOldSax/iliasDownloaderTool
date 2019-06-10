package control;

import analytics.ActionType;
import analytics.AnalyticsLogger;
import javafx.application.Platform;
import model.persistance.Flags;
import model.persistance.IliasTreeProvider;
import model.persistance.Settings;
import org.apache.log4j.Logger;
import plugin.IliasPlugin.LoginStatus;
import view.Dashboard;

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
            Platform.runLater(() -> {
                dashboard.showLoader(false);
                dashboard.setSigInTransparent(true);
                dashboard.fadeInLogin();
                Dashboard.setStatusText("Falsches Passwort!", true);
            });
            return false;
        }
        if (loginStatusMessage.equals(LoginStatus.CONNECTION_FAILED)) {
            LOGGER.warn("Connection failed!");
            Dashboard.setStatusText("Verbindung fehlgeschlagen!", true);
            dashboard.showLoader(false);
            return false;
        }
        Platform.runLater(() -> {
            dashboard.setTitle("Ilias - Angemeldet als " + username);
            Dashboard.setStatusText("Angemeldet als: " + username, false);
        });
        Settings.getInstance().getFlags().setLogin(true);
        Platform.runLater(() -> {
            dashboard.showLoader(false);
            dashboard.setSignInColor();
            if (!Settings.getInstance().getFlags().autoUpdate()) {
                Dashboard.setStatusText(
                                "Aktualisiere über den Button in der Menüleiste die Kurse auf deinem Schreibtisch!",
                                false);
            }
            dashboard.setSigInTransparent(true);
        });
        return true;
    }

    public void loadIliasTree() {
        AnalyticsLogger.getInstance().log(ActionType.SYNCHRONIZE);

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
            Dashboard.setStatusText("Aktualisierung abgebrochen.", false);
            return;
        }

        if (flags.isLocalIliasPathStored()) {
            Platform.runLater(() -> {
                dashboard.iliasTreeReloaded(true);
                dashboard.showLoader(false);
            });
            return;
        }
        Platform.runLater(() -> dashboard.showSettingsPrompt());
        dashboard.showLoader(false);
    }
}
