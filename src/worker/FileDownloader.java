package worker;

import iliasWorker.Ilias;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import model.Adresse;
import model.LocalDataReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;

public class FileDownloader implements Runnable {
	private HttpGet request;
	private HttpResponse response;
	private BasicHttpContext context;
	private HttpEntity entity;
	private final Adresse adresse;
	private final String targetPath;
	private final String type;

	public FileDownloader(Adresse adresse, String type) {
		this.adresse = adresse;
		this.targetPath = new LocalDataReader().findLocalDownloadPath(adresse);
		this.type = type;
	}

	@Override
	public void run() {
		try {
			String name = adresse.getName().replace(":", " - ").replace("/", "+");

			request = new HttpGet(adresse.getUrl());

			response = Ilias.getClient().execute(request, context);
			entity = response.getEntity();

			BufferedInputStream in = new BufferedInputStream(entity.getContent());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetPath + "/" + name + "." + type)));

			int inByte;
			while ((inByte = in.read()) != -1) {
				out.write(inByte);
			}

			in.close();
			out.close();

			request.releaseConnection();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
