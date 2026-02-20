package maze;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class HUD {

	private static final Color BG_COLOR = new Color(0, 0, 0, 180);

	private static final Color TEXT_COLOR = Color.WHITE;

	private static final Color SCORE_COLOR = new Color(255, 215, 0);

	private static final Color KEY_COLOR = new Color(200, 160, 60);

	private static final Font LABEL_FONT = new Font("Monospaced", Font.BOLD, 13);
	private static final Font VALUE_FONT = new Font("Monospaced", Font.PLAIN, 13);

	public void draw(Graphics g, Player player, int level, int gemsLeft) {
		int barHeight = 30;
		int panelW = MazeMap.TILE_SIZE * 21;

		g.setColor(BG_COLOR);
		g.fillRect(0, 0, panelW, barHeight);

		g.setFont(LABEL_FONT);
		int y = 20;

		g.setColor(TEXT_COLOR);
		g.drawString("LIVES:", 8, y);
		drawHearts(g, 65, 8, player.getLives());

		g.setColor(TEXT_COLOR);
		g.drawString("SCORE:", 140, y);
		g.setColor(SCORE_COLOR);
		g.setFont(VALUE_FONT);
		g.drawString(String.valueOf(player.getScore()), 200, y);

		g.setColor(TEXT_COLOR);
		g.setFont(LABEL_FONT);
		g.drawString("LVL:" + level, 260, y);

		g.setColor(TEXT_COLOR);
		g.drawString("GEMS:", 310, y);
		g.setColor(SCORE_COLOR);
		g.setFont(VALUE_FONT);
		g.drawString(String.valueOf(gemsLeft), 365, y);

		g.setColor(TEXT_COLOR);
		g.setFont(LABEL_FONT);
		g.drawString("KEYS:", 400, y);
		g.setColor(KEY_COLOR);
		g.setFont(VALUE_FONT);
		g.drawString(String.valueOf(player.getKeysHeld()), 450, y);

		// Block charge bar
		drawBlockBar(g, 480, 6, player);
	}

	private void drawBlockBar(Graphics g, int bx, int by, Player player) {
		int barW = 120;
		int barH = 10;

		boolean blocking = player.isBlocking();
		int charge = player.getBlockCharge();
		int maxCharge = Player.BLOCK_MAX_CHARGE;

		// Background
		g.setColor(new Color(30, 30, 50));
		g.fillRoundRect(bx, by, barW, barH, 4, 4);

		// Fill
		float ratio;
		Color fillColor;
		if (blocking) {
			// Show remaining block time draining
			ratio = (float) player.getBlockTicks() / Player.BLOCK_DURATION_TICKS;
			fillColor = new Color(80, 160, 255);
		} else if (charge >= maxCharge) {
			ratio = 1f;
			fillColor = new Color(100, 220, 255);
		} else {
			ratio = (float) charge / maxCharge;
			fillColor = new Color(60, 100, 160);
		}

		int fillW = (int) (barW * ratio);
		if (fillW > 0) {
			g.setColor(fillColor);
			g.fillRoundRect(bx, by, fillW, barH, 4, 4);
		}

		// Border
		g.setColor(new Color(150, 200, 255));
		g.drawRoundRect(bx, by, barW, barH, 4, 4);

		// Label
		g.setColor(TEXT_COLOR);
		g.setFont(LABEL_FONT);
		String label = blocking ? "BLOCK" : (charge >= maxCharge ? "BLOCK" : "RECHARGE");
		g.drawString(label, bx + barW + 4, by + barH);
	}

	private void drawHearts(Graphics g, int startX, int startY, int lives) {
		for (int i = 0; i < 3; i++) {
			int hx = startX + i * 20;
			int hy = startY + 2;
			if (i < lives) {
				g.setColor(new Color(220, 50, 50));
			} else {
				g.setColor(new Color(80, 40, 40));
			}

			g.fillOval(hx, hy, 8, 8);
			g.fillOval(hx + 5, hy, 8, 8);
			int[] hxp = { hx, hx + 13, hx + 6 };
			int[] hyp = { hy + 6, hy + 6, hy + 15 };
			g.fillPolygon(hxp, hyp, 3);
		}
	}

	public void drawGameOver(Graphics g, int width, int height, int score) {
		drawOverlay(g, width, height, new Color(180, 30, 30, 220), "GAME OVER", "Score: " + score,
				"Press R to restart");
	}

	public void drawLevelComplete(Graphics g, int width, int height, int level, int score) {
		drawOverlay(g, width, height, new Color(30, 140, 60, 220), "LEVEL " + level + " COMPLETE!", "Score: " + score,
				"Press N for next level");
	}

	public void drawWin(Graphics g, int width, int height, int score) {
		drawOverlay(g, width, height, new Color(30, 100, 200, 220), "YOU WIN!", "Final Score: " + score,
				"Press R to play again");
	}

	private void drawOverlay(Graphics g, int w, int h, Color bg, String title, String sub, String hint) {
		int bw = 320, bh = 120;
		int bx = (w - bw) / 2, by = (h - bh) / 2;

		g.setColor(bg);
		g.fillRoundRect(bx, by, bw, bh, 20, 20);
		g.setColor(Color.WHITE);
		g.drawRoundRect(bx, by, bw, bh, 20, 20);

		g.setFont(new Font("SansSerif", Font.BOLD, 26));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, bx + (bw - fm.stringWidth(title)) / 2, by + 42);

		g.setFont(new Font("SansSerif", Font.PLAIN, 16));
		fm = g.getFontMetrics();
		g.drawString(sub, bx + (bw - fm.stringWidth(sub)) / 2, by + 68);
		g.setColor(new Color(200, 200, 200));
		g.drawString(hint, bx + (bw - fm.stringWidth(hint)) / 2, by + 94);
	}
}