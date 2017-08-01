package cbn;

// Viewer

import java.awt.*;

abstract public class Viewer extends Canvas {
	protected CBNApplication applet;
	
	public Viewer (CBNApplication applet) {
		this.applet = applet;
	}
	
	abstract public void initialize (boolean color, int numColors);
	abstract public void update (int x, int y, int color);
	abstract public void updateLine (int x, int y, int x1, int y1, int color);
	abstract public void freeResources();
}
