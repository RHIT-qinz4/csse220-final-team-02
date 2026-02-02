package maze;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class Entity {

	protected int x, y;
	protected int dx, dy;

	protected HashMap<AnimationState, BufferedImage[]> animations = new HashMap<>();
	protected AnimationState currentState = AnimationState.IDLE;

	private int frameIndex = 0;
	private int frameDelay = 0;

	public Entity(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void update(MazeMap map) {

		int nextX = x + dx;
		int nextY = y + dy;

		if (!map.isWall(nextX, y)) {
			x = nextX;
		}

		if (!map.isWall(x, nextY)) {
			y = nextY;
		}

		animate();
	}

	protected void animate() {
		BufferedImage[] frames = animations.get(currentState);
		if (frames == null)
			return;

		frameDelay++;
		if (frameDelay > 8) {
			frameIndex = (frameIndex + 1) % frames.length;
			frameDelay = 0;
		}
	}

	public void draw(Graphics2D g) {
		BufferedImage[] frames = animations.get(currentState);
		if (frames != null && frames[frameIndex] != null) {
			g.drawImage(frames[frameIndex], x, y, null);
			return;
		}

		switch (currentState) {
		case WALK_UP:
			g.setColor(Color.BLUE);
			break;
		case WALK_DOWN:
			g.setColor(Color.GREEN);
			break;
		case WALK_LEFT:
			g.setColor(Color.ORANGE);
			break;
		case WALK_RIGHT:
			g.setColor(Color.CYAN);
			break;
		case HIT:
			g.setColor(Color.RED);
			break;
		default:
			g.setColor(Color.GRAY);
		}

		g.fillRect(x, y, 40, 40);
	}

	protected void setState(AnimationState state) {
		if (state != currentState) {
			currentState = state;
			frameIndex = 0;
			frameDelay = 0;
		}
	}
}
