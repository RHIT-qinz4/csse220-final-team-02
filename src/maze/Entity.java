package maze;

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

	public void update() {
		x += dx;
		y += dy;
		animate();
	}

	private void animate() {
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
		if (frames != null) {
			g.drawImage(frames[frameIndex], x, y, null);
		}
	}

	protected void setState(AnimationState state) {
		if (state != currentState) {
			currentState = state;
			frameIndex = 0;
			frameDelay = 0;
		}
	}
}
