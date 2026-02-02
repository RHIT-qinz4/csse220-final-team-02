package maze;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

	private MazeMap map;

	private Player player;
	private ArrayList<Entity> entities;

	public GamePanel() {
		setFocusable(true);

		map = new MazeMap();

		player = new Player(60, 60);
		Zombie zombie = new Zombie(300, 300);

		entities = new ArrayList<>();
		entities.add(player);
		entities.add(zombie);

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
			entity.update(map);
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		map.draw(g2);

		for (Entity entity : entities) {
			entity.draw(g2);
		}

		if (SpriteLoader.missingSprites) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial", Font.BOLD, 18));
			g2.drawString("Sprites not found — using placeholders", 40, 40);
		}
	}
}
