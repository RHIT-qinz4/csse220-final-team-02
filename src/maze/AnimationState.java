package maze;

/** Animation states used by player and zombie **/
public enum AnimationState {
	/** Walking upward. */
	WALK_UP,
	/** Walking downward. */
	WALK_DOWN,
	/** Walking left. */
	WALK_LEFT,
	/** Walking right. */
	WALK_RIGHT,
	/** Just received damage. */
	HIT,
	/** Standing still. */
	IDLE
}