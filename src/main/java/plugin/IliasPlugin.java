package plugin;

import org.apache.http.client.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.*;

public abstract class IliasPlugin {

	public enum LoginStatus {
		WRONG_PASSWORD, CONNECTION_FAILED, SUCCESS
	};

	DefaultHttpClient client;
	private final RedirectStrategy strategy;

	public IliasPlugin() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);

		client = new DefaultHttpClient(cm);
		strategy = new LaxRedirectStrategy();
		client.setRedirectStrategy(strategy);
	}

	public final DefaultHttpClient getClient() {
		DefaultHttpClient clonedClient = new DefaultHttpClient();

		final CookieStore cookieStore = client.getCookieStore();
		for (Cookie cookie : cookieStore.getCookies()) {
			clonedClient.getCookieStore().addCookie(cookie);
		}

		return clonedClient;
	}

	public final void shutdown() {
		client.getConnectionManager().shutdown();
	}

	public abstract LoginStatus login(String username, String password);

	public abstract String getBaseUri();

	public abstract String getDashboardHTML();
}
