package analytics;

import control.IliasManager;
import control.VersionValidator;
import model.persistance.Settings;
import model.persistance.User;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.util.UUID;

public class AnalyticsLogger {

    public static String url = "https://www.iliasdownloadertool.de/api/v1/analytics";

    public static AnalyticsLogger logger;

    private final String sessionID;

    private final String iliasVersion;

    private final String userID;

    private final String iliasPortal;

    private AnalyticsLogger() {
        this.sessionID = generateSessionID();
        this.iliasVersion = new VersionValidator().getVersion();
        this.userID = generateUserID();
        this.iliasPortal = IliasManager.getInstance().getShortName();
    }

    public static AnalyticsLogger getInstance() {
        if (logger == null) {
            logger = new AnalyticsLogger();
        }
        return logger;
    }

    private String generateUserID() {
        String pluginShortName = IliasManager.getInstance().getShortName();
        User user = Settings.getInstance().getUser();

        if (user.getName() == null) {
            return hash("init");
        } else {
            String name = hash(user.getName());
            return hash(name + hash(pluginShortName) + hash("G(xpt+OgoLltz5b#e(Bu-YcYF$cokfmp9349fdjd!-4sdvf2"));
        }
    }

    private String generateSessionID() {
        return hash(UUID.randomUUID().toString());
    }

    public void log(Enum actionType) {
        new Thread(() -> {
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);

            SessionLog sessionLog = new SessionLog(this.userID, this.sessionID, actionType,
                    this.iliasVersion, this.iliasPortal);

            StringEntity entity;
            try {
                entity = new StringEntity(sessionLog.toJson());
                httpPost.setEntity(entity);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                client.execute(httpPost);
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String hash(String s) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] digest = digestSHA3.digest(s.getBytes());

        return Hex.toHexString(digest);
    }

}
