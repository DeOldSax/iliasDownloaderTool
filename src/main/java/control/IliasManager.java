package control;

import org.apache.http.impl.client.CloseableHttpClient;
import plugin.IliasPlugin;
import plugin.IliasPlugin.LoginStatus;
import plugin.KITIlias;
import plugin.TuebIlias;

public class IliasManager {
	private IliasPlugin ilias;
	private static IliasManager iliasManager;

	private IliasManager() {
		this.ilias = new KITIlias();
//		this.ilias = new TuebIlias();
	}

	public static IliasManager getInstance() {
		if (iliasManager == null) {
			iliasManager = new IliasManager();
		}
		return iliasManager;
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
