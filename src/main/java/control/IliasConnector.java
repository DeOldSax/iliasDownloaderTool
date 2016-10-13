package control;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
import org.apache.log4j.*;

public class IliasConnector {
	private HttpGet request;
	private HttpResponse response;
	private HttpEntity entity;
	private BasicHttpContext context;
	private Logger LOGGER = Logger.getLogger(getClass());

	public String requestGet(String url) {
		context = new BasicHttpContext();
		String html = null;
		try {
			request = new HttpGet(url);

			final HttpClient client = IliasManager.getInstance().getIliasClient();
			response = client.execute(request, context);
			entity = response.getEntity();

			html = EntityUtils.toString(entity);
			request.releaseConnection();

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn(e.getStackTrace());
		}
		return html;
	}

	public int getFileSize(String url) {
		context = new BasicHttpContext();
		HttpHead head = new HttpHead(url);
		try {
			response = IliasManager.getInstance().getIliasClient().execute(head, context);
		} catch (ClientProtocolException e) {
			LOGGER.warn("", e);
		} catch (IOException e) {
			LOGGER.warn("", e);
		}

		int size = 0;
		if (response.containsHeader("Content-Length")) {
			Header[] fileSize = response.getHeaders("Content-Length");
			size = Integer.parseInt(fileSize[0].getValue());
		} else {
			LOGGER.warn("Headers: " + Arrays.toString(response.getAllHeaders()));
			LOGGER.warn("\nNo Filesize found for URL: " + url);
		}
		return size;
	}
}
