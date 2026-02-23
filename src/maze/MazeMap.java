package maze;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Loads, stores, and renders a tile-based maze level.
 *
 * <p>
 * Tile characters: {@code #} wall, {@code .} floor, {@code P} player spawn,
 * {@code Z} zombie spawn, {@code G} gem spawn, {@code E} exit, {@code T} trap,
 * {@code D} door (blocks movement). Spawn tiles are replaced with {@code '.'}
 * by gamepanel after entity creation.
 *
 * <p>
 * Wall and floor tiles are loaded from image files; if any are missing,
 * procedural tiles are generated instead.
 */
public class MazeMap {

	/** Width and height of a single tile in pixels. */
	public static final int TILE_SIZE = 32;

	private static final String[] LEVEL_1 = { "#####################", "#P..#.....#.....#..E#", "#.#.#.###.#.###.#.###",
			"#.#...#.G.....#.....#", "#.#####.#####.#####.#", "#.......#Z....#.....#", "#.#####.#.###.#.###.#",
			"#.#.G.#.#.#.#.....#.#", "#.#.#.#.#.#.#.#####.#", "#...#.....#.#.......#", "###.#.#####.#######.#",
			"#.G.#.#...#.......#.#", "#.###.#.#.#######.#.#", "#.....#Z#.........#.#", "#.#####.#########.#.#",
			"#.G.......Z...G.....#", "#####################" };

	private static final String[] LEVEL_2 = { "#####################", "#P....#.......#....E#", "#.##.##.#####.##.##.#",
			"#.#..#....G......#..#", "#.#.##.#######.###..#", "#.#..#.#.....#...Z..#", "#.##.#.#.###.#.#####.",
			"#....#.#.#.G.#.#.G..#", "#.####.#.#...#.##.###", "#.#....#.#.###.#....#", "#.#.##.#.#.#Z..#.##.#",
			"#...#..#...#...#.#..#", "###.#.##.###.###.#.##", "#...#..#.....#...#..#", "#.###.##.###.#.###..#",
			"#G.........Z.....G..#", "#####################" };

	private char[][] map;

	/** Per-tile variant index (0–3) used to pick one of four tile images. */
	private int[][] tileVariant;

	private final BufferedImage[] wallTiles = new BufferedImage[4];
	private final BufferedImage[] pathTiles = new BufferedImage[4];
	private int levelNumber;
	private final Random rand = new Random();

	/**
	 * Loads the given level, randomises tile variants, and initialises tile images.
	 *
	 * @param level 1-based level number
	 */
	public MazeMap(int level) {
		this.levelNumber = level;
		loadLevel(level);
		generateTileVariants();
		loadOrGenerateTiles();
	}

	/** Parses the string-array layout into the {@code map} char array. */
	private void loadLevel(int level) {
		String[] src = (level == 2) ? LEVEL_2 : LEVEL_1;
		int rows = src.length, cols = src[0].length();
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

	/** Assigns a random 0–3 variant to every tile for visual variety. */
	private void generateTileVariants() {
		tileVariant = new int[getRows()][getCols()];
		for (int r = 0; r < getRows(); r++)
			for (int c = 0; c < getCols(); c++)
				tileVariant[r][c] = rand.nextInt(4);
	}

	/**
	 * Loads tile images from disk. Falls back to procedural generation if any file
	 * is missing.
	 */
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

		if (anyMissing)
			generateProceduralTiles();
	}

	/** Procedurally generates grey wall tiles and brown floor tiles. */
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

	/**
	 * Builds one procedural tile image with noise pixels and a bevel (walls) or a
	 * simple border (floors).
	 */
	private BufferedImage buildTile(Color base, boolean isWall, int variant) {
		int s = TILE_SIZE;
		BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();

		g.setColor(base);
		g.fillRect(0, 0, s, s);

		Random r = new Random(variant * 31L);
		for (int i = 0; i < 40; i++) {
			int nx = r.nextInt(s), ny = r.nextInt(s), delta = r.nextInt(20) - 10;
			g.setColor(clampColor(base.getRed() + delta, base.getGreen() + delta, base.getBlue() + delta));
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

	/** Returns a {@link Color} with each channel clamped to [0, 255]. */
	private static Color clampColor(int r, int g, int b) {
		return new Color(Math.max(0, Math.min(255, r)), Math.max(0, Math.min(255, g)), Math.max(0, Math.min(255, b)));
	}

	/**
	 * Returns the tile character at the given grid coordinates. Returns {@code '#'}
	 * for out-of-bounds positions.
	 *
	 * @param row row index (0-based, top to bottom)
	 * @param col column index (0-based, left to right)
	 */
	public char getTile(int row, int col) {
		if (row < 0 || row >= map.length)
			return '#';
		if (col < 0 || col >= map[row].length)
			return '#';
		return map[row][col];
	}

	/**
	 * Returns the tile at the given pixel position.
	 *
	 * @param pixelX horizontal pixel coordinate
	 * @param pixelY vertical pixel coordinate
	 */
	public char getTileAtPixel(float pixelX, float pixelY) {
		return getTile((int) (pixelY / TILE_SIZE), (int) (pixelX / TILE_SIZE));
	}

	/**
	 * Overwrites the tile at the given grid coordinates.
	 *
	 * @param row  row index
	 * @param col  column index
	 * @param tile new tile character
	 */
	public void setTile(int row, int col, char tile) {
		if (row >= 0 && row < map.length && col >= 0 && col < map[row].length)
			map[row][col] = tile;
	}

	/** Returns the number of tile rows. */
	public int getRows() {
		return map.length;
	}

	/** Returns the number of tile columns. */
	public int getCols() {
		return map[0].length;
	}

	/** Returns the 1-based level number this map was loaded for. */
	public int getLevelNumber() {
		return levelNumber;
	}

	/**
	 * Renders the entire map. Special overlays are painted on exit, trap, and door
	 * tiles.
	 *
	 * @param g graphics context
	 */
	public void draw(Graphics g) {
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				int x = col * TILE_SIZE, y = row * TILE_SIZE;
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

	/** Draws a green circle with "EXIT" text over the exit tile. */
	private void drawExitOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(0, 200, 100, 180));
		g.fillOval(x + 4, y + 4, s - 8, s - 8);
		g.setColor(Color.WHITE);
		g.setFont(new Font("SansSerif", Font.BOLD, 10));
		g.drawString("EXIT", x + 2, y + s / 2 + 4);
	}

	/** Draws a red diamond over the trap tile. */
	private void drawTrapOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(200, 50, 50, 200));
		int[] xp = { x + s / 2, x + s - 4, x + s / 2, x + 4 };
		int[] yp = { y + 4, y + s / 2, y + s - 4, y + s / 2 };
		g.fillPolygon(xp, yp, 4);
	}

	/** Draws a brown door with a gold handle over the door tile. */
	private void drawDoorOverlay(Graphics g, int x, int y) {
		int s = TILE_SIZE;
		g.setColor(new Color(120, 80, 40));
		g.fillRect(x + 6, y + 4, s - 12, s - 8);
		g.setColor(new Color(200, 160, 60));
		g.fillOval(x + s / 2 - 3, y + s / 2 - 3, 6, 6);
	}
}