package cbn.algorithms;

// HenonAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class HenonAlgorithm extends Algorithm
{
	public HenonAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Initial skip", new Integer (100)));
		controls.addParameter (new Parameter ("Swap axes", new Boolean (true)));
		controls.addParameter (new Parameter ("Number of points", new Integer (1000)));
		controls.addParameter (new Parameter ("Delay", new Integer (1)));
		controls.addParameter (new Parameter ("A", new Double (1.29)));
		controls.addParameter (new Parameter ("B", new Double (0.3)));
		controls.addParameter (new Parameter ("Minimum x", new Double (-1.75)));
		controls.addParameter (new Parameter ("Maximum x", new Double (1.75)));
		controls.addParameter (new Parameter ("Minimum y", new Double (-1.75)));
		controls.addParameter (new Parameter ("Maximum y", new Double (1.75)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		boolean swapAxes = controls.getBooleanParameterValue ("Swap axes");
		int numPoints = controls.getIntegerParameterValue ("Number of points");
		int delay = controls.getIntegerParameterValue ("Delay");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		double xMin = controls.getDoubleParameterValue ("Minimum x");
		double xMax = controls.getDoubleParameterValue ("Maximum x");
		double yMin = controls.getDoubleParameterValue ("Minimum y");
		double yMax = controls.getDoubleParameterValue ("Maximum y");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		double rowFactor = (rows - 1) / (yMin - yMax);
		double colFactor = (cols - 1) / (xMax - xMin);
		double x, y, tmp, xx, yy;
		double[] hold = new double [delay];
		int h=0, row, col;
		
		x = (Math.random() - 0.5) / 5;
		y = (Math.random() - 0.5) / 5;
		for (int pts=0; running && pts<(numPoints + initSkip + delay); pts++) {
			hold[h++] = x;
			h %= delay;
			
			tmp = a - x*x + b*y;
			y = x;
			x = tmp;
			
			xx = swapAxes ? hold[h] : x;
			yy = swapAxes ? x : hold[h];

			if (pts >= (initSkip + delay)) 
				if ((xx >= xMin) && (xx <= xMax) &&
					(yy >= yMin) && (yy <= yMax)) {
					row = (int) (rowFactor * (yy - yMax));
					col = (int) (colFactor * (xx - xMin));
					viewer.update (col, row, 1);
				}
			if ((pts % 10) == 0) {
				Thread.yield();
				viewer.repaint();
			}
		}
		viewer.freeResources();
	}
}
