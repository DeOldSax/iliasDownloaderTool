package control;

import org.apache.http.impl.client.CloseableHttpClient;
import plugin.*;
import plugin.IliasPlugin.LoginStatus;

import java.util.HashMap;

public class IliasManager {

	private IliasPlugin ilias;

	private static IliasManager iliasManager;

	private IliasManager() {
		String magicVariable = "kn";

		HashMap<String, IliasPlugin> map = new HashMap<>();
		map.put("kn", new KNIlias());
		map.put("kit", new KITIlias());
		map.put("demo", new DemoIlias());
		map.put("hsf", new HSFIlias());
		map.put("tueb", new TuebIlias());
		map.put("wbs", new WBSIlias());
		map.put("ube", new UniBernIlias());
		map.put("phtg", new PHTGIlias());
		map.put("stugge", new StuggeIlias());

		this.ilias = map.get(magicVariable);
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
