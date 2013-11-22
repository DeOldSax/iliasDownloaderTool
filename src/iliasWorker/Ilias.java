/**
 * Tools for Ilias!
 * 
 * @author David Englert
 * @version 27.07.2013
 *  
 */

package iliasWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import view.LoginLoader;

public class Ilias {

	private final String urlKitAnmeldung = "https://ilias.studium.kit.edu/login.php?target=&soap_pw=&ext_uid=&cookies=nocookies&client_id=produktiv&lang=de";
	private final String urlLoginDialog = "https://ilias.studium.kit.edu/Shibboleth.sso/Login";
	private final String urlAuthnExtUp = "https://idp.scc.kit.edu/idp/Authn/ExtUP";
	private final String urlRedirect = "https://ilias.studium.kit.edu/Shibboleth.sso/SAML2/POST";

	private static DefaultHttpClient client;
	private final BasicHttpContext context;
	private final RedirectStrategy strategy;
	private HttpGet request1;
	private HttpPost request2, request3, request4;
	private HttpResponse response1, response2, response3, response4, responseX;
	private HttpEntity entity1, entity2, entity3, entity4, entityX;
	private final LoginLoader loginLoader;

	public Ilias(LoginLoader loginLoader) {
		this.loginLoader = loginLoader;

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);

		// Http client
		client = new DefaultHttpClient(cm);

		// automate Redirectory
		strategy = new LaxRedirectStrategy();
		client.setRedirectStrategy(strategy);

		context = new BasicHttpContext();
	}

	public String login(String username, String password) {
		String htmlStartpage = null;

		// GET get the Cookies
		request1 = new HttpGet(urlKitAnmeldung);

		// Get methode ausführen
		try {
			response1 = client.execute(request1, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		entity1 = response1.getEntity();

		// request schließen
		request1.releaseConnection();

		// POST click the Button
		request2 = new HttpPost(urlLoginDialog);

		// ParamList for ButtonClick
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("sendLogin", "1"));
		nvps.add(new BasicNameValuePair("idp_selection", "https://idp.scc.kit.edu/idp/shibboleth"));
		nvps.add(new BasicNameValuePair("target", "https://ilias.studium.kit.edu/shib_login.php?target="));
		nvps.add(new BasicNameValuePair("home_organization_selection", "Mit KIT-Account anmelden"));

		// Liste an POST übergeben
		request2.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		loginLoader.changeStatusMessage("sende Logindaten...");

		// POST request ausführen
		try {
			response2 = client.execute(request2, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// request schließen
		request2.releaseConnection();

		// POST enter password and username
		request3 = new HttpPost(urlAuthnExtUp);

		// ParamList with "username" and "password"
		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(new BasicNameValuePair("j_username", username));
		nvps2.add(new BasicNameValuePair("j_password", password));

		// Liste an POST übergeben
		request3.setEntity(new UrlEncodedFormEntity(nvps2, Consts.UTF_8));

		loginLoader.changeStatusMessage("überprüfe Passwort...");

		// POST request ausführen
		try {
			response3 = client.execute(request3, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Post Request erhalten
		entity3 = response3.getEntity();

		// hier noch name und value für den letzten aufruf holen

		String html = null;
		try {
			html = EntityUtils.toString(entity3);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(html);
		Element value1 = doc.select("input[name=RelayState]").first();
		Element value2 = doc.select("input[name=SAMLResponse]").first();

		// überprüfen ob Password bzw Username richtig sind!!!!
		if (value1 == null) {
			shutdown();
			JOptionPane.showMessageDialog(null, "Login Fehlgeschlagen", null, JOptionPane.ERROR_MESSAGE);
			return "0";
		}

		String v1 = value1.attr("value");
		String v2 = value2.attr("value");

		// request schließen
		request3.releaseConnection();

		// /letzter Schritt, aus letztem entity aus dem script name und
		// value lesen und damit dann url4 Post aufrufen

		request4 = new HttpPost(urlRedirect);

		// Liste mit Parametern für den ButtenKlick
		List<NameValuePair> nvps3 = new ArrayList<NameValuePair>();
		nvps3.add(new BasicNameValuePair("RelayState", v1));
		nvps3.add(new BasicNameValuePair("SAMLResponse", v2));

		// Liste an POST übergeben
		request4.setEntity(new UrlEncodedFormEntity(nvps3, Consts.UTF_8));

		// POST request ausführen
		try {
			response4 = client.execute(request4, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Post Request erhalten
		entity4 = response4.getEntity();

		// System.out.println(entity4);
		try {
			htmlStartpage = EntityUtils.toString(entity4);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// request schließen
		request4.releaseConnection();

		return htmlStartpage;
	}

	public void shutdown() {
		client.getConnectionManager().shutdown();
	}

	public static DefaultHttpClient getClient() {
		DefaultHttpClient clientCloned = ClientCloner.cloneClient(client);
		return clientCloned;
	}
}
