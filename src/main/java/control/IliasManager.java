package control;

import org.apache.http.impl.client.*;

import plugin.*;
import plugin.IliasPlugin.LoginStatus;

public class IliasManager {
	private IliasPlugin ilias;
	private static IliasManager iliasManager;

	private IliasManager() {
	}

	public static IliasManager getInstance() {
		if (iliasManager == null) {
			iliasManager = new IliasManager();
		}
		return iliasManager;
	}

	public void setIliasPlugin(IliasPlugin ilias) {
		this.ilias = ilias;
	}

	public LoginStatus login(String username, String password) {
		return this.ilias.login(username, password);
	}

	public String getDashboardHTML() {
		return this.ilias.getDashboardHTML();
	}

	public CloseableHttpClient getIliasClient() {
		return this.ilias.getClient();
	}

	public String getBaseUri() {
		return this.ilias.getBaseUri();
	}
}
