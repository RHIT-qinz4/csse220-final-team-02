package maze;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

	private Player player;
	private ArrayList<Entity> entities;

	public GamePanel() {
		setFocusable(true);

		player = new Player(200, 200);
		entities = new ArrayList<>();
		entities.add(player);
		entities.add(new Zombie(400, 300));

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				player.handleKeyPress(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				player.stop();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Entity entity : entities) {
			entity.update();
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		for (Entity entity : entities) {
			entity.draw(g2);
		}
	}
}
