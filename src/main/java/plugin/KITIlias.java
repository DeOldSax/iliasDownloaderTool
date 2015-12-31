package plugin;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.log4j.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class KITIlias extends IliasPlugin {

	private final String urlLoginDialog = "https://ilias.studium.kit.edu/Shibboleth.sso/Login";
	private final String urlAuthnExtUp = "https://idp.scc.kit.edu/idp/Authn/ExtUP";
	private final String urlRedirect = "https://ilias.studium.kit.edu/Shibboleth.sso/SAML2/POST";

	private HttpPost postRequest;
	private HttpResponse response;
	private HttpEntity entity;
	private Logger LOGGER = Logger.getLogger(getClass());
	private String dashboardHTML;
	private BasicHttpContext context;

	@Override
	public LoginStatus login(String username, String password) {
		LoginStatus loginStatus = LoginStatus.CONNECTION_FAILED;
		context = new BasicHttpContext();

		postRequest = new HttpPost(urlLoginDialog);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("sendLogin", "1"));
		nvps.add(new BasicNameValuePair("idp_selection", "https://idp.scc.kit.edu/idp/shibboleth"));
		nvps.add(new BasicNameValuePair("target",
				"https://ilias.studium.kit.edu/shib_login.php?target="));
		nvps.add(new BasicNameValuePair("home_organization_selection", "Mit KIT-Account anmelden"));

		postRequest.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		try {
			client.execute(postRequest, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			postRequest.releaseConnection();
		}

		postRequest = new HttpPost(urlAuthnExtUp);

		List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
		nvps2.add(new BasicNameValuePair("j_username", username));
		nvps2.add(new BasicNameValuePair("j_password", password));

		postRequest.setEntity(new UrlEncodedFormEntity(nvps2, Consts.UTF_8));

		try {
			response = client.execute(postRequest, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			entity = response.getEntity();
		}

		String html = null;
		try {
			html = EntityUtils.toString(entity);
		} catch (ParseException e) {
			LOGGER.warn(e.getStackTrace());
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			postRequest.releaseConnection();
		}

		Document doc = Jsoup.parse(html);
		Element value1 = doc.select("input[name=RelayState]").first();
		Element value2 = doc.select("input[name=SAMLResponse]").first();

		// if password or username is wrong, value1 will be null
		if (value1 == null) {
			shutdown();
			return LoginStatus.WRONG_PASSWORD;
		}

		String v1 = value1.attr("value");
		String v2 = value2.attr("value");

		postRequest.releaseConnection();

		postRequest = new HttpPost(urlRedirect);

		List<NameValuePair> nvps3 = new ArrayList<NameValuePair>();
		nvps3.add(new BasicNameValuePair("RelayState", v1));
		nvps3.add(new BasicNameValuePair("SAMLResponse", v2));

		postRequest.setEntity(new UrlEncodedFormEntity(nvps3, Consts.UTF_8));

		try {
			response = client.execute(postRequest, context);
		} catch (IOException e) {
			LOGGER.warn(e.getStackTrace());
		}

		final HttpEntity entityX = response.getEntity();
		try {
			String htmlStartpage = EntityUtils.toString(entityX);
			if (htmlStartpage.equals("1")) {
				loginStatus = LoginStatus.CONNECTION_FAILED;
			} else {
				loginStatus = LoginStatus.SUCCESS;
				this.dashboardHTML = htmlStartpage;
			}
		} catch (ParseException | IOException e) {
			LOGGER.warn(e.getStackTrace());
		} finally {
			postRequest.releaseConnection();
		}

		postRequest.releaseConnection();
		return loginStatus;
	}

	@Override
	public String getBaseUri() {
		return "https://ilias.studium.kit.edu/";
	}

	@Override
	public String getDashboardHTML() {
		return this.dashboardHTML;
	}

}
