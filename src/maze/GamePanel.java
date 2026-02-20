<<<<<<< HEAD
package maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

	public static final int FPS = 60;

	private enum GameState {
		PLAYING, LEVEL_COMPLETE, GAME_OVER, WIN
	}

	private Thread gameThread;
	private MazeMap map;
	private Player player;
	private final ArrayList<Zombie> zombies = new ArrayList<>();
	private final ArrayList<Gem> gems = new ArrayList<>();
	private final HUD hud = new HUD();

	private GameState state = GameState.PLAYING;
	private int currentLevel = 1;
	private static final int MAX_LEVEL = 2;

	private boolean huntActivated = false;

	public GamePanel() {
		loadLevel(currentLevel);

		setPreferredSize(new Dimension(map.getCols() * MazeMap.TILE_SIZE, map.getRows() * MazeMap.TILE_SIZE));
		setBackground(Color.BLACK);
		setFocusable(true);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleKeyPress(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (player != null)
					player.keyReleased(e);
			}
		});
	}

	private void loadLevel(int level) {
		currentLevel = level;
		map = new MazeMap(level);
		zombies.clear();
		gems.clear();
		player = null;

		for (int row = 0; row < map.getRows(); row++) {
			for (int col = 0; col < map.getCols(); col++) {
				char tile = map.getTile(row, col);
				float px = col * MazeMap.TILE_SIZE;
				float py = row * MazeMap.TILE_SIZE;

				switch (tile) {
				case 'P' -> {
					player = new Player(px, py);
					map.setTile(row, col, '.');
				}
				case 'Z' -> {
					zombies.add(new Zombie(px, py));
					map.setTile(row, col, '.');
				}
				case 'G' -> {
					gems.add(new Gem(px, py));
					map.setTile(row, col, '.');
				}
				}
			}
		}

		state = GameState.PLAYING;
		huntActivated = false;
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1_000_000_000.0 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();

		while (gameThread != null) {
			long now = System.nanoTime();
			delta += (now - lastTime) / drawInterval;
			lastTime = now;

			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException ignored) {
			}
		}
	}

	private void update() {
		if (state != GameState.PLAYING)
			return;

		player.update(map);

		for (Zombie z : zombies)
			z.update(map, player);

		checkGemCollection();
		checkHuntModeActivation();
		checkZombieCollisions();
		checkTrapTile();
		checkWinCondition();
		checkDeathCondition();
	}

	private void checkHuntModeActivation() {
		if (!huntActivated && allGemsCollected()) {
			huntActivated = true;
			for (Zombie z : zombies)
				z.activateHuntMode();
		}
	}

	private void checkGemCollection() {
		Rectangle playerBounds = player.getBounds();
		for (Gem g : gems) {
			if (!g.isCollected() && playerBounds.intersects(g.getBounds())) {
				g.collect();
				player.addScore(g.getPointValue());
			}
		}
	}

	private void checkZombieCollisions() {
		if (player.isInvincible())
			return;
		Rectangle playerBounds = player.getBounds();
		for (Zombie z : zombies) {
			if (playerBounds.intersects(z.getBounds())) {
				player.takeDamage();
				z.catapult(player, map);
				break;
			}
		}
	}

	private void checkTrapTile() {
		int row = player.getPixelY() / MazeMap.TILE_SIZE;
		int col = player.getPixelX() / MazeMap.TILE_SIZE;
		if (map.getTile(row, col) == 'T') {
			player.takeDamage();
		}
	}

	private void checkWinCondition() {
		if (allGemsCollected()) {
			int row = player.getPixelY() / MazeMap.TILE_SIZE;
			int col = player.getPixelX() / MazeMap.TILE_SIZE;
			if (map.getTile(row, col) == 'E') {
				if (currentLevel < MAX_LEVEL) {
					state = GameState.LEVEL_COMPLETE;
				} else {
					state = GameState.WIN;
				}
			}
		}
	}

	private void checkDeathCondition() {
		if (player.getLives() <= 0) {
			state = GameState.GAME_OVER;
		}
	}

	private boolean allGemsCollected() {
		for (Gem g : gems)
			if (!g.isCollected())
				return false;
		return true;
	}

	private int gemsRemaining() {
		int count = 0;
		for (Gem g : gems)
			if (!g.isCollected())
				count++;
		return count;
	}

	private void handleKeyPress(KeyEvent e) {
		switch (state) {
		case PLAYING -> {
			if (player != null)
				player.keyPressed(e);
		}
		case GAME_OVER, WIN -> {
			if (e.getKeyCode() == KeyEvent.VK_R) {
				loadLevel(1);
			}
		}
		case LEVEL_COMPLETE -> {
			if (e.getKeyCode() == KeyEvent.VK_N) {

				int savedScore = player.getScore();
				loadLevel(currentLevel + 1);
				player.addScore(savedScore);
			}
		}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		map.draw(g);

		for (Gem gem : gems)
			gem.draw(g);

		for (Zombie z : zombies)
			z.draw(g);

		if (player != null)
			player.draw(g);

		if (player != null) {
			hud.draw(g, player, currentLevel, gemsRemaining());
		}

		int w = getWidth(), h = getHeight();
		switch (state) {
		case GAME_OVER -> hud.drawGameOver(g, w, h, player.getScore());
		case LEVEL_COMPLETE -> hud.drawLevelComplete(g, w, h, currentLevel, player.getScore());
		case WIN -> hud.drawWin(g, w, h, player != null ? player.getScore() : 0);
		default -> {
		}
		}
	}
}
=======
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
<<<<<<< HEAD
=======
		g2.drawString("Health: " + Integer.toString(player.getHP()), 20, 30);
		g2.drawString("Points: " + Integer.toString(player.getPoints()), 120, 30);
		if(gameOver) {
			g2.drawString("GAME OVER: Restarting in 5...", 500, 30);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
        }
>>>>>>> de36945fd8ca3248639e1e532f60b680979466d4

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
>>>>>>> b81d4c94727e8c358647e577bb6d2d65a41e1f86
