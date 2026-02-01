package maze;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Maze Game");
		GamePanel panel = new GamePanel();

		frame.add(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Timer timer = new Timer(16, panel); // ~60 FPS
		timer.start();
	}
}
