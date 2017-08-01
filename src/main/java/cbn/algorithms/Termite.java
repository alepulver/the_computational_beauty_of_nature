package cbn.algorithms;

// Termite

import java.util.Random;

import cbn.Viewer;

public class Termite {
	private static final byte[][] ANGLES = {
		{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
	};
	private static final int NUM_ANGLES = ANGLES.length;
	
	private static int rows;
	private static int cols;
	private static Random rnd;
	private static boolean[][] world;
	private static Viewer viewer;
	
	private int x;
	private int y;
	private int dir;
	private int nx;
	private int ny;
	
	public static void setup (int r, int c, Random rand, boolean[][] w, Viewer v) {
		rows   = r;
		cols   = c;
		rnd    = rand;
		world  = w;
		viewer = v;
	}
	
	public Termite() {
		x = Math.abs (rnd.nextInt()) % cols;
		y = Math.abs (rnd.nextInt()) % rows;
		dir = Math.abs(rnd.nextInt()) % NUM_ANGLES;
	}
	
	public void move() {
		nx = x;
		ny = y;
		
		dir = (dir + ((Math.abs (rnd.nextInt()) % 3) - 1) + NUM_ANGLES) % NUM_ANGLES;
		nx = (nx + ANGLES[dir][0] + cols) % cols;
		ny = (ny + ANGLES[dir][1] + rows) % rows;
		
		if (world[x][y] && !world[nx][ny]) {		// carry to empty spot
			world[x][y] = false;
			viewer.update (x, y, -1);
			x = nx;
			y = ny;
			world[x][y] = true;
			viewer.update (x, y, 1);
		} else if (world[x][y] && world[nx][ny]) {	// carry to chip spot
			dir = (dir + (NUM_ANGLES>>1)) % NUM_ANGLES;
			x = (x + ANGLES[dir][0] + cols) % cols;
			y = (y + ANGLES[dir][1] + rows) % rows;
		} else {									// not carrying
			x = nx;
			y = ny;
		}
	}
}
