package maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MazeMap {

	private final int TILE_SIZE = 50;

	// 1 = wall, 0 = floor
	private int[][] map = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1 },
			{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1 },
			{ 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1 },
			{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };

	public void draw(Graphics2D g) {

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, map[0].length * TILE_SIZE, map.length * TILE_SIZE);

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(4));

		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[0].length; col++) {

				if (map[row][col] == 1) {

					int x = col * TILE_SIZE;
					int y = row * TILE_SIZE;

					// Top edge
					if (row == 0 || map[row - 1][col] == 0) {
						g.drawLine(x, y, x + TILE_SIZE, y);
					}

					// Bottom edge
					if (row == map.length - 1 || map[row + 1][col] == 0) {
						g.drawLine(x, y + TILE_SIZE, x + TILE_SIZE, y + TILE_SIZE);
					}

					// Left edge
					if (col == 0 || map[row][col - 1] == 0) {
						g.drawLine(x, y, x, y + TILE_SIZE);
					}

					// Right edge
					if (col == map[0].length - 1 || map[row][col + 1] == 0) {
						g.drawLine(x + TILE_SIZE, y, x + TILE_SIZE, y + TILE_SIZE);
					}
				}
			}
		}
	}

	public boolean isWall(int x, int y) {
		int col = x / TILE_SIZE;
		int row = y / TILE_SIZE;

		if (row < 0 || row >= map.length || col < 0 || col >= map[0].length)
			return true;

		return map[row][col] == 1;
	}

	public int getTileSize() {
		return TILE_SIZE;
	}
}
