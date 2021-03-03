package plugin;

import java.io.*;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

@Slf4j
public class StuggeIlias extends IliasPlugin {

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
            post = new HttpPost("https://ilias3.uni-stuttgart.de/login.php");

            executePost();

            String html = null;
            try {
                html = EntityUtils.toString(entity);
            } catch (IOException | ParseException e) {
                log.warn(e.getMessage(), e);
            }

            Document doc = Jsoup.parse(html);
            Element form = doc.select("form[name=formlogin").first();

            post = new HttpPost("https://ilias3.uni-stuttgart.de/" + form.attr("action"));
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));
            nvps.add(new BasicNameValuePair("cmd[doStandardAuthentication]", "Anmelden"));
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
        return "https://ilias3.uni-stuttgart.de/";
    }

    @Override
    public String getDashboardHTML() {
        return this.dashboardHTML;
    }

    @Override
    public String getShortName() {
        return "STUGGE";
    }

}
