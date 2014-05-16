package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	private HttpResponse response3, response4;
	private HttpEntity entity;
	private Logger LOGGER = Logger.getLogger(getClass());

	public Ilias() {

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);

		client = new DefaultHttpClient(cm);

		strategy = new LaxRedirectStrategy();
		client.setRedirectStrategy(strategy);

		context = new BasicHttpContext();
	}

	public String login(String username, String password) {
		String htmlStartpage = null;

		request1 = new HttpGet(urlKitAnmeldung);

		try {
			client.execute(request1, context);
		} catch (IOException e1) {
			shutdown();
			return "1";
		} finally {
			request1.releaseConnection();
		}

		request2 = new HttpPost(urlLoginDialog);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("sendLogin", "1"));
		nvps.add(new BasicNameValuePair("idp_selection", "https://idp.scc.kit.edu/idp/shibboleth"));
		nvps.add(new BasicNameValuePair("target", "https://ilias.studium.kit.edu/shib_login.php?target="));
		nvps.add(new BasicNameValuePair("home_organization_selection", "Mit KIT-Account anmelden"));

		request2.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		try {
			client.execute(request2, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			request2.releaseConnection();
		}

		request3 = new HttpPost(urlAuthnExtUp);

		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(new BasicNameValuePair("j_username", username));
		nvps2.add(new BasicNameValuePair("j_password", password));

		request3.setEntity(new UrlEncodedFormEntity(nvps2, Consts.UTF_8));

		try {
			response3 = client.execute(request3, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			entity = response3.getEntity();
		}

		String html = null;
		try {
			html = EntityUtils.toString(entity);
		} catch (ParseException e) {
			LOGGER.warn(e.getStackTrace());
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			request3.releaseConnection();
		}

		Document doc = Jsoup.parse(html);
		Element value1 = doc.select("input[name=RelayState]").first();
		Element value2 = doc.select("input[name=SAMLResponse]").first();

		// if password or username is wrong, value1 will be null
		if (value1 == null) {
			shutdown();
			return "0";
		}

		String v1 = value1.attr("value");
		String v2 = value2.attr("value");

		request3.releaseConnection();

		request4 = new HttpPost(urlRedirect);

		List<NameValuePair> nvps3 = new ArrayList<NameValuePair>();
		nvps3.add(new BasicNameValuePair("RelayState", v1));
		nvps3.add(new BasicNameValuePair("SAMLResponse", v2));

		request4.setEntity(new UrlEncodedFormEntity(nvps3, Consts.UTF_8));

		try {
			response4 = client.execute(request4, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		}

		final HttpEntity entityX = response4.getEntity();
		try {
			htmlStartpage = EntityUtils.toString(entityX);
		} catch (ParseException | IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			request4.releaseConnection();
		}

		request4.releaseConnection();
		return htmlStartpage;
	}

	public void shutdown() {
		client.getConnectionManager().shutdown();
	}

	public static DefaultHttpClient getClient() {
		DefaultHttpClient clientCloned = IliasClientCloner.cloneClient(client);
		return clientCloned;
	}
}
