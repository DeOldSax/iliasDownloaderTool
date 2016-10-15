package plugin;

import java.io.IOException;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;

public abstract class IliasPlugin {

	public enum LoginStatus {
		WRONG_PASSWORD, CONNECTION_FAILED, SUCCESS
	};

	protected CloseableHttpClient client;
	private CookieStore cookieStore;

	public IliasPlugin() {
		cookieStore = new BasicCookieStore();

		client = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setDefaultCookieStore(cookieStore)
				.build();
	}

	public final CloseableHttpClient getClient() {
		CookieStore clonedCookieStore = new BasicCookieStore();

		for (Cookie cookie : this.cookieStore.getCookies()) {
			clonedCookieStore.addCookie(cookie);
		}

		return HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
	}

	public final void shutdown() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract LoginStatus login(String username, String password);

	public abstract String getBaseUri();

	public abstract String getDashboardHTML();
}
