package maze;

import java.awt.Color;
import java.awt.Graphics2D;

public class Gem extends Entity {

	public Gem(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		g.fillOval(x, y, 30, 30);
	}

	@Override
	public void update(MazeMap map) {
		// Gems do not move
	}
}
