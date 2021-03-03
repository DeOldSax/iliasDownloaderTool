package control;

import java.io.*;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

@Slf4j
public class IliasConnector {
	private HttpGet request;
	private HttpResponse response;
	private HttpEntity entity;
	private BasicHttpContext context;

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
			log.warn(e.getMessage(), e);
		}
		return html;
	}

	public int getFileSize(String url) {

		context = new BasicHttpContext();
		HttpHead head = new HttpHead(url);
		try {
			response = IliasManager.getInstance().getIliasClient().execute(head, context);
		} catch (HttpHostConnectException e) {
			log.warn("connection timout while fetching file size", e);
			return 0;
		} catch (ClientProtocolException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}

		int size = 0;
		if (response.containsHeader("Content-Length")) {
			Header[] fileSize = response.getHeaders("Content-Length");
			size = Integer.parseInt(fileSize[0].getValue());
		} else {
			log.warn("Headers: " + Arrays.toString(response.getAllHeaders()));
			log.warn("\nNo Filesize found for URL: " + url);
		}
		return size;
	}
}
