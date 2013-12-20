package worker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;

import studport.Studierendenportal;

public class TranscriptSelector extends MouseAdapter {

	private final Studierendenportal studport;
	private final JComboBox<String> comboBox;
	private String selectedItem;

	public TranscriptSelector(Studierendenportal studport, JComboBox<String> comboBox) {
		this.studport = studport;
		this.comboBox = comboBox;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		selectedItem = (String) comboBox.getSelectedItem();
		studport.downloadNotenauszug(selectedItem);
	}
}
