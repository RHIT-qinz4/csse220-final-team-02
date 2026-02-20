package maze;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {

	protected float x;

	protected float y;

	public Entity(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public int getPixelX() {
		return Math.round(x);
	}

	public int getPixelY() {
		return Math.round(y);
	}

	public Rectangle getBounds() {
		int inset = 4;
		return new Rectangle(getPixelX() + inset, getPixelY() + inset, MazeMap.TILE_SIZE - inset * 2,
				MazeMap.TILE_SIZE - inset * 2);
	}

	public abstract void update(MazeMap map);

	public abstract void draw(Graphics g);
}