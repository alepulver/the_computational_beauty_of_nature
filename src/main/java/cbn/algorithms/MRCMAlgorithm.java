package cbn.algorithms;

// MRCMAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class MRCMAlgorithm extends Algorithm
{
	public static final int AFFINE_COLUMNS = 6;
	
	public static final String[] PRESET_RULE_NAMES = {
		"crystal", "ornate", "devil", "triangle", "ice",
		"carpet", "pascal", "dragon", "fern", "snowflake",
		"tree", "twig", "weed", "pine", "xmastree"
	};
	public static final String[] PRESET_RULE_DATA = {
		// crystal
			"0.0 -0.5 0.5 0.0 0.5 0.0\n"+
			"0.0 0.5 -0.5 0.0 0.5 0.5\n"+
			"0.5 0.0 0.0 0.5 0.25 0.5",
		// ornate
			"0.333 0.0 0.0 0.333 0.333 0.666\n"+
			"0.0 0.333 1.0 0.0 0.666 0.0\n"+
			"0.0 -0.333 1.0 0.0 0.333 0.0",
		// devil
			" 0.333334 0.000000 0.000000  0.500000 0.000000 0.000000\n"+
			" 0.333334 0.000000 0.000000  0.500000 0.333334 0.000000\n"+
			" 0.333334 0.000000 0.000000  0.500000 0.666667 0.000000\n"+
			" 0.333334 0.000000 0.000000  0.500000 0.666667 0.500000\n"+
			"-0.333334 0.000000 0.000000 -0.500000 0.666667 0.500000\n"+
			"-0.333334 0.000000 0.000000 -0.500000 1.000000 0.500000",
		// triangle
			"0.5  0.0  0.0  0.5  0.0  0.0\n"+
			"0.5  0.0  0.0  0.5  0.5  0.0\n"+
			"0.5  0.0  0.0  0.5  0.0  0.5",
		// ice
			"0.750   0.000   0.000   0.750   0.125   0.125\n"+
			"0.500  -0.500   0.500   0.500   0.500   0.000\n"+
			"0.25    0.000   0.000   0.25    0.0     0.75\n"+
			"0.25    0.000   0.000   0.25    0.75    0.75\n"+
			"0.25    0.000   0.000   0.25    0.0     0.0\n"+
			"0.25    0.000   0.000   0.25    0.75    0.0",
		// carpet
			"0.5  0.0  0.0  0.5  0.0  0.0\n"+
			"0.5  0.0  0.0  0.5  0.5  0.0\n"+
			"0.0 -0.5  0.5  0.0  1.0  0.5",
		// pascal
			"0.5 0.0 0.0 0.5 0.0  0.0\n"+
			"0.5 0.0 0.0 0.5 0.5  0.0\n"+
			"0.5 0.0 0.0 0.5 0.25 0.5",
		// dragon
			" 0.000  0.577  -0.577  0.000  0.0951  0.5893\n"+
			" 0.000  0.577  -0.577  0.000  0.4413  0.7893\n"+
			" 0.000  0.577  -0.577  0.000  0.0952  0.9893",
		// fern
			" 0.849  0.037 -0.037  0.849  0.075  0.175\n"+
			" 0.197 -0.226  0.226  0.197  0.400  0.049\n"+
			"-0.150  0.283  0.260  0.237  0.575 -0.084\n"+
			" 0.000  0.000  0.000  0.165  0.500  0.000",
		// snowflake
			"0.382  0.000  0.000  0.382  0.3072  0.6190\n"+
			"0.382  0.000  0.000  0.382  0.6033  0.4044\n"+
			"0.382  0.000  0.000  0.382  0.0139  0.4044\n"+
			"0.382  0.000  0.000  0.382  0.1253  0.0595\n"+
			"0.382  0.000  0.000  0.382  0.4920  0.0595",
		// tree
			" 0.195 -0.488  0.344  0.443  0.4431  0.2453\n"+
			" 0.462  0.414 -0.252  0.361  0.2511  0.5692\n"+
			"-0.058 -0.070  0.453 -0.111  0.5976  0.0969\n"+
			"-0.035  0.070 -0.469 -0.022  0.4884  0.5069\n"+
			"-0.637  0.000  0.000  0.501  0.8562  0.2513",
		// twig
			" 0.387  0.430  0.430 -0.387  0.2560  0.5220\n"+
			" 0.441 -0.091 -0.009 -0.322  0.4219  0.5059\n"+
			"-0.468  0.020 -0.113  0.015  0.4000  0.4000",
		// weed
			"0.5    0.0   0.0  0.75  0.25  0.0\n"+
			"0.25  -0.2   0.1  0.3   0.25  0.5\n"+
			"0.25   0.2  -0.1  0.3   0.5   0.4\n"+
			"0.2    0.0   0.0  0.3   0.4   0.55",
		// pine
			"0.25  0.0  0.0   0.9   0.375  0.0\n"+
			"0.65  0.0  0.0   0.75  0.175  0.25\n"+
			"0.0  -0.5  0.25  0.0   0.5    0.2\n"+
			"0.0   0.5 -0.25  0.0   0.5    0.45",
		// xmastree
			"0.25  0.0   0.0   0.9   0.39   0.0\n"+
			"0.25  0.0   0.0   0.9   0.36   0.0\n"+
			"0.65  0.0   0.0   0.75  0.175  0.25\n"+
			"0.0  -0.5   0.25  0.0   0.5    0.2\n"+
			"0.0   0.5  -0.25  0.0   0.5    0.45"
	};
	
	public MRCMAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Depth", new Integer (4)));
		controls.addParameter (new Parameter ("Border", new Integer (10)));
		controls.addParameter (new Parameter ("Seed box width", new Double (1.0)));
		controls.addParameter (new Parameter ("Seed box height", new Double (1.0)));
		controls.addParameter (new Parameter ("Draw an L", new Boolean (false)));
		
		controls.addParameter (new Parameter ("Affine rule", PRESET_RULE_NAMES, 
						      new String[] { "Rule data" }));
		controls.addParameter (new Parameter ("Rule data", PRESET_RULE_DATA[0],
						      PRESET_RULE_DATA, true));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int depth = controls.getIntegerParameterValue ("Depth");
		int border = controls.getIntegerParameterValue ("Border");
		double bw = controls.getDoubleParameterValue ("Seed box width");
		double bh = controls.getDoubleParameterValue ("Seed box height");
		boolean drawL = controls.getBooleanParameterValue ("Draw an L");
		String rulesText = controls.getStringParameterValue ("Rule data");
		
		double[][] aff = parseAffineTransformation (rulesText);
				
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		int numPoints = drawL ? 7 : 4;
		double x[][] = new double [depth+1][numPoints];
		double y[][] = new double [depth+1][numPoints];

		int boxwidth = Math.min (cols, rows) - 2*border;
		int xoff = (cols - boxwidth) / 2;
		int yoff = (rows - boxwidth) / 2;
		
		x[0][0] = (1.+bw)/2;   y[0][0] = (1.-bh)/2;
		x[0][1] = (1.-bw)/2;   y[0][1] = (1.-bh)/2;
		x[0][2] = (1.-bw)/2;   y[0][2] = (1.+bh)/2;
		x[0][3] = (1.+bw)/2;   y[0][3] = (1.+bh)/2;
		
		if (drawL) {
			x[0][4] = 0.5;                y[0][4] = y[0][0] + 0.1*bh;
			x[0][5] = x[0][1] + 0.1*bh;   y[0][5] = y[0][4];
			x[0][6] = x[0][5];            y[0][6] = y[0][2] - 0.1*bh;
		}

		computeFigure (1, depth, rows, boxwidth, numPoints, drawL, xoff, yoff, x, y, aff);
		
		viewer.freeResources();
	}
	
	public static double[][] parseAffineTransformation (String text) {
		StringTokenizer tokens = new StringTokenizer (text);
		int numTokens = tokens.countTokens();
		int rows = numTokens / AFFINE_COLUMNS;
		if ((tokens.countTokens() % AFFINE_COLUMNS) != 0) rows++;

		double[][] aff = new double [rows][AFFINE_COLUMNS];
		
		for (int r=0; r<rows; r++)
			for (int c=0; c<AFFINE_COLUMNS; c++)
				if (tokens.hasMoreTokens())
					try {
						aff[r][c] = (Double.valueOf (tokens.nextToken())).doubleValue();
					} catch (NumberFormatException nfe) {
					}
		return aff;
	}

	private void computeFigure (int level, int depth, int rows, int boxwidth, int numPoints,
	                            boolean drawL, int xoff, int yoff, double[][] x, double[][] y,
	                            double[][] aff) {
		if (running) {
			if (level < (depth+1)) {
			
				for (int i=0; running && i<aff.length; i++) {
					for (int j=0; running && j<numPoints; j++) {
						x[level][j] = aff[i][0]*x[level-1][j] + aff[i][1]*y[level-1][j] + aff[i][4];
						y[level][j] = aff[i][2]*x[level-1][j] + aff[i][3]*y[level-1][j] + aff[i][5];
					}
					Thread.yield();
					computeFigure (level+1, depth, rows, boxwidth, numPoints, drawL, xoff, yoff, x, y, aff);
				}
				
			} else {
			
				Viewer viewer = applet.getViewer();
				int ax, ay, bx, by;
				
				for (int i=0; running && i<4; i++) {
					ax = (int) (x[level-1][i]*(boxwidth-1) + xoff + 0.5);
					ay = rows - (int) (y[level-1][i]*(boxwidth-1) + yoff + 0.5);
					bx = (int) (x[level-1][(i+1) % 4]*(boxwidth-1) + xoff + 0.5);
					by = rows - (int) (y[level-1][(i+1) % 4]*(boxwidth-1) + yoff + 0.5);
					viewer.updateLine (ax, ay, bx, by, 1);
				}
				if (drawL) {
					for (int i=4; running && i<numPoints-1; i++) {
						ax = (int) (x[level-1][i]*(boxwidth-1) + xoff + 0.5);
						ay = rows - (int) (y[level-1][i]*(boxwidth-1) + yoff + 0.5);
						bx = (int) (x[level-1][i+1]*(boxwidth-1) + xoff + 0.5);
						by = rows - (int) (y[level-1][i+1]*(boxwidth-1) + yoff + 0.5);
						viewer.updateLine (ax, ay, bx, by, 1);
					}
				}
				Thread.yield();
				viewer.repaint();
			}
		}
	}
}

