package control;

import org.apache.http.impl.client.CloseableHttpClient;
import plugin.*;
import plugin.IliasPlugin.LoginStatus;

public class IliasManager {

	private IliasPlugin ilias;

	private static IliasManager iliasManager;

	private IliasManager() {
		// this.ilias = new KITIlias();
		// this.ilias = new StuggeIlias();
		// this.ilias = new TuebIlias();
		// this.ilias = new KNIlias();
		// this.ilias = new PHTGIlias();
		// this.ilias = new HSFIlias();
		this.ilias = new WBSIlias();
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

	public String getShortName() {
		return this.ilias.getShortName();
	}

}
