package maze;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteLoader {

	public static boolean missingSprites = false;

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

	public static BufferedImage[][] loadPlayerFrames(int size) {
		return loadFrames("player", size);
	}

	public static BufferedImage[][] loadZombieFrames(int size) {
		return loadFrames("zombie", size);
	}

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

	private static BufferedImage[][] loadFrames(String entity, int size) {
		String[] dirs = { "up", "down", "left", "right" };
		int numFrames = 3;
		BufferedImage[][] frames = new BufferedImage[4][numFrames];
		for (int d = 0; d < 4; d++) {
			for (int f = 0; f < numFrames; f++) {
				String path = entity + "_" + dirs[d] + "_" + f + ".png";
				File file = new File(path);
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

	private static BufferedImage generateGemFallback(int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		int cx = size / 2, r = size / 2 - 4;
		int[] xp = { cx, cx + r, cx, cx - r };
		int[] yp = { cx - r, cx, cx + r, cx };
		g.setColor(new Color(255, 215, 0));
		g.fillPolygon(xp, yp, 4);
		g.setColor(new Color(200, 150, 0));
		g.drawPolygon(xp, yp, 4);
		g.dispose();
		return img;
	}

	public static BufferedImage[] generatePlayerSprites(int size) {
		BufferedImage[][] frames = loadPlayerFrames(size);
		return new BufferedImage[] { frames[0][0], frames[1][0], frames[2][0], frames[3][0] };
	}

	public static BufferedImage[] generateZombieSprites(int size) {
		BufferedImage[][] frames = loadZombieFrames(size);
		return new BufferedImage[] { frames[0][0], frames[1][0], frames[2][0], frames[3][0] };
	}

	private static BufferedImage scale(BufferedImage src, int w, int h) {
		Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = out.createGraphics();
		g2.drawImage(scaled, 0, 0, null);
		g2.dispose();
		return out;
	}

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