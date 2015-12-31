package download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.IliasFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;

import view.Dashboard;
import control.Ilias;
import control.LocalFileStorage;

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

			response = Ilias.getClient().execute(request, context);
			entity = response.getEntity();

			BufferedInputStream in = new BufferedInputStream(entity.getContent());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetPath)));

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
