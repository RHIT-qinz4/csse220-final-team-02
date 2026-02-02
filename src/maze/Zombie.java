package maze;

public class Zombie extends Entity {

	private int moveTimer = 0;

	public Zombie(int x, int y) {
		super(x, y);

		animations.put(AnimationState.WALK_DOWN,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("z_down1.png", 40, 40),
						SpriteLoader.load("z_down2.png", 40, 40), SpriteLoader.load("z_down3.png", 40, 40) });

		animations.put(AnimationState.WALK_UP,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("z_up1.png", 40, 40),
						SpriteLoader.load("z_up2.png", 40, 40), SpriteLoader.load("z_up3.png", 40, 40) });

		animations.put(AnimationState.WALK_LEFT,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("z_left1.png", 40, 40),
						SpriteLoader.load("z_left2.png", 40, 40), SpriteLoader.load("z_left3.png", 40, 40) });

		animations.put(AnimationState.WALK_RIGHT,
				new java.awt.image.BufferedImage[] { SpriteLoader.load("z_right1.png", 40, 40),
						SpriteLoader.load("z_right2.png", 40, 40), SpriteLoader.load("z_right3.png", 40, 40) });

		setRandomDirection();
	}

	private void setRandomDirection() {

		int speed = 2;

		int[][] directions = { { speed, speed }, { -speed, speed }, { speed, -speed }, { -speed, -speed } };

		int[] dir = directions[(int) (Math.random() * directions.length)];

		dx = dir[0];
		dy = dir[1];

		if (dx > 0)
			setState(AnimationState.WALK_RIGHT);
		else
			setState(AnimationState.WALK_LEFT);
	}

	@Override
	public void update(MazeMap map) {

		int nextX = x + dx;
		int nextY = y + dy;

		boolean hitVerticalWall = map.isWall(nextX, y);
		boolean hitHorizontalWall = map.isWall(x, nextY);

		if (hitVerticalWall) {
			dx = -dx;
		}

		if (hitHorizontalWall) {
			dy = -dy;
		}

		x += dx;
		y += dy;

		animate();

		moveTimer++;
		if (moveTimer > 60) {
			setRandomDirection();
			moveTimer = 0;
		}
	}

}
