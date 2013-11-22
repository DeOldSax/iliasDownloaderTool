package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class DesignerFactory {
	public static void designField(JComponent component) {
		component.setOpaque(true);
		component.setBackground(new Color(255, 245, 190));
		component.setForeground(Color.BLACK);
		component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		component.setFont(new Font("Arial", Font.BOLD, 12));
	}

	public static void designLabel(JComponent component) {
		component.setOpaque(true);
		component.setBackground(Color.WHITE);
		component.setForeground(Color.BLACK);
		component.setFont(new Font("Arial", Font.BOLD, 13));
	}

	public static void designPanel(JComponent component, int top, int left, int bottom, int right) {
		component.setOpaque(true);
		component.setBackground(Color.WHITE);
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	public static void paint(JComponent component) {
		component.setOpaque(true);
		component.setBackground(Color.WHITE);
		component.setForeground(Color.BLACK);
	}

	public static void setEmptyBorder(JComponent component, int top, int left, int bottom, int right) {
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	public static void design(JComponent component, int top, int left, int bottom, int right) {
		paint(component);
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}
}
