package cbn.algorithms;

// VAnt

import java.awt.*;
import java.util.*;

public class VAnt {
	private static int DIRECTIONS = 4;
	
	private Point location;
	private int direction;
	private int rows;
	private int cols;
	
	public VAnt (Random rnd, int rows, int cols) {
		location = new Point (0, 0);
		location.x = Math.abs (rnd.nextInt()) % cols;
		location.y = Math.abs (rnd.nextInt()) % rows;
		direction = Math.abs (rnd.nextInt()) % DIRECTIONS;
		this.rows = rows;
		this.cols = cols;
	}
	
	public void move (String ruleStr, int[][] world, int numStates, Point newLoc) {
		newLoc.x = location.x; newLoc.y = location.y;
		switch (direction) {
			case 0:  newLoc.y++; break;
			case 1:  newLoc.x++; break;
			case 2:  newLoc.y--; break;
			case 3:  newLoc.x--; break;
		}
		newLoc.x = (newLoc.x + cols) % cols;
		newLoc.y = (newLoc.y + rows) % rows;
		
		int old = world[newLoc.y][newLoc.x];
		world[newLoc.y][newLoc.x] = (world[newLoc.y][newLoc.x] + 1) % numStates;
		
		location.x = newLoc.x;
		location.y = newLoc.y;
		direction += (ruleStr.charAt(old) != '0') ? DIRECTIONS+1 : DIRECTIONS-1;
		direction %= DIRECTIONS;
	}
}
