package maze;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Base class for all game objects player, zombie, gem.
 *
 * <p>
 * Holds a floating-point position and provides a collision rectangle inset by 4
 * pixels to allow smoother corridor navigation.
 */
public abstract class Entity {

	/** Horizontal position in pixels (top-left corner). */
	protected float x;

	/** Vertical position in pixels (top-left corner). */
	protected float y;

	/**
	 * @param x initial X position in pixels
	 * @param y initial Y position in pixels
	 */
	public Entity(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Returns the X position rounded to the nearest pixel. */
	public int getPixelX() {
		return Math.round(x);
	}

	/** Returns the Y position rounded to the nearest pixel. */
	public int getPixelY() {
		return Math.round(y);
	}

	/**
	 * Returns the collision rectangle for this entity. Inset by 4 px on each side
	 * to allow easier corridor navigation.
	 */
	public Rectangle getBounds() {
		int inset = 4;
		return new Rectangle(getPixelX() + inset, getPixelY() + inset, MazeMap.TILE_SIZE - inset * 2,
				MazeMap.TILE_SIZE - inset * 2);
	}

	/**
	 * Updates this entity's state for one game tick.
	 *
	 * @param map the current maze, used for wall-collision checks
	 */
	public abstract void update(MazeMap map);

	/**
	 * Draws this entity.
	 *
	 * @param g the graphics context
	 */
	public abstract void draw(Graphics g);
}