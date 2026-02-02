package maze;

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
			BufferedImage original = ImageIO.read(new File(path));

			Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);

			BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = resized.createGraphics();
			g2.drawImage(scaled, 0, 0, null);
			g2.dispose();

			return resized;

		} catch (IOException e) {
			missingSprites = true;
			return null;
		}
	}
}
