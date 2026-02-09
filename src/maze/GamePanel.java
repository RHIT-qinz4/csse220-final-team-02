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
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

	private MazeMap map;

	private Player player;
	private ArrayList<Entity> entities = new ArrayList<>();

	private boolean gameOver = false;
	private int restartCounter = 0;

	private Timer timer;

	public GamePanel() {
		setFocusable(true);

		map = new MazeMap();

		initGame(); // build the world

		timer = new Timer(30, this);
		timer.start();

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

	private void initGame() {
		entities.clear();

		player = new Player(50, 50);
		entities.add(player);

		entities.add(new Zombie(300, 200));

		entities.add(new Gem(200, 100));
		entities.add(new Gem(400, 350));
		entities.add(new Gem(600, 150));

		gameOver = false;
		restartCounter = 0;
	}

	// ================= GAME LOOP =================

	@Override
	public void actionPerformed(ActionEvent e) {

		if (gameOver) {
			restartCounter--;

			if (restartCounter <= 0) {
				initGame();
			}

			repaint();
			return;
		}

		for (Entity entity : entities) {
			entity.update(map);
		}

		handleCollisions();

		if (player.getHP() <= 0) {
			gameOver = true;
			restartCounter = 150; // ~5 seconds
		}

		repaint();
	}

	// ================= DRAW =================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		map.draw(g2);

		for (Entity entity : entities) {
			entity.draw(g2);
		}

		// HUD
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		g2.drawString("HP: " + player.getHP(), 20, 30);
		g2.drawString("Points: " + player.getPoints(), 20, 60);

		if (gameOver) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial", Font.BOLD, 40));
			g2.drawString("GAME OVER", 260, 240);

			int secondsLeft = restartCounter / 30;
			g2.setFont(new Font("Arial", Font.BOLD, 24));
			g2.drawString("Restarting in: " + secondsLeft, 260, 280);
		}

		if (SpriteLoader.missingSprites) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial", Font.BOLD, 18));
			g2.drawString("Sprites not found — using placeholders", 40, 40);
		}
	}

	// ================= COLLISIONS =================

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
