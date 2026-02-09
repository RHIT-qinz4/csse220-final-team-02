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
	private boolean gameOver = false;

	public GamePanel() {
		setFocusable(true);

		map = new MazeMap();

		player = new Player(60, 60);
		Zombie zombie = new Zombie(300, 300);

		entities = new ArrayList<>();
		entities.add(player);
		entities.add(zombie);
		entities.add(new Gem(200, 100));
		entities.add(new Gem(400, 350));
		entities.add(new Gem(600, 150));

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

		if (gameOver)
			return;

		for (Entity entity : entities) {
			entity.update(map);
		}

		handleCollisions();

		if (player.getHP() <= 0) {
			gameOver = true;
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

	private void handleCollisions() {

		for (int i = 0; i < entities.size(); i++) {
			for (int j = i + 1; j < entities.size(); j++) {

				Entity a = entities.get(i);
				Entity b = entities.get(j);

				if (a.getBounds().intersects(b.getBounds())) {

					// Player ↔ Zombie
					if (a instanceof Player && b instanceof Zombie) {
						Player p = (Player) a;
						Zombie z = (Zombie) b;
						p.damage();
						z.knockBack(p, map);
					}

					if (b instanceof Player && a instanceof Zombie) {
						Player p = (Player) b;
						Zombie z = (Zombie) a;
						p.damage();
						z.knockBack(p, map);
					}

					// Player ↔ Gem
					if (a instanceof Player && b instanceof Gem) {
						((Player) a).addPoint();
						entities.remove(b);
						j--;
					}

					if (b instanceof Player && a instanceof Gem) {
						((Player) b).addPoint();
						entities.remove(a);
						i--;
						break;
					}
				}
			}
		}
	}

}
