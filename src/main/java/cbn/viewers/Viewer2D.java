package cbn.viewers;

// Viewer2D

import java.awt.*;
import java.awt.image.BufferedImage;

import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Viewer;

public class Viewer2D extends Viewer
{
	protected Color[] colorMap;
	private BufferedImage image;
	protected Graphics g;
	private boolean resourcesInUse;
	
	public Viewer2D (CBNApplication applet) {
		super (applet);
		int w = Integer.parseInt (applet.getParameter ("viewer width"));
		int h = Integer.parseInt (applet.getParameter ("viewer height"));
		resize (w, h);
	}
	
	public synchronized void initialize (boolean color, int numColors) {
	    try {
	      	if (resourcesInUse) wait();
	    } catch (InterruptedException e) { }
	    
		Dimension size = size();
		int w=size.width, h=size.height;
		
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = image.getGraphics();
		resourcesInUse = true;
		
		ControlPanel controlPanel = applet.getControlPanel();
		
		g.setColor (Color.black);
		g.fillRect (0, 0, w, h);
		
		initColorMap (color, numColors);
	}
	
	public void update (int x, int y, int color) {
		g.setColor (color>0 ? colorMap[color] : Color.black);
		g.drawLine (x, y, x, y);
	}
	
	public void updateLine (int x, int y, int x1, int y1, int color) {
		g.setColor (color>0 ? colorMap[color] : Color.black);
		g.drawLine (x, y, x1, y1);
	}
	
	public synchronized void freeResources() {
		g.dispose();
		image.flush();
		resourcesInUse = false;
		notify();
	}
	
	public void update (Graphics g) {
		paint (g);
	}
	
	public void paint (Graphics g) {
		if (image != null)
			g.drawImage (image, 0, 0, this);
	}
	
	private void initColorMap (boolean color, int numColors) {
		float f;
		
		colorMap = new Color [numColors];
		for (int i=0; i<numColors; i++) {
			f = (1.0f * i) / (numColors-1);
			if (color)
				colorMap[i] = Color.getHSBColor (f, 1.0f, 1.0f);
			else
				colorMap[i] = new Color (f, f, f); // grey-scale
		}
		if ((colorMap[numColors-1].getRed() == 0) &&
			(colorMap[numColors-1].getGreen() == 0) &&
			(colorMap[numColors-1].getBlue() == 0)) {
			colorMap[numColors-1] = colorMap[0];
		}
	}
}
