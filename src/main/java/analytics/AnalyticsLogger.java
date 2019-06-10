package analytics;

import control.IliasManager;
import control.VersionValidator;
import model.persistance.Settings;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.util.UUID;

public class AnalyticsLogger {

    public static AnalyticsLogger logger;

    private final String sessionID;

    private final String iliasVersion;

    private final String userID;

    private final String iliasPortal;

    public AnalyticsLogger() {
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
        String name = Settings.getInstance().getUser().getName();
        String shortName = IliasManager.getInstance().getShortName();

        return hash(hash(name) + hash(shortName) + hash("G(xpt+OgoLltz5b#e(Bu-YcYF$cokfmp9349fdjd!-4sdvf2"));
    }

    private String generateSessionID() {
        return hash(UUID.randomUUID().toString());
    }

    public void log(Enum actionType) {
        new Thread(() -> {
            CloseableHttpClient client = HttpClients.createDefault();
            String uri = "http://localhost:5000/api/v1/analytics";
            String uri2 = "https://www.iliasdownloadertool.de/api/v1/analytics";
            HttpPost httpPost = new HttpPost(uri2);

            SessionLog sessionLog = new SessionLog(this.userID, this.sessionID, actionType,
                    this.iliasVersion, this.iliasPortal);

            StringEntity entity = null;
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
