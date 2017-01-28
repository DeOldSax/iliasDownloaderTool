package plugin;

import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.log4j.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class TuebIlias extends IliasPlugin {
	private HttpPost post;
	private HttpResponse response;
	private HttpEntity entity;
	private Logger LOGGER = Logger.getLogger(getClass());
	private String dashboardHTML;
	private BasicHttpContext context;
	private List<NameValuePair> nvps;

	@Override
	public LoginStatus login(String username, String password) {
		LoginStatus loginStatus = LoginStatus.CONNECTION_FAILED;
		context = new BasicHttpContext();
		nvps = new ArrayList<NameValuePair>();
		try {
			post = new HttpPost("https://ovidius.uni-tuebingen.de/ilias3/shib_login.php?target=");
			
			executePost();
			String html = null;
			try {
				html = EntityUtils.toString(entity);
			} catch (IOException | ParseException e) {
				LOGGER.warn(e.getStackTrace());
			}

			Document doc = Jsoup.parse(html);
			Element form = doc.select("form[action*=idp").first();
			Element input_tag = form.select("input[name='lt']").first();
			String ltKey = input_tag.attr("value");
			
			post = new HttpPost("https://idp.uni-tuebingen.de" + form.attr("action"));
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			
			nvps.add(new BasicNameValuePair("lt", ltKey));
			nvps.add(new BasicNameValuePair("execution", "e1s1"));
			nvps.add(new BasicNameValuePair("_eventId", "submit"));
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			executePost();
			html = null;
			try {
				html = EntityUtils.toString(entity);
			} catch (IOException | ParseException e) {
				LOGGER.warn(e.getStackTrace());
			}
			

			doc = Jsoup.parse(html);
			Element relayState = doc.select("input[name=RelayState]").first();
			Element samlResponse = doc.select("input[name=SAMLResponse]").first();

			// if password or username is wrong, value1 will be null
			if (relayState == null) {
				shutdown();
				return LoginStatus.WRONG_PASSWORD;
			}

			String relayStateValue = relayState.attr("value");
			String samlResponseValue = samlResponse.attr("value");

			post = new HttpPost("https://ovidius.uni-tuebingen.de/Shibboleth.sso/SAML2/POST");
			nvps.add(new BasicNameValuePair("RelayState", relayStateValue));
			nvps.add(new BasicNameValuePair("SAMLResponse", samlResponseValue));
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			executePost();

			try {
				String htmlStartpage = EntityUtils.toString(entity);
				if (htmlStartpage.equals("1")) {
					loginStatus = LoginStatus.CONNECTION_FAILED;
				} else {
					loginStatus = LoginStatus.SUCCESS;
					this.dashboardHTML = htmlStartpage;
				}
			} catch (ParseException | IOException e) {
				LOGGER.warn(e.getStackTrace());
			}
		} finally {
			post.releaseConnection();
	}

		return loginStatus;
	}

	private void executePost() {
		try {
			this.response = this.client.execute(this.post, this.context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			this.LOGGER.warn(e.getStackTrace());
		} catch (IOException e) {
			e.printStackTrace();
			this.LOGGER.warn(e.getStackTrace());
		} finally {
			this.entity = this.response.getEntity();
		}

		this.nvps.clear();
	}

	@Override
	public String getBaseUri() {
		return "https://ovidius.uni-tuebingen.de/ilias3/";
	}

	@Override
	public String getDashboardHTML() {
		return this.dashboardHTML;
	}
}
