package view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class DownloadFolderChooser implements ActionListener {

	private final JFileChooser fileChooser;
	private final Component parentComponent;
	private final JTextField field;

	public DownloadFolderChooser(Component parentComponent, JTextField field) {
		this.parentComponent = parentComponent;
		this.field = field;
		fileChooser = new JFileChooser();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final File selectedFile = new File(field.getText());
		fileChooser.setCurrentDirectory(selectedFile);
		fileChooser.setSelectedFile(new File(selectedFile.getName()));
		final int returnValue = fileChooser.showOpenDialog(parentComponent);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			field.setText(file.getAbsolutePath());
		}
	}
}
