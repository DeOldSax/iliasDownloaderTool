package main;

import view.LoginWindow;

public class IliasDownloaderTool {
	public static void main(final String[] args) {

		new LoginWindow();

		boolean newVersionCalled = new VersionValidator().validate();
		if (newVersionCalled) {
			System.exit(0);
		}
	}
}
