package starter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A control panel that combines the drawing area and the menu.
 */
public class Controller extends JPanel {
	private MyMenu menu;
	private DrawingComponent drawing;

	public Controller() {
		setLayout(new BorderLayout());

		menu = new MyMenu();
		drawing = new DrawingComponent();

		add(menu, BorderLayout.SOUTH);
		add(drawing, BorderLayout.CENTER);

		ButtonListener bl = new ButtonListener();
		menu.getLeftButton().addActionListener(bl);
		menu.getRightButton().addActionListener(bl);
		menu.getResetButton().addActionListener(bl);
		SwingUtilities.invokeLater(() -> drawing.requestFocusInWindow());
	}

	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String buttonLabel = e.getActionCommand();

			if (buttonLabel.equals("Left")) {
				drawing.moveLeft();
				drawing.requestFocusInWindow();
			} else if (buttonLabel.equals("Right")) {
				drawing.moveRight();
				drawing.requestFocusInWindow();
			} else {
				drawing.reset();
				drawing.requestFocusInWindow();
			}
		}
	}
}
