package control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.persistance.Settings;
import model.persistance.User;
import view.Dashboard;

public class LoginProvider implements EventHandler<ActionEvent> {

	private final TextField usernameField;
	private final PasswordField passwordField;
	private final RadioButton savePwd;
	private final Dashboard dashboard;

	public LoginProvider(Dashboard dashboard, TextField usernameField, PasswordField passwordField, RadioButton savePwd) {
		this.dashboard = dashboard;
		this.usernameField = usernameField;
		this.passwordField = passwordField;
		this.savePwd = savePwd;
	}

	@Override
	public void handle(ActionEvent event) {
		dashboard.setStatusText("", false);
		dashboard.showLoader(true);
		dashboard.setMenuTransparent(false);
		dashboard.setSigInTransparent(true);
		final String username = usernameField.getText();
		final boolean validUsername = username.length() == 5 || username.startsWith("u");
		if (!validUsername) {
			toggleDashboardLoginState("Ungültiger Benutzername");
			return;
		} else {
			final String password = passwordField.getText();
			if (password.length() < 1) {
				toggleDashboardLoginState("Ungültiges Passwort");
				return;
			}
			User user = Settings.getInstance().getUser();
			if (savePwd.isSelected()) {
				user.setName(username);
				user.setPassword(password);
			} else {
				user.setName("");
				user.setPassword("");
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					new IliasStarter(dashboard, username, password).login();
				}
			}).start();
		}
	}
	private void toggleDashboardLoginState(final String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dashboard.setStatusText(message, true);
				usernameField.requestFocus();
				usernameField.selectAll();
				dashboard.fadeInLogin();
				dashboard.showLoader(false);
			}
		});
	}
	
}