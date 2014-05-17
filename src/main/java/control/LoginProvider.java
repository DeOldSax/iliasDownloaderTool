package control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.Settings;
import view.Dashboard;

public class LoginProvider implements EventHandler<ActionEvent> {

	private final TextField usernameField;
	private final PasswordField passwordField;
	private final RadioButton savePwd;
	private final Settings settings;
	private final Dashboard dashboard;

	public LoginProvider(Dashboard dashboard, TextField usernameField, PasswordField passwordField, RadioButton savePwd) {
		this.dashboard = dashboard;
		this.usernameField = usernameField;
		this.passwordField = passwordField;
		this.savePwd = savePwd;
		this.settings = Settings.getInstance();
	}

	@Override
	public void handle(ActionEvent event) {
		dashboard.setStatusText("", false);
		dashboard.showLoader(true);
		dashboard.setMenuTransparent(false);
		dashboard.setSigInTransparent(true);
		final String username = usernameField.getText();
		final boolean validUsername = username.length() != 5 || !username.startsWith("u");
		if (validUsername) {
			dashboard.setStatusText("Ungültiger Benutzername", true);
			usernameField.requestFocus();
			usernameField.selectAll();
			dashboard.fadeInLogin();
			dashboard.showLoader(false);
			return;
		} else {
			final String password = passwordField.getText();
			if (password.length() < 1) {
				dashboard.setStatusText("Ungültiges Passwort", true);
				passwordField.requestFocus();
				passwordField.selectAll();
				dashboard.fadeInLogin();
				dashboard.showLoader(false);
				return;
			}
			if (savePwd.isSelected()) {
				settings.storeUsername(username);
				settings.storePassword(password);
			} else {
				settings.storeUsername("");
				settings.storePassword("");
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					new IliasStarter(dashboard, username, password).login();
				}
			}).start();
		}
	}
}