package maze;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MazeMap {

	public static final int TILE_SIZE = 32;

	private static final String[] LEVEL_1 = { "#####################", 
			"#P..#.....#.....#..E#", 
			"#.#.#.###.#.###.#.###",
			"#.#...#.G.....#.....#", 
			"#.#####.#####.#####.#", 
			"#.......#Z....#.....#", 
			"#.#####.#.###.#.###.#",
			"#.#.G.#.#.#.#.....#.#", 
			"#.#.#.#.#.#.#.#####.#", 
			"#...#.....#.#.......#",
			"###.#.#####.#######.#",
			"#.G.#.#...#.......#.#", 
			"#.###.#.#.#######.#.#", 
			"#.....#Z#.........#.#", 
			"#.#####.#########.#.#",
			"#.G.......Z...G.....#", 
			"#####################" };

	private static final String[] LEVEL_2 = { "#####################", "#P....#.......#....E#", "#.##.##.#####.##.##.#",
			"#.#..#....G......#..#", "#.#.##.#######.###..#", "#.#..#.#.....#...Z..#", "#.##.#.#.###.#.#####.",
			"#....#.#.#.G.#.#.G..#", "#.####.#.#...#.##.###", "#.#....#.#.###.#....#", "#.#.##.#.#.#Z..#.##.#",
			"#...#..#...#...#.#..#", "###.#.##.###.###.#.##", "#...#..#.....#...#..#", "#.###.##.###.#.###..#",
			"#G.........Z.....G..#", "#####################" };

	private char[][] map;

	private int[][] tileVariant;

	private final BufferedImage[] wallTiles = new BufferedImage[4];

	private final BufferedImage[] pathTiles = new BufferedImage[4];

	private int levelNumber;

	private final Random rand = new Random();

	public MazeMap(int level) {
		this.levelNumber = level;
		loadLevel(level);
		generateTileVariants();
		loadOrGenerateTiles();
	}

	private void loadLevel(int level) {
		String[] src = (level == 2) ? LEVEL_2 : LEVEL_1;
		int rows = src.length;
		int cols = src[0].length();
		map = new char[rows][cols];
		for (int r = 0; r < rows; r++) {

			String row = src[r];
			while (row.length() < cols)
				row += "#";
			if (row.length() > cols)
				row = row.substring(0, cols);
			map[r] = row.toCharArray();
		}
	}

	private void generateTileVariants() {
		tileVariant = new int[getRows()][getCols()];
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getCols(); c++)
				tileVariant[r][c] = rand.nextInt(4);
	}

	private void loadOrGenerateTiles() {
		String[] wallPaths = { "tile.png", "tile1.png", "tile2.png", "tile3.png" };
		String[] pathPaths = { "path.png", "path2.png", "path3.png", "path4.png" };

		boolean anyMissing = false;
		for (int i = 0; i < 4; i++) {
			wallTiles[i] = SpriteLoader.load(wallPaths[i], TILE_SIZE, TILE_SIZE);
			pathTiles[i] = SpriteLoader.load(pathPaths[i], TILE_SIZE, TILE_SIZE);
			if (SpriteLoader.missingSprites)
				anyMissing = true;
		}

		if (anyMissing) {
			generateProceduralTiles();
		}
	}

	private void generateProceduralTiles() {

		Color[] wallColors = { new Color(90, 90, 100), new Color(85, 88, 98), new Color(95, 93, 105),
				new Color(88, 86, 95) };

		Color[] floorColors = { new Color(60, 45, 30), new Color(55, 42, 28), new Color(65, 48, 32),
				new Color(58, 44, 29) };

		for (int i = 0; i < 4; i++) {
			wallTiles[i] = buildTile(wallColors[i], true, i);
			pathTiles[i] = buildTile(floorColors[i], false, i);
		}
	}

	private BufferedImage buildTile(Color base, boolean isWall, int variant) {
		int s = TILE_SIZE;
		BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();

		g.setColor(base);
		g.fillRect(0, 0, s, s);

		Random r = new Random(variant * 31L);
		for (int i = 0; i < 40; i++) {
			int nx = r.nextInt(s);
			int ny = r.nextInt(s);
			int delta = r.nextInt(20) - 10;
			Color noisy = clampColor(base.getRed() + delta, base.getGreen() + delta, base.getBlue() + delta);
			g.setColor(noisy);
			g.fillRect(nx, ny, 2, 2);
		}

		if (isWall) {
			g.setColor(base.brighter().brighter());
			g.drawLine(0, 0, s - 1, 0);
			g.drawLine(0, 0, 0, s - 1);
			g.setColor(base.darker().darker());
			g.drawLine(s - 1, 0, s - 1, s - 1);
			g.drawLine(0, s - 1, s - 1, s - 1);
		} else {

			g.setColor(base.darker());
			g.drawRect(0, 0, s - 1, s - 1);
		}

		g.dispose();
		return img;
	}

	private static Color clampColor(int r, int g, int b) {
		return new Color(Math.max(0, Math.min(255, r)), Math.max(0, Math.min(255, g)), Math.max(0, Math.min(255, b)));
	}

	public char getTile(int row, int col) {
		if (row < 0 || row >= map.length)
			return '#';
		if (col < 0 || col >= map[row].length)
			return '#';
		return map[row][col];
	}

	public char getTileAtPixel(float pixelX, float pixelY) {
		int col = (int) (pixelX / TILE_SIZE);
		int row = (int) (pixelY / TILE_SIZE);
		return getTile(row, col);
	}

	public void setTile(int row, int col, char tile) {
		if (row >= 0 && row < map.length && col >= 0 && col < map[row].length)
			map[row][col] = tile;
	}

	public int getRows() {
		return map.length;
	}

	public int getCols() {
		return map[0].length;
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void draw(Graphics g) {
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				int x = col * TILE_SIZE;
				int y = row * TILE_SIZE;
				char tile = map[row][col];
				int v = tileVariant[row][col];

				if (tile == '#') {
					g.drawImage(wallTiles[v], x, y, null);
				} else {
					g.drawImage(pathTiles[v], x, y, null);

					if (tile == 'E')
						drawExitOverlay(g, x, y);

					if (tile == 'T')
						drawTrapOverlay(g, x, y);

					if (tile == 'D')
						drawDoorOverlay(g, x, y);
				}
			}
		}
	}

	private void drawExitOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(0, 200, 100, 180));
		g.fillOval(x + 4, y + 4, s - 8, s - 8);
		g.setColor(Color.WHITE);
		g.setFont(new Font("SansSerif", Font.BOLD, 10));
		g.drawString("EXIT", x + 2, y + s / 2 + 4);
	}

	private void drawTrapOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(200, 50, 50, 200));
		int[] xp = { x + s / 2, x + s - 4, x + s / 2, x + 4 };
		int[] yp = { y + 4, y + s / 2, y + s - 4, y + s / 2 };
		g.fillPolygon(xp, yp, 4);
	}

	private void drawDoorOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(120, 80, 40));
		g.fillRect(x + 6, y + 4, s - 12, s - 8);
		g.setColor(new Color(200, 160, 60));
		g.fillOval(x + s / 2 - 3, y + s / 2 - 3, 6, 6);
	}
}