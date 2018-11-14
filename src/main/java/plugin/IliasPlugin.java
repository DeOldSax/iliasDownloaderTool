package plugin;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContextBuilder;

import model.persistance.Settings;

public abstract class IliasPlugin {

	public enum LoginStatus {
		WRONG_PASSWORD, CONNECTION_FAILED, SUCCESS
	};

	protected CloseableHttpClient client;
	private CookieStore cookieStore;
	private SSLContext sslContext;

	public IliasPlugin() {
		newClient();
	}

	public final CloseableHttpClient getClient() {
		CookieStore clonedCookieStore = new BasicCookieStore();

		for (Cookie cookie : this.cookieStore.getCookies()) {
			clonedCookieStore.addCookie(cookie);
		}

		CloseableHttpClient clonedClient;

		if (!Settings.getInstance().getFlags().isConnectSecure()) {

			try {
				sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
			} catch (KeyManagementException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (KeyStoreException e1) {
				e1.printStackTrace();
			}

			clonedClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
					.setDefaultCookieStore(cookieStore).setSSLContext(sslContext)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		} else {
			clonedClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
					.setDefaultCookieStore(cookieStore).build();
		}

		return clonedClient;
	}

	public final void shutdown() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void newClient() {
		cookieStore = new BasicCookieStore();

		if (!Settings.getInstance().getFlags().isConnectSecure()) {

			try {
				sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
			} catch (KeyManagementException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (KeyStoreException e1) {
				e1.printStackTrace();
			}

			client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
					.setDefaultCookieStore(cookieStore).setSSLContext(sslContext)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		} else {
			client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy())
					.setDefaultCookieStore(cookieStore).build();

		}

	}

	public abstract LoginStatus login(String username, String password);

	public abstract String getBaseUri();

	public abstract String getDashboardHTML();
}
