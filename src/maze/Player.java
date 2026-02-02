package maze;

import java.awt.event.KeyEvent;

public class Player extends Entity {

	public Player(int x, int y) {
		super(x, y);

		animations.put(AnimationState.WALK_DOWN,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("down1.png", 40, 40),
						SpriteLoader.load("down2.png", 40, 40), SpriteLoader.load("down3.png", 40, 40) });

		animations.put(AnimationState.WALK_UP,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("up1.png", 40, 40),
						SpriteLoader.load("up2.png", 40, 40), SpriteLoader.load("up3.png", 40, 40) });

		animations.put(AnimationState.WALK_LEFT,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("left1.png", 40, 40),
						SpriteLoader.load("left2.png", 40, 40), SpriteLoader.load("left3.png", 40, 40) });

		animations.put(AnimationState.WALK_RIGHT,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("right1.png", 40, 40),
						SpriteLoader.load("right2.png", 40, 40), SpriteLoader.load("right3.png", 40, 40) });

		animations.put(AnimationState.HIT, new java.awt.image.BufferedImage[] { SpriteLoader.load("hit1.png", 40, 40),
				SpriteLoader.load("hit2.png", 40, 40), SpriteLoader.load("hit3.png", 40, 40) });
		setState(AnimationState.WALK_DOWN);
	}

	public void handleKeyPress(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_W) {
			dx = 0;
			dy = -3;
			setState(AnimationState.WALK_UP);
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			dx = 0;
			dy = 3;
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
}
