package plugin;

import org.apache.http.*;
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

public class PHTGIlias extends IliasPlugin {
    private HttpPost post;
    private HttpResponse response;
    private HttpEntity entity;
    private Logger LOGGER = Logger.getLogger(getClass());
    private String dashboardHTML;
    private BasicHttpContext context;
    private List<NameValuePair> nvps;

    @Override
    public IliasPlugin.LoginStatus login(String username, String password) {
        IliasPlugin.LoginStatus loginStatus = IliasPlugin.LoginStatus.CONNECTION_FAILED;
        context = new BasicHttpContext();
        nvps = new ArrayList<>();

        try {
            post = new HttpPost("https://ilias.phtg.ch/login.php");

            executePost();

            String html = null;
            try {
                html = EntityUtils.toString(entity);
            } catch (IOException | ParseException e) {
                LOGGER.warn(e.getStackTrace());
            }

            Document doc = Jsoup.parse(html);
            Element form = doc.select("form[name=formlogin").first();

            post = new HttpPost("https://ilias.phtg.ch/" + form.attr("action"));
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));
            nvps.add(new BasicNameValuePair("cmd[doStandardAuthentication]", "Anmelden"));
            post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

            executePost();

            try {
                String htmlStartpage = EntityUtils.toString(entity);
                if (htmlStartpage.equals("1")) {
                    loginStatus = IliasPlugin.LoginStatus.CONNECTION_FAILED;
                } else {
                    loginStatus = IliasPlugin.LoginStatus.SUCCESS;
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
        return "https://ilias.phtg.ch";
    }

    @Override
    public String getDashboardHTML() {
        return this.dashboardHTML;
    }

    @Override
    public String getShortName() {
        return "PHTG";
    }
    
}
