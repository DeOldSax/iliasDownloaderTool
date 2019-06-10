package analytics;

import control.VersionValidator;
import org.json.JSONObject;
import plugin.IliasPlugin;

public class SessionLog {

    /**
     * hash value to identify the user
     */
    private String userID;

    /**
     * unique id which indicates a session
     */
    private String sessionID;

    /**
     * one of {@link ActionType}
     */
    private Enum actionType;

    /**
     * {@link VersionValidator#getVersion()}
     */
    private String iliasVersion;

    /**
     * connected ilias portal defined in {@link IliasPlugin#getShortName()}
     */
    private String iliasPortal;

    public SessionLog(String userID, String sessionID, Enum actionType, String iliasVersion, String iliasPortal) {
        this.userID = userID;
        this.sessionID = sessionID;
        this.actionType = actionType;
        this.iliasVersion = iliasVersion;
        this.iliasPortal = iliasPortal;
    }

    public String toJson() {
        return new JSONObject()
            .put("user_id", this.userID)
            .put("session_id", this.sessionID)
            .put("action_type", actionType)
            .put("ilias_version", this.iliasVersion)
            .put("ilias_portal", this.iliasPortal)
            .toString();
    }

}
