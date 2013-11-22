package laf;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldListener implements MouseListener {

	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color COLOR = Color.GRAY;
	private final JTextField search;

	public TextFieldListener(JTextField search) {
		this.search = search;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (!search.getText().equals("suchen")) {
			return;
		}
		search.setText("");
		search.setForeground(TEXT_COLOR);
		search.setBorder(BorderFactory.createLineBorder(COLOR));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (search.isFocusOwner() && !search.getText().equals("suchen")) {
			return;
		}
		search.setText("");
		search.setForeground(TEXT_COLOR);
		search.setBorder(BorderFactory.createLineBorder(COLOR));
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (search.isFocusOwner()) {
			return;
		}
		search.setText("suchen");
		search.setForeground(Color.LIGHT_GRAY);
		search.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (!search.getText().equals("suchen")) {
			return;
		}
		search.setText("");
		search.setForeground(TEXT_COLOR);
		search.setBorder(BorderFactory.createLineBorder(COLOR));
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
