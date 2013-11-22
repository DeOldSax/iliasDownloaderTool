package iliasWorker;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class ClientCloner {

	public static DefaultHttpClient cloneClient(DefaultHttpClient oldClient) {
		DefaultHttpClient clonedClient = new DefaultHttpClient();

		final CookieStore cookieStore = oldClient.getCookieStore();
		for (Cookie cookie : cookieStore.getCookies()) {
			clonedClient.getCookieStore().addCookie(cookie);
		}

		return clonedClient;
	}
}
