package maze;

/**
 * Interface for items the player can pick up.
 *
 * @see Gem
 */
public interface Collectible {

	/** Returns {@code true} if this item has already been collected. */
	boolean isCollected();

	/** Marks this item as collected. */
	void collect();

	/** Returns the score points awarded when this item is collected. */
	int getPointValue();
}