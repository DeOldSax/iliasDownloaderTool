package laf;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTree;

public class FocusBorderOfTree extends MouseAdapter {

	public FocusBorderOfTree() {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		JTree source = (JTree) arg0.getSource();
		source.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		JTree source = (JTree) arg0.getSource();
		source.setBorder(BorderFactory.createLineBorder(Color.GRAY));

	}
}
