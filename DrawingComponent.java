package starter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

public class DrawingComponent extends JPanel {

	public DrawingComponent() {
		setBackground(Color.CYAN);
		setOpaque(true);
		setPreferredSize(new Dimension(500, 200));
		timer = new Timer(50, e -> {
			ball.move();
			repaint();
		});
		timer.start();
		setFocusable(true);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_T) {
					ball.flip();
				}
			}
		});
	}

	// DrawingComponent fields (example)
	private int start_x = 250;
	private int x = start_x;
	private int y = 20;
	private int step = 10;
	private Timer timer;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(4));
		g2.drawLine(x, y, x, y + 150);
		ball.draw(g2);
	}

	public void moveLeft() {
		x -= step;
		repaint();
	}

	public void moveRight() {
		x += step;
		repaint();
	}

	public void reset() {
		x = start_x;
		repaint();
	}

	public class Ball {
		private int x, y, radius;
		private int dx = 4; // direction + speed, 4 pixels per move
		private int dy = 0; // direction + speed

		public Ball(int x, int y, int radius) {
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		public void move() {
			x += dx;
		}

		public void draw(Graphics2D g2) {
			g2.setColor(Color.RED);
			g2.fillOval(x, y, 2 * radius, 2 * radius);
		}

		public void flip() {
			dx = -dx;
		}
	}

	private Ball ball = new Ball(80, 100, 14);
}
