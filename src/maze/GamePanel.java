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