package studportControl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NameValuePairFactory {

	public List<NameValuePair> create(String username, String password) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("ctl00$PlaceHolderMain$Login$UserName", username));
		nvps.add(new BasicNameValuePair("ctl00$PlaceHolderMain$Login$password", password));
		nvps.add(new BasicNameValuePair("ctl00$PlaceHolderMain$Login$loginbutton", "Anmelden"));
		nvps.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUKMjAwOTU0MTUzNmRkGMZMzls3EXOMxXXupzS+b50SxJ4="));
		nvps.add(new BasicNameValuePair("__spDummyText2", ""));
		nvps.add(new BasicNameValuePair("__spDummyText1", ""));
		nvps.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWBAL65MrYDwKkvPT7CgKr+9XVAQLd5aKiC99IVOll/HVaIlq16JWHtOJnlcxm"));
		return nvps;
	}
}
