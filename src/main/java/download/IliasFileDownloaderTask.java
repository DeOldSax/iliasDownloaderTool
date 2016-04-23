package download;

import java.io.*;

import javafx.application.*;
import javafx.concurrent.*;
import model.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.protocol.*;
import org.apache.log4j.*;

import view.*;
import control.*;

public class IliasFileDownloaderTask extends Task<Void> {
	private HttpGet request;
	private HttpResponse response;
	private BasicHttpContext context;
	private HttpEntity entity;
	private IliasFile file;
	private String targetPath;

	protected IliasFileDownloaderTask(IliasFile file, String targetPath) {
		this.file = file;
		this.targetPath = targetPath;
	}

	@Override
	protected Void call() throws Exception {
		try {
			request = new HttpGet(file.getUrl());

			response = IliasManager.getInstance().getIliasClient().execute(request, context);
			entity = response.getEntity();

			BufferedInputStream in = new BufferedInputStream(entity.getContent());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(
					targetPath)));

			int inByte;
			while ((inByte = in.read()) != -1) {
				out.write(inByte);
			}

			in.close();
			out.close();

			request.releaseConnection();
		} catch (IOException e) {
			Logger.getLogger(getClass()).warn("", e);
		}

		LocalFileStorage.getInstance().addIliasFile(file, targetPath);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Dashboard.fileDownloaded(file);
			}
		});
		return null;
	}
}
