package model;

import iliasWorker.Ilias;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;

public class GetThread extends Thread {

	DefaultHttpClient client;
	BasicHttpContext context;
	HttpHead head;
	String path;
	FileInputStream pdfHash;
	HttpGet request;
	HttpEntity entity;
	HttpResponse response;
	BasicCookieStore cookiestore;
	LaxRedirectStrategy strategy;
	private final Directory adresse;
	private final List<Integer> localData;

	public GetThread(Directory adresse, List<Integer> localData) {
		this.adresse = adresse;
		this.localData = localData;
		this.context = new BasicHttpContext();
		this.head = new HttpHead(adresse.getUrl());
		this.path = "C:/Users/sdsd/Desktop";
	}

	@Override
	public void run() {
		// ID --> txt Datei auf dem Rechner

		client = new DefaultHttpClient();

		strategy = new LaxRedirectStrategy();
		client.setRedirectStrategy(strategy);

		context = new BasicHttpContext();
		cookiestore = new BasicCookieStore();

		List<Cookie> tempCookies = Ilias.getClient().getCookieStore().getCookies();

		Cookie[] a = new Cookie[tempCookies.size()];
		for (int i = 0; i < tempCookies.size(); i++) {
			a[i] = tempCookies.get(i);
		}

		cookiestore.addCookies(a);
		context.setAttribute(ClientContext.COOKIE_STORE, cookiestore);
		try {

			// HeadRequest, um Dateigröße abzufragen
			System.out.println("head start---------");
			HttpResponse resp = client.execute(head, context);
			System.out.println("head end");
			Header[] pdfSize = resp.getHeaders("Content-Length");
			System.out.println(pdfSize.length);

			for (int i = 0; i < pdfSize.length; i++) {
				System.out.println("Schleife startet");
				System.out.println("Dateigröße: " + Integer.valueOf(pdfSize[i].getValue()) + " -- " + adresse.getName());
				if (!localData.contains(Integer.valueOf(pdfSize[i].getValue()))) {
					// wenn keine Lokale Dateigröße mit den Online Dateien
					// übereinstimmt
					// Dateigröße stimmt überein, Hash prüfen!
					// Hash prüfen muss noch implementiert werden!
					System.out.println("Diese Datei ist noch nicht auf dem PC: " + pdfSize[i].getValue());
					// ladePdfHerunter(path);
				}
			}
			head.releaseConnection();
			System.out.println("ende");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void ladePdfHerunter(String PdfPath) {

		try {
			// diese Methode wurde aufgerufen wenn die pdf Datei noch nicht
			// vorhanden ist und soll diese jetzt runterladen!

			// mögliche Pfadkorrektur
			String nameNew = adresse.getName().replace(":", " - ").replace("/", "+");

			response = client.execute(request, context);
			entity = response.getEntity();

			File pdf = new File(path + "\\" + nameNew + "[" + "überordner" + "]" + ".pdf");
			if (entity != null) {
				// do something useful with the entity
				BufferedInputStream in = new BufferedInputStream(entity.getContent());

				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pdf));
				int inByte;
				while ((inByte = in.read()) != -1) {
					out.write(inByte);
				}
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void hashihash() {
		/*
		 * MessageDigest md = MessageDigest.getInstance("MD5"); out.close();
		 * pdfHash = new FileInputStream(pdf);
		 * 
		 * byte[] dataBytes = new byte[1024];
		 * 
		 * int nread = 0; while ((nread = pdfHash.read(dataBytes)) != -1) {
		 * md.update(dataBytes, 0, nread); }; byte[] mdbytes = md.digest();
		 * //convert the byte to hex format method 1 StringBuffer sb = new
		 * StringBuffer(); for (int i = 0; i < mdbytes.length; i++) {
		 * sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100,
		 * 16).substring(1)); }
		 * 
		 * System.out.println("Digest(in hex format): " + sb.toString() +
		 * "-----" + nameNew);
		 * 
		 * 
		 * in.close(); out.close();
		 */
		// httpget.releaseConnection();
	}

}