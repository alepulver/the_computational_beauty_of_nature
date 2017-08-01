package cbn.algorithms;

// DiffuseParticle

import java.awt.*;
import java.util.*;

import cbn.Viewer;

public class DiffuseParticle {
	private static boolean[][] grid;
	private static Dimension gridSize;
	private static Point boxMin;
	private static Point boxMax;
	private static Viewer viewer;
	private static boolean invisible;
	private static int stepsPerColor;
	private static Random rnd;
	private static Point tmp;
	
	private Point loc;
	
	public static void setGrid (boolean[][] theGrid) {
		grid = theGrid;
		gridSize = new Dimension (grid.length, grid[0].length);
		boxMin = new Point (gridSize.width/2, gridSize.height/2);
		boxMax = new Point (gridSize.width/2, gridSize.height/2);
	}
	
	public static void setViewer (Viewer theViewer, boolean isInvisible) {
		viewer = theViewer;
		invisible = isInvisible;
	}

	public static void setMisc (int stepsPerCol, int seed) {
		stepsPerColor = stepsPerCol;
		rnd = new Random ((long) seed);
		tmp = new Point (0, 0);
	}

	public DiffuseParticle() {
		loc = new Point (0, 0);
		loc.x = (int) ((gridSize.width/2) +
					   ((gridSize.width/5)*rnd.nextDouble() - (gridSize.width/10)));
		loc.y = (int) ((gridSize.height/2) +
					   ((gridSize.height/5)*rnd.nextDouble() - (gridSize.height/10)));
	}
	
	public void move (int step) {
	// random steps
		tmp.x = loc.x + ((rnd.nextDouble() < 0.5) ? -1 : +1);
		tmp.y = loc.y + ((rnd.nextDouble() < 0.5) ? -1 : +1);
		
	// bounding box
		tmp.x = (tmp.x < (boxMin.x-5)) ? (boxMax.x+5) :
										 ((tmp.x > (boxMax.x+5)) ? (boxMin.x-5) : tmp.x);
		tmp.y = (tmp.y < (boxMin.y-5)) ? (boxMax.y+5) :
										 ((tmp.y > (boxMax.y+5)) ? (boxMin.y-5) : tmp.y);
	
	// bound by screen					   
		tmp.x = (tmp.x < 0) ? (gridSize.width-1) :
							  ((tmp.x > (gridSize.width-1)) ? 0 : tmp.x);
		tmp.y = (tmp.y < 0) ? (gridSize.height-1) :
							  ((tmp.y > (gridSize.height-1)) ? 0 : tmp.y);
		
	// show movement
		if (!invisible) {
			viewer.update (loc.x, loc.y, 0);
			viewer.repaint();
		}
		loc.x = tmp.x;
		loc.y = tmp.y;
		if (!invisible) {
			viewer.update (loc.x, loc.y, (step/stepsPerColor)+1);
			viewer.repaint();
		}
		
	// sticky check
		if (isStuck()) {
		// stick it to the clump
			grid[loc.x][loc.y] = true;

		// update bounding box			
			boxMin.x = Math.min (boxMin.x, loc.x);
			boxMax.x = Math.max (boxMax.x, loc.x);
			boxMin.y = Math.min (boxMin.y, loc.y);
			boxMax.y = Math.max (boxMax.y, loc.y);
		
		// plot stuck particle
			viewer.update (loc.x, loc.y, (step/stepsPerColor)+1);
			viewer.repaint();
			
		// create new particle (move this one)
		// near bounding box border
			while (grid[loc.x][loc.y]) {
				switch ((int) (4*rnd.nextDouble())) {
				case 0:
					loc.x = (int) (boxMin.x - 5*rnd.nextDouble());
					loc.y = (int) (boxMin.y + (boxMax.y-boxMin.y)*rnd.nextDouble());
					break;
				case 1:
					loc.x = (int) (boxMax.x + 5*rnd.nextDouble());
					loc.y = (int) (boxMin.y + (boxMax.y-boxMin.y)*rnd.nextDouble());
					break;
				case 2:
					loc.y = (int) (boxMin.y - 5*rnd.nextDouble());
					loc.x = (int) (boxMin.x + (boxMax.x-boxMin.x)*rnd.nextDouble());
				case 3:
					loc.y = (int) (boxMax.y + 5*rnd.nextDouble());
					loc.x = (int) (boxMin.x + (boxMax.x-boxMin.x)*rnd.nextDouble());
					break;
				}
				
			}

		// plot replacement	
			if (!invisible) {
				viewer.update (loc.x, loc.y, (step/stepsPerColor)+1);
				viewer.repaint();
			}
		}
	}
	
	private boolean isStuck() {
		int xx, yy;
		
		for (int dx=-1; dx<=1; dx++) {
			for (int dy=-1; dy<=1; dy++) {
				if ((dx==0) && (dy==0)) continue;
				xx = loc.x + dx;
				yy = loc.y + dy;
				xx = (xx < 0) ? (gridSize.width-1) : ((xx > (gridSize.width-1)) ? 0 : xx);
				yy = (yy < 0) ? (gridSize.height-1) : ((yy > (gridSize.height-1)) ? 0 : yy);
				if (grid[xx][yy]) return true;
			}
		}
		return false;
	}
}
