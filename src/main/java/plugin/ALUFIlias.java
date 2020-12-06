package plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ALUFIlias extends IliasPlugin {
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
            post = new HttpPost("https://ilias.uni-freiburg.de/shib_login.php");

            executePost();

            String html = null;
            try {
                html = EntityUtils.toString(entity);
            } catch (IOException | ParseException e) {
                log.warn(e.getMessage(), e);
            }

            Document doc = Jsoup.parse(html);
            Element form = doc.select("form[id=login-form").first();
            String hiddenContext = form.select("input[type=hidden]").attr("value");

            post = new HttpPost("https://mylogin.ub.uni-freiburg.de" + form.attr("action"));
            nvps.add(new BasicNameValuePair("LoginForm[context]", hiddenContext));
            nvps.add(new BasicNameValuePair("LoginForm[username]", username));
            nvps.add(new BasicNameValuePair("LoginForm[password]", password));
            nvps.add(new BasicNameValuePair("yt0", "Login"));
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
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            this.entity = this.response.getEntity();
        }

        this.nvps.clear();
    }

    @Override
    public String getBaseUri() {
        return "https://ilias.uni-freiburg.de";
    }

    @Override
    public String getDashboardHTML() {
        return this.dashboardHTML;
    }

    @Override
    public String getShortName() {
        return "ALUF";
    }
}
