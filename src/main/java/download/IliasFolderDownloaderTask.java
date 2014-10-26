package download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import model.IliasFile;
import model.IliasFolder;
import model.IliasTreeNode;
import model.persistance.Settings;
import utils.DirectoryUtils;

/**
 * This task recursively downloads the complete structure of all passed
 * {@link IliasFolder}s inside the {@linkplain #iliasTreeNodes list}.
 * 
 * @author deoldsax
 *
 */
public class IliasFolderDownloaderTask extends Task<Void> {
	private List<IliasTreeNode> iliasTreeNodes;

	public IliasFolderDownloaderTask(List<IliasTreeNode> iliasTreeNodes) {
		this.iliasTreeNodes = iliasTreeNodes;
	}

	public IliasFolderDownloaderTask(IliasTreeNode iliasTreeNode) {
		this.iliasTreeNodes = new ArrayList<IliasTreeNode>();
		iliasTreeNodes.add(iliasTreeNode);
	}

	@Override
	protected Void call() throws Exception {
		String localIliasFolderPath = Settings.getInstance().getIliasFolderSettings()
				.getLocalIliasFolderPath();
		new Thread(new Downloader(iliasTreeNodes, localIliasFolderPath)).start();
		return null;
	}

	private class Downloader implements Runnable {
		private List<IliasTreeNode> iliasTreeNodes;
		private String currentLevelDownloadPath;

		public Downloader(List<IliasTreeNode> iliasTreeNodes, String currentLevelDownloadPath) {
			this.iliasTreeNodes = iliasTreeNodes;
			this.currentLevelDownloadPath = currentLevelDownloadPath;
		}

		@Override
		public void run() {
			for (IliasTreeNode node : iliasTreeNodes) {
				if (node instanceof IliasFolder) {
					createFolder(node);
					String newDownloadPath = appendDownloadPath(node);
					new Thread(
							new Downloader(((IliasFolder) node).getChildNodes(), newDownloadPath))
							.start();
				} else if (node instanceof IliasFile) {
					downloadFile((IliasFile) node);
				}
			}
		}

		private void createFolder(IliasTreeNode node) {
			String newDownloadPath = appendDownloadPath(node);
			File file = new File(newDownloadPath);
			if (!file.exists()) {
				file.mkdir();
			}
		}

		private void downloadFile(IliasFile node) {
			IliasFile file = node;
			String validName = DirectoryUtils.getInstance().makeDirectoryNameValid(file.getName());
			String path = currentLevelDownloadPath + "/" + validName + "." + node.getExtension();
			new Thread(new IliasFileDownloaderTask(file, path)).start();
		}

		private String appendDownloadPath(IliasTreeNode node) {
			String validDirectoryString = DirectoryUtils.getInstance().makeDirectoryNameValid(
					node.getName());
			String newDownloadPath = currentLevelDownloadPath + "/" + validDirectoryString;
			return newDownloadPath;
		}
	}
}
