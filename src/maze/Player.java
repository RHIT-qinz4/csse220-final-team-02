package maze;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player extends Entity {

	private static final float SPEED = 2.5f;
	private static final int DAMAGE_COOLDOWN_TICKS = 90;
	private static final int ANIM_FRAME_DURATION = 10;
	private static final int NUM_FRAMES = 3;

	// Block mechanic constants
	public static final int BLOCK_DURATION_TICKS = 60; // 1 second at 60 FPS
	public static final int BLOCK_MAX_CHARGE = 300; // 5 seconds to fully recharge

	private int lives = 3;
	private int score = 0;
	private int keysHeld = 0;
	private int damageCooldown = 0;

	// Block state
	private boolean blockHeld = false;
	private boolean blocking = false;
	private int blockTicks = 0; // counts down while blocking
	private int blockCharge = BLOCK_MAX_CHARGE; // current charge (full = ready)

	private boolean movingUp, movingDown, movingLeft, movingRight;

	private final BufferedImage[][] frames;

	private AnimationState animState = AnimationState.IDLE;
	private int animTick = 0;
	private int animFrame = 0;

	public Player(float x, float y) {
		super(x, y);
		frames = SpriteLoader.loadPlayerFrames(MazeMap.TILE_SIZE);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = true;
		case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = true;
		case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = true;
		case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = true;
		case KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT -> blockHeld = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = false;
		case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = false;
		case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = false;
		case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = false;
		case KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT -> blockHeld = false;
		}
	}

	@Override
	public void update(MazeMap map) {
		if (damageCooldown > 0)
			damageCooldown--;

		// --- Block logic ---
		if (blocking) {
			blockTicks--;
			if (blockTicks <= 0) {
				// Block expired — start recharging from 0
				blocking = false;
				blockCharge = 0;
			}
		} else {
			// Recharge when not blocking
			if (blockCharge < BLOCK_MAX_CHARGE)
				blockCharge++;

			// Activate block only when fully charged and key is held
			if (blockHeld && blockCharge >= BLOCK_MAX_CHARGE) {
				blocking = true;
				blockTicks = BLOCK_DURATION_TICKS;
			}
		}

		float dx = 0, dy = 0;
		if (movingUp)
			dy -= SPEED;
		if (movingDown)
			dy += SPEED;
		if (movingLeft)
			dx -= SPEED;
		if (movingRight)
			dx += SPEED;

		if (dx != 0 && dy != 0) {
			dx *= 0.707f;
			dy *= 0.707f;
		}

		updateAnimState(dx, dy);
		moveAxis(map, dx, 0);
		moveAxis(map, 0, dy);

		if (dx != 0 || dy != 0) {
			animTick++;
			if (animTick >= ANIM_FRAME_DURATION) {
				animTick = 0;
				animFrame = (animFrame + 1) % NUM_FRAMES;
			}
		} else {
			animTick = 0;
			animFrame = 0;
		}
	}

	private void moveAxis(MazeMap map, float dx, float dy) {
		float newX = x + dx;
		float newY = y + dy;
		int ts = MazeMap.TILE_SIZE;
		int inset = 2;

		int left = (int) (newX + inset) / ts;
		int right = (int) (newX + ts - inset - 1) / ts;
		int top = (int) (newY + inset) / ts;
		int bottom = (int) (newY + ts - inset - 1) / ts;

		boolean blocked = isWall(map, top, left) || isWall(map, top, right) || isWall(map, bottom, left)
				|| isWall(map, bottom, right);
		if (!blocked) {
			x = newX;
			y = newY;
		}
	}

	private boolean isWall(MazeMap map, int row, int col) {
		char t = map.getTile(row, col);
		return t == '#' || t == 'D';
	}

	private void updateAnimState(float dx, float dy) {
		if (dy < 0)
			animState = AnimationState.WALK_UP;
		else if (dy > 0)
			animState = AnimationState.WALK_DOWN;
		else if (dx < 0)
			animState = AnimationState.WALK_LEFT;
		else if (dx > 0)
			animState = AnimationState.WALK_RIGHT;
		else
			animState = AnimationState.IDLE;
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (damageCooldown > 0 && (damageCooldown / 6) % 2 == 0)
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));

		int dirIndex = animStateToIndex();
		int frameIndex = (animState == AnimationState.IDLE) ? 0 : animFrame;
		g2.drawImage(frames[dirIndex][frameIndex], getPixelX(), getPixelY(), null);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		// Draw shield overlay when blocking
		if (blocking) {
			int px = getPixelX(), py = getPixelY(), s = MazeMap.TILE_SIZE;
			float pulse = (float) Math.abs(Math.sin(blockTicks * 0.18));
			int alpha = 120 + (int) (80 * pulse);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
			g2.setColor(new java.awt.Color(80, 160, 255));
			g2.fillOval(px - 4, py - 4, s + 8, s + 8);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2.setColor(new java.awt.Color(180, 220, 255));
			g2.drawOval(px - 4, py - 4, s + 8, s + 8);
		}
	}

	private int animStateToIndex() {
		return switch (animState) {
		case WALK_UP -> 0;
		case WALK_DOWN -> 1;
		case WALK_LEFT -> 2;
		case WALK_RIGHT -> 3;
		default -> 1;
		};
	}

	public int getLives() {
		return lives;
	}

	public int getScore() {
		return score;
	}

	public int getKeysHeld() {
		return keysHeld;
	}

	public boolean isInvincible() {
		return damageCooldown > 0;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public int getBlockCharge() {
		return blockCharge;
	}

	public int getBlockTicks() {
		return blockTicks;
	}

	public void takeDamage() {
		if (blocking) {
			// Block absorbs the hit — end the block early and start recharging
			blocking = false;
			blockTicks = 0;
			blockCharge = 0;
			damageCooldown = DAMAGE_COOLDOWN_TICKS; // brief invincibility so zombie steps back
			return;
		}
		if (damageCooldown == 0) {
			lives--;
			damageCooldown = DAMAGE_COOLDOWN_TICKS;
			animState = AnimationState.HIT;
		}
	}

	public void addScore(int pts) {
		score += pts;
	}

	public void addKey() {
		keysHeld++;
	}

	public boolean useKey() {
		if (keysHeld > 0) {
			keysHeld--;
			return true;
		}
		return false;
	}
}