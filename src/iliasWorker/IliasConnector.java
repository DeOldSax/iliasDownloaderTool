package iliasWorker;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

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

			final HttpClient client = Ilias.getClient();
			response = client.execute(request, context);
			entity = response.getEntity();

			html = EntityUtils.toString(entity);
			request.releaseConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}

	public int requestHead(String url) {
		context = new BasicHttpContext();
		HttpHead head = new HttpHead(url);
		try {
			response = Ilias.getClient().execute(head, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Header[] pdfSize = response.getHeaders("Content-Length");
		final int size = Integer.parseInt(pdfSize[0].getValue());
		return size;
	}
}
