package download;

import java.io.File;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.IliasFile;
import model.IliasTreeNode;
import model.persistance.IliasTreeProvider;
import utils.DirectoryUtils;
import control.LocalFileStorage;

/**
 * This class provides the option to ask the User for a position to store the
 * file and calls {@link IliasFileDownloaderTask}.
 * 
 * @author deoldsax
 *
 */
public class IliasFileDownloader extends Task<Void> {
	private IliasTreeNode node;
	private DownloadMode mode;

	public IliasFileDownloader(final IliasTreeNode node, DownloadMode mode) {
		this.node = node;
		this.mode = mode;
	}

	public IliasFileDownloader(final IliasTreeNode node) {
		this(node, DownloadMode.NORMAL);
	}

	@Override
	protected Void call() throws Exception {
		final List<IliasFile> allFiles = IliasTreeProvider.getAllIliasFiles();

		for (IliasFile file : allFiles) {
			if (node instanceof IliasFile) {
				if (file.getUrl().equals(node.getUrl())) {
					download(file);
				}
			}
		}

		return null;
	}

	private void download(IliasFile file) {
		String targetPath = LocalFileStorage.getInstance().suggestDownloadPath(file);
		String name = DirectoryUtils.getInstance().makeDirectoryNameValid(file.getName());

		switch (mode) {
		case AUTO:
			targetPath = targetPath + "\\" + name + "." + file.getExtension();
			new Thread(new IliasFileDownloaderTask(file, targetPath)).start();
			break;
		case NORMAL:
			askForStoragePosition(name, targetPath, file);
			break;
		}
	}

	private void askForStoragePosition(String name, final String targetPath, final IliasFile file) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(targetPath));
		fileChooser.setInitialFileName(name + "." + file.getExtension());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final File selectedFile = fileChooser.showSaveDialog(new Stage());
				String path = targetPath;
				if (selectedFile != null) {
					path = selectedFile.getAbsolutePath();
					if (!path.endsWith("." + file.getExtension())) {
						path = path + "." + file.getExtension();
					}
					new Thread(new IliasFileDownloaderTask(file, path)).start();
				}
			}
		});
	}
}
