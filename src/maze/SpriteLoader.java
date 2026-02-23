package maze;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteLoader {

	/**
	 * Set to true when any sprite file is missing. Checked by to decide whether to
	 * generate procedural tiles.
	 */
	public static boolean missingSprites = false;

	/**
	 * Loads and scales an image from the given path. Returns a magenta placeholder
	 * if the file is missing or unreadable.
	 *
	 * @param path   file path relative to the working directory
	 * @param width  target width in pixels
	 * @param height target height in pixels
	 */
	public static BufferedImage load(String path, int width, int height) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				missingSprites = true;
				return placeholder(width, height);
			}
			BufferedImage img = ImageIO.read(f);
			if (img == null) {
				missingSprites = true;
				return placeholder(width, height);
			}
			return scale(img, width, height);
		} catch (IOException e) {
			missingSprites = true;
			return placeholder(width, height);
		}
	}

	/**
	 * Loads all player sprite frames as a array (4 directions × 3 walk frames).
	 *
	 * @param size sprite size in pixels
	 */
	public static BufferedImage[][] loadPlayerFrames(int size) {
		return loadFrames("player", size);
	}

	/**
	 * Loads all zombie sprite frames as a array (4 directions × 3 walk frames).
	 *
	 * @param size sprite size in pixels
	 */
	public static BufferedImage[][] loadZombieFrames(int size) {
		return loadFrames("zombie", size);
	}

	/**
	 * Loads the gem sprite, falling back to a gold diamond if the file is missing.
	 *
	 * @param size target size in pixels
	 */
	public static BufferedImage loadGemSprite(int size) {
		File f = new File("gem.png");
		if (f.exists()) {
			try {
				BufferedImage img = ImageIO.read(f);
				if (img != null)
					return scale(img, size, size);
			} catch (IOException ignored) {
			}
		}
		missingSprites = true;
		return generateGemFallback(size);
	}

	/**
	 * Loads a frame array for the named entity. Missing files are replaced with a
	 * coloured fallback sprite.
	 */
	private static BufferedImage[][] loadFrames(String entity, int size) {
		String[] dirs = { "up", "down", "left", "right" };
		BufferedImage[][] frames = new BufferedImage[4][3];
		for (int d = 0; d < 4; d++) {
			for (int f = 0; f < 3; f++) {
				File file = new File(entity + "_" + dirs[d] + "_" + f + ".png");
				if (file.exists()) {
					try {
						BufferedImage img = ImageIO.read(file);
						if (img != null) {
							frames[d][f] = scale(img, size, size);
							continue;
						}
					} catch (IOException ignored) {
					}
				}
				missingSprites = true;
				frames[d][f] = generateCharFallback(size, entity.equals("zombie"));
			}
		}
		return frames;
	}

	/**
	 * Generates a simple body+head placeholder. Blue for the player, green for
	 * zombies.
	 */
	private static BufferedImage generateCharFallback(int size, boolean zombie) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(zombie ? new Color(80, 140, 80) : new Color(60, 100, 200));
		g.fillRoundRect(4, 4, size - 8, size - 8, 6, 6);
		g.setColor(new Color(255, 210, 160));
		g.fillOval(size / 2 - 5, 4, 10, 10);
		g.dispose();
		return img;
	}

	/** Generates a gold diamond gem placeholder. */
	private static BufferedImage generateGemFallback(int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		int cx = size / 2, r = size / 2 - 4;
		int[] xp = { cx, cx + r, cx, cx - r }, yp = { cx - r, cx, cx + r, cx };
		g.setColor(new Color(255, 215, 0));
		g.fillPolygon(xp, yp, 4);
		g.setColor(new Color(200, 150, 0));
		g.drawPolygon(xp, yp, 4);
		g.dispose();
		return img;
	}

	/**
	 * Returns an array of four single-frame player sprites (one per direction,
	 * frame 0).
	 */
	public static BufferedImage[] generatePlayerSprites(int size) {
		BufferedImage[][] f = loadPlayerFrames(size);
		return new BufferedImage[] { f[0][0], f[1][0], f[2][0], f[3][0] };
	}

	/**
	 * Returns an array of four single-frame zombie sprites (one per direction,
	 * frame 0).
	 */
	public static BufferedImage[] generateZombieSprites(int size) {
		BufferedImage[][] f = loadZombieFrames(size);
		return new BufferedImage[] { f[0][0], f[1][0], f[2][0], f[3][0] };
	}

	/** Scales to the given dimensions using smooth interpolation. */
	private static BufferedImage scale(BufferedImage src, int w, int h) {
		Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = out.createGraphics();
		g2.drawImage(scaled, 0, 0, null);
		g2.dispose();
		return out;
	}

	/**
	 * Creates a solid magenta placeholder with a centred {@code '?'}. Makes missing
	 * assets immediately obvious during development.
	 */
	private static BufferedImage placeholder(int w, int h) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.MAGENTA);
		g2.fillRect(0, 0, w, h);
		g2.setColor(Color.BLACK);
		g2.drawString("?", w / 2 - 4, h / 2 + 4);
		g2.dispose();
		return img;
	}
}