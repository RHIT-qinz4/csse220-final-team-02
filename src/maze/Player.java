package maze;

import java.awt.event.KeyEvent;

public class Player extends Entity {

	private boolean isHit = false;
	private int hitTimer = 0;

	public Player(int x, int y) {
		super(x, y);

		animations.put(AnimationState.WALK_DOWN, new java.awt.image.BufferedImage[] { SpriteLoader.load("down1.png"),
				SpriteLoader.load("down2.png"), SpriteLoader.load("down3.png") });

		animations.put(AnimationState.WALK_UP, new java.awt.image.BufferedImage[] { SpriteLoader.load("up1.png"),
				SpriteLoader.load("up2.png"), SpriteLoader.load("up3.png") });

		animations.put(AnimationState.WALK_LEFT, new java.awt.image.BufferedImage[] { SpriteLoader.load("left1.png"),
				SpriteLoader.load("left2.png"), SpriteLoader.load("left3.png") });

		animations.put(AnimationState.WALK_RIGHT, new java.awt.image.BufferedImage[] { SpriteLoader.load("right1.png"),
				SpriteLoader.load("right2.png"), SpriteLoader.load("right3.png") });

		animations.put(AnimationState.HIT, new java.awt.image.BufferedImage[] { SpriteLoader.load("hit1.png"),
				SpriteLoader.load("hit2.png"), SpriteLoader.load("hit3.png") });

		setState(AnimationState.WALK_DOWN);
	}

	public void handleKeyPress(KeyEvent e) {

		if (isHit)
			return;

		if (e.getKeyCode() == KeyEvent.VK_W) {
			dy = -3;
			dx = 0;
			setState(AnimationState.WALK_UP);
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			dy = 3;
			dx = 0;
			setState(AnimationState.WALK_DOWN);
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			dx = -3;
			dy = 0;
			setState(AnimationState.WALK_LEFT);
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			dx = 3;
			dy = 0;
			setState(AnimationState.WALK_RIGHT);
		}
	}

	public void stop() {
		dx = 0;
		dy = 0;
	}

	public void getHit() {
		isHit = true;
		hitTimer = 40; // frames of hit animation
		setState(AnimationState.HIT);
	}

	@Override
	public void update() {
		super.update();

		if (isHit) {
			hitTimer--;
			if (hitTimer <= 0) {
				isHit = false;
				setState(AnimationState.WALK_DOWN);
			}
		}
	}
}