package view;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class DownloadFolderChooser extends MouseAdapter {

	private final JFileChooser fileChooser;
	private final Component parentComponent;
	private final JLabel label;

	public DownloadFolderChooser(Component parentComponent, JLabel label) {
		this.parentComponent = parentComponent;
		this.label = label;
		fileChooser = new JFileChooser();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			openFileChooser();
		}
	}

	private void openFileChooser() {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final File selectedFile = new File(label.getText());
		fileChooser.setCurrentDirectory(selectedFile);
		fileChooser.setSelectedFile(new File(selectedFile.getName()));
		final int returnValue = fileChooser.showOpenDialog(parentComponent);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			label.setText(file.getAbsolutePath());
		}
	}
}
