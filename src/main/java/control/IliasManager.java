package control;

import analytics.AnalyticsLogger;
import lombok.extern.slf4j.Slf4j;
import model.persistance.Plugin;
import model.persistance.Settings;
import org.apache.http.impl.client.CloseableHttpClient;
import plugin.*;
import plugin.IliasPlugin.LoginStatus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class IliasManager {

	private IliasPlugin ilias;

	private static IliasManager iliasManager;

	private IliasManager() {
        Plugin plugin = Settings.getInstance().getPlugin();

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
        map.put("fhdo", new FHDortmundIlias());
		this.ilias = map.get(plugin.getName() == null ? "kn" : plugin.getName());
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
