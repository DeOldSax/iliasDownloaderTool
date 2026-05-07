package plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class FHDortmundIlias extends IliasPlugin {

    private HttpPost post;
    private HttpResponse response;
    private HttpEntity entity;
    private String dashboardHTML;
    private BasicHttpContext context;
    private List<NameValuePair> nvps;

    @Override
    public LoginStatus login(String username, String password) {
        LoginStatus loginStatus = LoginStatus.CONNECTION_FAILED;
        context = new BasicHttpContext();
        nvps = new ArrayList<>();

        try {
            HttpGet get = new HttpGet(getBaseUri() + "/login.php?client_id=ilias-fhdo&cmd=force_login&lang=en");

            executeGet(get);

            String html = null;
            try {
                html = EntityUtils.toString(entity);
            } catch (IOException | ParseException e) {
                log.warn(e.getMessage(), e);
            }

            Document doc = Jsoup.parse(html);
            Element form = doc.select("form[name='login_form']").first();
            Elements labels = form.select("label[for]");
            AtomicReference<String> usernameKey = new AtomicReference<>("");
            AtomicReference<String> passwordKey = new AtomicReference<>("");
            labels.forEach((Element label) ->{
                if (label.text().equals("Anmeldename*") || label.text().equals("Username*")) {
                    usernameKey.set(form.select("input[id='" + label.attr("for") + "']").first().attr("name"));
                } else if (label.text().equals("Passwort*") || label.text().equals("Password*")) {
                    passwordKey.set(form.select("input[id='" + label.attr("for") + "']").first().attr("name"));
                }
            });

            post = new HttpPost(getBaseUri() + "/" + form.attr("action"));
            nvps.add(new BasicNameValuePair(usernameKey.get(), username));
            nvps.add(new BasicNameValuePair(passwordKey.get(), password));
            //nvps.add(new BasicNameValuePair("cmd[doStandardAuthentication]", "Login"));
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
                log.warn(e.getMessage(), e);
            }
        } finally {
            post.releaseConnection();
        }

        return loginStatus;
    }

    private void executeGet(HttpGet get) {
        try {
            this.response = this.client.execute(get, this.context);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.warn(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn(e.getMessage(), e);
        } finally {
            this.entity = this.response.getEntity();
        }

        this.nvps.clear();
    }

    private void executePost() {
        try {
            this.response = this.client.execute(this.post, this.context);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.warn(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn(e.getMessage(), e);
        } finally {
            this.entity = this.response.getEntity();
        }

        this.nvps.clear();
    }

    @Override
    public String getBaseUri() {
        return "https://www.ilias.fh-dortmund.de/ilias";
    }

    @Override
    public String getDashboardHTML() {
        return this.dashboardHTML;
    }

    @Override
    public String getShortName() {
        return "fhdo";
    }
}
