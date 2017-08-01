package cbn;

// GridControl

import java.awt.*;

public class GridControl extends Canvas {
	private boolean[][] state;
	private int gridSize;
	private int lastR;
	private int lastC;
	private boolean[] falseRow;
	
	public GridControl (int r, int c, int s) {
		state = new boolean [r][c];
		gridSize = s;
		resize (c*s, r*s);
		falseRow = new boolean [c];
	}

	public boolean[][] getState() {
		int rows=state.length, cols=state[0].length;
		boolean[][] returnValue = new boolean [rows][cols];
		
		for (int r=0; r<rows; r++)
			System.arraycopy (state[r], 0, returnValue[r], 0, cols);
			
		return returnValue;
	}
	
	public void setState (boolean[][] newValue) {
		int wr = state.length, wc = state[0].length;
		int pr = newValue.length, pc = newValue[0].length;
		int ro = (wr-pr)>>1, co = (wc-pc)>>1;
		
		for (int r=0; r<wr; r++)
			System.arraycopy (falseRow, 0, state[r], 0, wc);
			
		for (int r=0; r<pr; r++)
			System.arraycopy (newValue[r], 0, state[ro+r], co, pc);
	}
	
	public void update (Graphics g) {
		paint (g);
	}
	
	public void paint (Graphics g) {
		int rows=state.length, cols=state[0].length;
		
		g.setColor (Color.black);
		g.fillRect (0, 0, gridSize*cols, gridSize*rows);
		
		g.setColor (Color.gray);
		for (int x=0; x<gridSize*cols; x+=gridSize) 
			g.drawLine (x, 0, x, gridSize*rows);
		for (int y=0; y<gridSize*rows; y+=gridSize)
			g.drawLine (0, y, gridSize*cols, y);
		
		g.setColor (Color.white);
		for (int r=0; r<rows; r++)
			for (int c=0; c<cols; c++)
				if (state[r][c])
					g.fillRect (gridSize*c+1, gridSize*r+1, gridSize-1, gridSize-1);
	}
	
	public boolean mouseDown (Event e, int x, int y) {
		int rows=state.length, cols=state[0].length;
		int c=x/gridSize, r=y/gridSize;
		
		if ((c >= 0) && (c < cols) &&
			(r >= 0) && (r < rows)) {
			state[r][c] = !state[r][c];
			repaint (gridSize*c+1, gridSize*r+1, gridSize-1, gridSize-1);
			lastR = r;
			lastC = c;
			return true;
		} else
			return super.mouseDrag (e, x, y);
	}
	
	public boolean mouseDrag (Event e, int x, int y) {
		int rows=state.length, cols=state[0].length;
		int c=x/gridSize, r=y/gridSize;
		
		if ((c >= 0) && (c < cols) &&
			(r >= 0) && (r < rows)) {
			if ((lastR != r) || (lastC != c)) {
				state[r][c] = !state[r][c];
				repaint (gridSize*c+1, gridSize*r+1, gridSize-1, gridSize-1);
				lastR = r;
				lastC = c;
				return true;
			} else {
				return super.mouseDrag (e, x, y);
			}
		} else {
			return super.mouseDrag (e, x, y);
		}
	}
}
