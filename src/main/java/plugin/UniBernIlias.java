package plugin;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UniBernIlias extends IliasPlugin {

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
		nvps = new ArrayList<>();

		try {
			post = new HttpPost("https://wayf.switch.ch/SWITCHaai/WAYF?entityID=https%3A%2F%2Filias.unibe.ch%2Fshibboleth&return=https%3A%2F%2Filias.unibe.ch%2FShibboleth.sso%2FLogin%3FSAMLDS%3D1%26target%3Dhttps%253A%252F%252Filias.unibe.ch%252Fshib_login.php%253Ftarget%253D");
			nvps.add(new BasicNameValuePair("request_type", "embedded"));
			nvps.add(new BasicNameValuePair("user_idp", "https://aai-idp.unibe.ch/idp/shibboleth"));
			nvps.add(new BasicNameValuePair("Login", "Anmelden"));
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			executePost();

			String html = null;
			try {
				html = EntityUtils.toString(entity);
			} catch (IOException | ParseException e) {
				LOGGER.warn(e.getStackTrace());
			}

			Document doc = Jsoup.parse(html);
			Element form = doc.select("form[action*=idp").first();

			post = new HttpPost("https://aai-idp.unibe.ch" + form.attr("action"));
			nvps.add(new BasicNameValuePair("_eventId_proceed", ""));
			nvps.add(new BasicNameValuePair("j_username", username));
			nvps.add(new BasicNameValuePair("j_password", password));
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			executePost();

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

			post = new HttpPost("https://ilias.unibe.ch/Shibboleth.sso/SAML2/POST");
			nvps.add(new BasicNameValuePair("RelayState", relayStateValue));
			nvps.add(new BasicNameValuePair("SAMLResponse", samlResponseValue));
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			executePost();

			String htmlStartpage;

			try {
				htmlStartpage = EntityUtils.toString(entity);
				if (htmlStartpage.equals("1")) {
					loginStatus = LoginStatus.CONNECTION_FAILED;
				} else {
					loginStatus = LoginStatus.SUCCESS;
				}
			} catch (ParseException | IOException e) {
				LOGGER.warn(e.getStackTrace());
			}

			post = new HttpPost("https://ilias.unibe.ch/ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems");

			executePost();

			try {
				htmlStartpage = EntityUtils.toString(entity);

				this.dashboardHTML = htmlStartpage;
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
		return "https://ilias.unibe.ch/";
	}

	@Override
	public String getDashboardHTML() {
		return this.dashboardHTML;
	}

	@Override
	public String getShortName() {
		return "UBE";
	}

}
