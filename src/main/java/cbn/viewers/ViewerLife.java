package cbn.viewers;

// ViewerLife

import java.awt.*;

import cbn.CBNApplication;

public class ViewerLife extends Viewer2D {
	private int factor;
	
	public ViewerLife (CBNApplication applet, int factor) {
		super (applet);
		int w = Integer.parseInt (applet.getParameter ("viewer width"));
		int h = Integer.parseInt (applet.getParameter ("viewer height"));
		resize (w, h);
		this.factor = factor;
	}
	
	public void update (int x, int y, int color) {
		g.setColor (color>0 ? colorMap[color] : Color.black);
		g.fillRect (x*factor, y*factor, factor, factor);
	}
	
	public int getFactor() {
		return factor;
	}
}
