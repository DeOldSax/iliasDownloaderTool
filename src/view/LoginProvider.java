package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import javax.swing.JOptionPane;

import model.StorageProvider;
import control.IliasStarter;

public class LoginProvider implements EventHandler<ActionEvent> {

	private final TextField usernameField;
	private final PasswordField passwordField;
	private final RadioButton savePwd;
	private final StorageProvider storageProvider;

	public LoginProvider(TextField usernameField, PasswordField passwordField, RadioButton savePwd) {
		this.usernameField = usernameField;
		this.passwordField = passwordField;
		this.savePwd = savePwd;
		this.storageProvider = new StorageProvider();
	}

	@Override
	public void handle(ActionEvent event) {
		Dashboard.showLoader(true);
		Dashboard.setMenuTransparent(false);
		final String username = usernameField.getText();
		final boolean validUsername = username.length() != 5 || !username.startsWith("u");
		if (validUsername) {
			JOptionPane.showMessageDialog(null, "ungültiger Benutzername", null, JOptionPane.ERROR_MESSAGE);
			usernameField.requestFocus();
			usernameField.selectAll();
			return;
		} else {
			final String password = passwordField.getText();
			if (password.length() < 1) {
				JOptionPane.showMessageDialog(null, "ungültiges Passwort", null, JOptionPane.ERROR_MESSAGE);
				passwordField.requestFocus();
				passwordField.selectAll();
				return;
			}
			if (savePwd.isSelected()) {
				storageProvider.storeUsername(username);
				storageProvider.storePassword(password);
			} else {
				storageProvider.storeUsername("");
				storageProvider.storePassword("");
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					new IliasStarter(username, password).login();
				}
			}).start();
		}
	}
}