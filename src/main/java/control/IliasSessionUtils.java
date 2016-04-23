package control;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.*;

public class IliasSessionUtils {
	public void printCookies(DefaultHttpClient client) {
		int counter = 1;
		System.out.println("----------->Cookies: " + counter + ":");
		List<Cookie> cookies = client.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("\nNone");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("\n- " + cookies.get(i).toString());
			}
		}
		System.out.println("\n");
		counter++;
	}

	public static void printHeader(HttpResponse response) {
		Header[] h = response.getAllHeaders();
		for (int i = 0; i < h.length; i++) {
			System.out.println(h[i] + "\n");
		}
	}

	public static void getStatus(HttpResponse response) {
		System.out.println("\nStatus: " + response.getStatusLine() + "\n");
	}

	public void logout() {
		final String urlLogout = "https://ilias.studium.kit.edu/logout.php?lang=de";
		final String urlLogoutIdp = "https://idp.scc.kit.edu/idp/Logout";
		BasicHttpContext context = new BasicHttpContext();

		HttpGet request = new HttpGet(urlLogout);

		@SuppressWarnings("unused")
		HttpResponse response = null;
		try {
			response = IliasManager.getInstance().getIliasClient().execute(request, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		request.releaseConnection();

		request = new HttpGet(urlLogoutIdp);
		try {
			response = IliasManager.getInstance().getIliasClient().execute(request, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.releaseConnection();

		IliasManager.getInstance().getIliasClient().getConnectionManager().shutdown();
	}
}
