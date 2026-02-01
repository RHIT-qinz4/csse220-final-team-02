package maze;

public class Zombie extends Entity {

	private int moveTimer = 0;

	private boolean isHit = false;
	private int hitTimer = 0;

	public Zombie(int x, int y) {
		super(x, y);

		// WALK DOWN
		animations.put(AnimationState.WALK_DOWN, new java.awt.image.BufferedImage[] { SpriteLoader.load("z_down1.png"),
				SpriteLoader.load("z_down2.png"), SpriteLoader.load("z_down3.png") });

		// WALK UP
		animations.put(AnimationState.WALK_UP, new java.awt.image.BufferedImage[] { SpriteLoader.load("z_up1.png"),
				SpriteLoader.load("z_up2.png"), SpriteLoader.load("z_up3.png") });

		// WALK LEFT
		animations.put(AnimationState.WALK_LEFT, new java.awt.image.BufferedImage[] { SpriteLoader.load("z_left1.png"),
				SpriteLoader.load("z_left2.png"), SpriteLoader.load("z_left3.png") });

		// WALK RIGHT
		animations.put(AnimationState.WALK_RIGHT,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("z_right1.png"),
						SpriteLoader.load("z_right2.png"), SpriteLoader.load("z_right3.png") });

		// HIT ANIMATION
		animations.put(AnimationState.HIT, new java.awt.image.BufferedImage[] { SpriteLoader.load("z_hit1.png"),
				SpriteLoader.load("z_hit2.png"), SpriteLoader.load("z_hit3.png") });

		setRandomDirection();
	}

	private void setRandomDirection() {
		int dir = (int) (Math.random() * 4);

		dx = 0;
		dy = 0;

		switch (dir) {
		case 0:
			dy = -2;
			setState(AnimationState.WALK_UP);
			break;
		case 1:
			dy = 2;
			setState(AnimationState.WALK_DOWN);
			break;
		case 2:
			dx = -2;
			setState(AnimationState.WALK_LEFT);
			break;
		case 3:
			dx = 2;
			setState(AnimationState.WALK_RIGHT);
			break;
		}
	}

	@Override
	public void update() {
		super.update();

		// Handle hit animation
		if (isHit) {
			hitTimer--;
			if (hitTimer <= 0) {
				isHit = false;
				setRandomDirection();
			}
			return;
		}

		// Change direction every ~1 second
		moveTimer++;
		if (moveTimer > 60) {
			setRandomDirection();
			moveTimer = 0;
		}
	}

	public void getHit() {
		isHit = true;
		hitTimer = 40;
		dx = 0;
		dy = 0;
		setState(AnimationState.HIT);
	}
}
