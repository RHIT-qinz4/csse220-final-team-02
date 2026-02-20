package maze;

public interface Collectible {

	boolean isCollected();

	void collect();

	int getPointValue();
}