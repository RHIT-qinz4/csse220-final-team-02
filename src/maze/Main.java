package maze;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point.
 *
 * <p>
 * Creates the game window, attaches a GamePanel, and starts the game loop on
 * the Swing Event Dispatch Thread.
 */
public class Main {

	/** Launches the game. */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame window = new JFrame("Maze: Zombies and Gems");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);

			GamePanel panel = new GamePanel();
			window.add(panel);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);

			panel.requestFocusInWindow();
			panel.startGameThread();
		});
	}
}