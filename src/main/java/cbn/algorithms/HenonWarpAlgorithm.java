package cbn.algorithms;

// HenonWarpAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class HenonWarpAlgorithm extends Algorithm
{
	public HenonWarpAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Swap axes", new Boolean (true)));
		controls.addParameter (new Parameter ("Length of side", new Integer (201)));
		controls.addParameter (new Parameter ("Iterations", new Integer (1)));
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
		
		boolean swapAxes = controls.getBooleanParameterValue ("Swap axes");
		int len = controls.getIntegerParameterValue ("Length of side");
		int iterations = controls.getIntegerParameterValue ("Iterations");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		double xMin = controls.getDoubleParameterValue ("Minimum x");
		double xMax = controls.getDoubleParameterValue ("Maximum x");
		double yMin = controls.getDoubleParameterValue ("Minimum y");
		double yMax = controls.getDoubleParameterValue ("Maximum y");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		if ((len % 2) == 0) len++;
		double x, y, t, xx, yy;
		double rowFactor = (rows - 1) / (yMin - yMax);
		double colFactor = (cols - 1) / (xMax - xMin);
		int row, col;
		
		double[][] px = new double [len][len];
		double[][] py = new double [len][len];
		
		double adj = (len-1) / 2.0, radj;
		for (int r=0; running && r<len; r++) {
			radj = (adj - r) / rowFactor;
			for (int c=0; running && c<len; c++) {
				px[r][c] = (c - adj) / colFactor;
				py[r][c] = radj;
			}
		}
			
		for (int r=0; running && r<len; r++) {
			for (int c=0; running && c<len; c++) {
				x = px[r][c];
				y = py[r][c];
				
				for (int i=0; running && i<iterations; i++) {
					t = a - x*x + b*y;
					y = x;
					x = t;
				}
				xx = swapAxes ? y : x;
				yy = swapAxes ? x : y;
				
				row = (int) (rowFactor * (yy - yMax));
				col = (int) (colFactor * (xx - xMin));
				viewer.update (col, row, 1);
				if ((c % 20) == 0)
					Thread.yield();
			}
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
