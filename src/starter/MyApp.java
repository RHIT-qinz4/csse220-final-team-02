package starter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



public class MyApp {
	private JFrame frame;  

	public MyApp() {
		frame = new JFrame("Moving Ball");
		frame.setContentPane(new Controller());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() ->new MyApp().run());
	}

	public void run() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

}
