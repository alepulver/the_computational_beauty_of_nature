package cbn.algorithms;

// Phase1DAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class Phase1DAlgorithm extends Algorithm
{
	public Phase1DAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		String[] mapTypes = {"Logistic", "Tent", "Sinusoidal", "Gaussian"};
		
		controls.addParameter (new Parameter("Map type", mapTypes));
		controls.addParameter (new Parameter ("Initial skip", new Integer (0)));
		controls.addParameter (new Parameter ("Number of points", new Integer (10)));
		controls.addParameter (new Parameter ("Initial value", new Double (0.123456)));
		controls.addParameter (new Parameter ("Parameter (r)", new Double (1.0)));
		controls.addParameter (new Parameter ("Parameter (aux)", new Double (1.0)));
		controls.addParameter (new Parameter ("Second trajectory delta", new Double (0.0)));
		controls.addParameter (new Parameter ("Arrows", new Boolean (true)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int mapType = controls.getChoiceParameterValue ("Map type");
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		int numPoints = controls.getIntegerParameterValue ("Number of points");
		double x0 = controls.getDoubleParameterValue ("Initial value");
		double r = controls.getDoubleParameterValue ("Parameter (r)");
		double aux = controls.getDoubleParameterValue ("Parameter (aux)");
		double dx = controls.getDoubleParameterValue ("Second trajectory delta");
		boolean arrows = controls.getBooleanParameterValue ("Arrows");
		
		viewer.initialize (false, 4);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		double xMin = 0.0, xMax = 1.0, yMin = 0.0, yMax = 1.0;
		double colFactor = (cols - 1) / (xMax - xMin);
		double rowFactor = (rows - 1) / (yMin - yMax);
		double x, y;
		int row, col, row0=0, col0=0, rowx, rowy, colx, coly;

		// draw the map, and the line y=x
		x = xMin;
		for (col=0; col<cols; col++, x+=(1/colFactor)) {
			y = map (mapType, x, r, aux);
			row = (int) (rowFactor * (y - yMax));
			viewer.updateLine (col, row, col0, row0, 3);
			row0 = row;
			col0 = col;
		}
		viewer.updateLine (0, rows, cols, 0, 3);
		viewer.repaint();
		Thread.yield();

		// draw trajectory
		x=x0;
		for (int pt=0; pt<(numPoints+initSkip); pt++, x=y) {
			y = map (mapType, x, r, aux);
			if (pt >= initSkip) {
				rowx = (int) (rowFactor * (x - yMax));
				rowy = (int) (rowFactor * (y - yMax));
				colx = (int) (colFactor * (x - xMin));
				coly = (int) (colFactor * (y - xMin));
				viewer.updateLine (colx, rowx, colx, rowy, 2);
				viewer.updateLine (colx, rowy, coly, rowy, 2);
				if (arrows) {
					drawArrow (viewer, colx, rowx, colx, rowy, 2);
					drawArrow (viewer, colx, rowy, coly, rowy, 2);
				}
			}
		}
		viewer.repaint();
		Thread.yield();

		// draw second trajectory
		if (dx > 0) {
			x=x0+dx;
			for (int pt=0; pt<(numPoints+initSkip); pt++, x=y) {
				y = map (mapType, x, r, aux);
				if (pt >= initSkip) {
					rowx = (int) (rowFactor * (x - yMax));
					rowy = (int) (rowFactor * (y - yMax));
					colx = (int) (colFactor * (x - xMin));
					coly = (int) (colFactor * (y - xMin));
					viewer.updateLine (colx, rowx, colx, rowy, 1);
					viewer.updateLine (colx, rowy, coly, rowy, 1);
					if (arrows) {
						drawArrow (viewer, colx, rowx, colx, rowy, 1);
						drawArrow (viewer, colx, rowy, coly, rowy, 1);
					}
				}
			}
			viewer.repaint();
			Thread.yield();
		}

		viewer.freeResources();
	}
	
	private double map (int type, double x, double r, double aux) {
		double m=0;
		
		switch (type) {
		case 0: // logistic
			m = 4 * r * x * (1-x);
			break;	
		case 1: // tent
			m = (x <= 0.5) ? (2 * r * x) : (2 * r * (1-x));
			break;	
		case 2: // sinusoidal
			m = Math.sin (x * Math.PI * aux * 2.0 * r) * 0.5 + 0.5;
			break;	
		case 3: // Gaussian
			m = r * Math.exp (-aux * (x-0.5) * (x-0.5));
			break;	
		}
		
		return m;
	}
	
	private void drawArrow (Viewer viewer, int x1, int y1, int x2, int y2, int c) {
		int xm = (x1+x2)>>1, ym = (y1+y2)>>1, off=3;
		
		if ((x1==x2) && (y2>y1) && (Math.abs (y1-y2) > off)) {          // northbound
			viewer.updateLine (xm, ym, xm-off, ym-off, c);
			viewer.updateLine (xm, ym, xm+off, ym-off, c);
			viewer.updateLine (xm-off, ym-off, xm+off, ym-off, c);
		} else if ((x1==x2) && (y1>y2) && (Math.abs (y1-y2) > off)) {   // southbound
			viewer.updateLine (xm, ym, xm-off, ym+off, c);
			viewer.updateLine (xm, ym, xm+off, ym+off, c);
			viewer.updateLine (xm-off, ym+off, xm+off, ym+off, c);
		} else if ((y1==y2) && (x2>x1) && (Math.abs (x1-x2) > off)) {   // eastbound
			viewer.updateLine (xm, ym, xm-off, ym-off, c);
			viewer.updateLine (xm, ym, xm-off, ym+off, c);
			viewer.updateLine (xm-off, ym-off, xm-off, ym+off, c);
		} else if ((y1==y2) && (x1>x2) && (Math.abs (x1-x2) > off)) {   // westbound
			viewer.updateLine (xm, ym, xm+off, ym-off, c);
			viewer.updateLine (xm, ym, xm+off, ym+off, c);
			viewer.updateLine (xm+off, ym-off, xm+off, ym+off, c);
		}
	}
}
