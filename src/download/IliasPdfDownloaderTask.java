package download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.IliasPdf;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;

import control.Ilias;
import control.LocalPdfStorage;
import view.Dashboard;

public class IliasPdfDownloaderTask extends Task<Void> {
	private HttpGet request;
	private HttpResponse response;
	private BasicHttpContext context;
	private HttpEntity entity;
	private IliasPdf pdf;
	private String targetPath;

	protected IliasPdfDownloaderTask(IliasPdf pdf, String targetPath) {
		this.pdf = pdf;
		this.targetPath = targetPath;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			request = new HttpGet(pdf.getUrl());

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
			e.printStackTrace();
		}
		
		LocalPdfStorage.getInstance().addPdf(pdf, targetPath);
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Dashboard.fileDownloaded(pdf);
			}
		});
		return null;
	}
}
