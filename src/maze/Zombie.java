package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Zombie extends Entity {

	private static final float SPEED = 1.2f;
	private static final float HUNT_SPEED = 1.6f;
	private static final int DIRECTION_HOLD = 80;
	private static final int HUNT_RECALC_INTERVAL = 30;
	private static final int ANIM_FRAME_DURATION = 12;
	private static final int NUM_FRAMES = 3;

	private final Random rand;
	private int direction;
	private int dirTimer;

	private boolean huntMode = false;
	private int huntRecalcTimer = 0;
	private final Deque<int[]> path = new ArrayDeque<>();

	private AnimationState animState = AnimationState.WALK_DOWN;
	private int animTick = 0;
	private int animFrame = 0;

	private final BufferedImage[][] frames;

	public Zombie(float x, float y) {
		super(x, y);
		rand = new Random();
		direction = rand.nextInt(4);
		dirTimer = rand.nextInt(DIRECTION_HOLD);
		frames = SpriteLoader.loadZombieFrames(MazeMap.TILE_SIZE);
	}

	public void activateHuntMode() {
		huntMode = true;
		huntRecalcTimer = 0;
		path.clear();
	}

	public void update(MazeMap map, Player player) {
		if (huntMode) {
			updateHunt(map, player);
		} else if (hasLineOfSight(map, player)) {
			updateChase(player);
		} else {
			updateRandomWalk(map);
		}

		animTick++;
		if (animTick >= ANIM_FRAME_DURATION) {
			animTick = 0;
			animFrame = (animFrame + 1) % NUM_FRAMES;
		}
	}

	@Override
	public void update(MazeMap map) {
		updateRandomWalk(map);
		animTick++;
		if (animTick >= ANIM_FRAME_DURATION) {
			animTick = 0;
			animFrame = (animFrame + 1) % NUM_FRAMES;
		}
	}

	private void updateRandomWalk(MazeMap map) {
		dirTimer--;
		if (dirTimer <= 0) {
			direction = rand.nextInt(4);
			dirTimer = DIRECTION_HOLD + rand.nextInt(40);
		}
		float dx = 0, dy = 0;
		switch (direction) {
		case 0 -> dy = -SPEED;
		case 1 -> dy = SPEED;
		case 2 -> dx = -SPEED;
		case 3 -> dx = SPEED;
		}
		updateAnimState(dx, dy);
		if (!tryMove(map, dx, dy)) {
			direction = rand.nextInt(4);
			dirTimer = DIRECTION_HOLD;
		}
	}

	private boolean hasLineOfSight(MazeMap map, Player player) {
		int ts = MazeMap.TILE_SIZE;
		int myRow = Math.round(y) / ts;
		int myCol = Math.round(x) / ts;
		int pRow = player.getPixelY() / ts;
		int pCol = player.getPixelX() / ts;

		int facingDRow = 0, facingDCol = 0;
		switch (direction) {
		case 0 -> facingDRow = -1;
		case 1 -> facingDRow = 1;
		case 2 -> facingDCol = -1;
		case 3 -> facingDCol = 1;
		}

		if (myRow == pRow && facingDRow == 0) {
			int toPlayerCol = pCol - myCol;
			if (toPlayerCol == 0)
				return false;
			boolean ahead = (facingDCol > 0 && toPlayerCol > 0) || (facingDCol < 0 && toPlayerCol < 0);
			if (!ahead)
				return false;
			int minC = Math.min(myCol, pCol);
			int maxC = Math.max(myCol, pCol);
			for (int c = minC + 1; c < maxC; c++)
				if (map.getTile(myRow, c) == '#')
					return false;
			return true;
		}

		if (myCol == pCol && facingDCol == 0) {
			int toPlayerRow = pRow - myRow;
			if (toPlayerRow == 0)
				return false;
			boolean ahead = (facingDRow > 0 && toPlayerRow > 0) || (facingDRow < 0 && toPlayerRow < 0);
			if (!ahead)
				return false;
			int minR = Math.min(myRow, pRow);
			int maxR = Math.max(myRow, pRow);
			for (int r = minR + 1; r < maxR; r++)
				if (map.getTile(r, myCol) == '#')
					return false;
			return true;
		}

		return false;
	}

	private void updateChase(Player player) {
		float cx = x + MazeMap.TILE_SIZE / 2f;
		float cy = y + MazeMap.TILE_SIZE / 2f;
		float px = player.getPixelX() + MazeMap.TILE_SIZE / 2f;
		float py = player.getPixelY() + MazeMap.TILE_SIZE / 2f;

		float dx = 0, dy = 0;
		if (Math.abs(cx - px) > Math.abs(cy - py))
			dx = (px > cx) ? SPEED : -SPEED;
		else
			dy = (py > cy) ? SPEED : -SPEED;

		updateAnimState(dx, dy);
		x += dx;
		y += dy;
	}

	private void updateHunt(MazeMap map, Player player) {
		int ts = MazeMap.TILE_SIZE;
		int myRow = Math.round(y) / ts;
		int myCol = Math.round(x) / ts;
		int pRow = player.getPixelY() / ts;
		int pCol = player.getPixelX() / ts;

		huntRecalcTimer--;
		if (huntRecalcTimer <= 0 || path.isEmpty()) {
			bfsRecalculate(map, myRow, myCol, pRow, pCol);
			huntRecalcTimer = HUNT_RECALC_INTERVAL;
		}

		if (path.isEmpty()) {
			updateRandomWalk(map);
			return;
		}

		int[] next = path.peek();
		float targetX = next[1] * ts;
		float targetY = next[0] * ts;
		float diffX = targetX - x;
		float diffY = targetY - y;
		float dx = 0, dy = 0;

		if (Math.abs(diffX) > HUNT_SPEED)
			dx = (diffX > 0) ? HUNT_SPEED : -HUNT_SPEED;
		else if (Math.abs(diffY) > HUNT_SPEED)
			dy = (diffY > 0) ? HUNT_SPEED : -HUNT_SPEED;
		else {
			x = targetX;
			y = targetY;
			path.poll();
			return;
		}

		updateAnimState(dx, dy);
		x += dx;
		y += dy;
	}

	private void bfsRecalculate(MazeMap map, int startRow, int startCol, int goalRow, int goalCol) {
		path.clear();
		int rows = map.getRows(), cols = map.getCols();
		int[][] parent = new int[rows * cols][2];
		for (int[] p : parent)
			Arrays.fill(p, -1);
		boolean[] visited = new boolean[rows * cols];
		Queue<int[]> queue = new LinkedList<>();
		visited[startRow * cols + startCol] = true;
		queue.add(new int[] { startRow, startCol });
		boolean found = false;

		outer: while (!queue.isEmpty()) {
			int[] cur = queue.poll();
			int cr = cur[0], cc = cur[1];
			for (int[] nb : new int[][] { { cr - 1, cc }, { cr + 1, cc }, { cr, cc - 1 }, { cr, cc + 1 } }) {
				int nr = nb[0], nc = nb[1];
				if (nr < 0 || nr >= rows || nc < 0 || nc >= cols)
					continue;
				if (map.getTile(nr, nc) == '#')
					continue;
				int idx = nr * cols + nc;
				if (visited[idx])
					continue;
				visited[idx] = true;
				parent[idx][0] = cr;
				parent[idx][1] = cc;
				if (nr == goalRow && nc == goalCol) {
					found = true;
					break outer;
				}
				queue.add(new int[] { nr, nc });
			}
		}

		if (!found)
			return;
		Deque<int[]> reversed = new ArrayDeque<>();
		int r = goalRow, c = goalCol;
		while (!(r == startRow && c == startCol)) {
			reversed.push(new int[] { r, c });
			int idx = r * cols + c;
			r = parent[idx][0];
			c = parent[idx][1];
		}
		while (!reversed.isEmpty())
			path.add(reversed.pop());
	}

	private boolean tryMove(MazeMap map, float dx, float dy) {
		float nx = x + dx, ny = y + dy;
		int ts = MazeMap.TILE_SIZE, inset = 4;
		int left = (int) (nx + inset) / ts, right = (int) (nx + ts - inset - 1) / ts;
		int top = (int) (ny + inset) / ts, bottom = (int) (ny + ts - inset - 1) / ts;
		for (int[] rc : new int[][] { { top, left }, { top, right }, { bottom, left }, { bottom, right } })
			if (map.getTile(rc[0], rc[1]) == '#')
				return false;
		x = nx;
		y = ny;
		return true;
	}

	private void updateAnimState(float dx, float dy) {
		if (dy < 0)
			animState = AnimationState.WALK_UP;
		else if (dy > 0)
			animState = AnimationState.WALK_DOWN;
		else if (dx < 0)
			animState = AnimationState.WALK_LEFT;
		else if (dx > 0)
			animState = AnimationState.WALK_RIGHT;
	}

	public void catapult(Player player, MazeMap map) {
		int ts = MazeMap.TILE_SIZE;
		float pushX = x - player.x, pushY = y - player.y;
		float targetX = x, targetY = y;
		if (Math.abs(pushX) >= Math.abs(pushY))
			targetX = x + (pushX > 0 ? ts : -ts);
		else
			targetY = y + (pushY > 0 ? ts : -ts);
		int col = Math.round(targetX) / ts, row = Math.round(targetY) / ts;
		if (map.getTile(row, col) != '#') {
			x = targetX;
			y = targetY;
		}
		path.clear();
		huntRecalcTimer = 0;
	}

	@Override
	public void draw(Graphics g) {
		if (huntMode) {
			int s = MazeMap.TILE_SIZE, px = getPixelX(), py = getPixelY();
			int alpha = 40 + (int) (30 * Math.abs(Math.sin(animTick * 0.3)));
			((Graphics2D) g).setColor(new Color(255, 0, 0, alpha));
			((Graphics2D) g).fillOval(px - 5, py - 5, s + 10, s + 10);
		}

		int dirIndex = switch (animState) {
		case WALK_UP -> 0;
		case WALK_DOWN -> 1;
		case WALK_LEFT -> 2;
		case WALK_RIGHT -> 3;
		default -> 1;
		};
		g.drawImage(frames[dirIndex][animFrame], getPixelX(), getPixelY(), null);
	}
}