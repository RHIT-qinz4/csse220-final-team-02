package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Gem extends Entity implements Collectible {

	private static final int POINT_VALUE = 10;

	private boolean collected = false;
	private int animTick = 0;

	private static final BufferedImage SPRITE = SpriteLoader.loadGemSprite(MazeMap.TILE_SIZE);

	public Gem(float x, float y) {
		super(x, y);
	}

	@Override
	public boolean isCollected() {
		return collected;
	}

	@Override
	public void collect() {
		collected = true;
	}

	@Override
	public int getPointValue() {
		return POINT_VALUE;
	}

	@Override
	public void update(MazeMap map) {
		animTick++;
	}

	@Override
	public void draw(Graphics g) {
		if (collected)
			return;

		int px = getPixelX();
		int py = getPixelY();
		int s = MazeMap.TILE_SIZE;

		int bob = (int) (Math.sin(animTick * 0.07) * 2);

		if (SPRITE != null) {
			g.drawImage(SPRITE, px, py + bob, null);
		} else {

			int cx = px + s / 2, cy = py + s / 2 + bob, r = s / 2 - 5;
			int[] xp = { cx, cx + r, cx, cx - r };
			int[] yp = { cy - r, cy, cy + r, cy };
			g.setColor(new Color(255, 215, 0));
			g.fillPolygon(xp, yp, 4);
			g.setColor(new Color(255, 255, 180));
			g.fillOval(cx - r / 3, cy - r / 2, r / 3, r / 3);
			g.setColor(new Color(200, 150, 0));
			g.drawPolygon(xp, yp, 4);
		}
	}
}