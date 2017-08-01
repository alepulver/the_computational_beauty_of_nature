package cbn.algorithms;

// LotkaAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class LotkaAlgorithm extends Algorithm
{
	public LotkaAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Initial fish", new Double (1.0)));
		controls.addParameter (new Parameter ("Initial sharks", new Double (0.2)));
		controls.addParameter (new Parameter ("Number of points", new Integer (2500)));
		controls.addParameter (new Parameter ("Time step", new Double (0.01)));
		controls.addParameter (new Parameter ("Random seed (or -1)", new Integer (-1)));
		controls.addParameter (new Parameter ("Fish growth rate (a)", new Double (1.5)));
		controls.addParameter (new Parameter ("Shark consumption rate (b)", new Double (1.5)));
		controls.addParameter (new Parameter ("Fish nutritional value (c)", new Double (0.5)));
		controls.addParameter (new Parameter ("Shark death rate (d)", new Double (1.5)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		double f0 = controls.getDoubleParameterValue ("Initial fish");
		double s0 = controls.getDoubleParameterValue ("Initial sharks");
		int numPoints = controls.getIntegerParameterValue ("Number of points");
		double dt = controls.getDoubleParameterValue ("Time step");
		int seed = controls.getIntegerParameterValue ("Random seed (or -1)");
		double a = controls.getDoubleParameterValue ("Fish growth rate (a)");
		double b = controls.getDoubleParameterValue ("Shark consumption rate (b)");
		double c = controls.getDoubleParameterValue ("Fish nutritional value (c)");
		double d = controls.getDoubleParameterValue ("Shark death rate (d)");
		
		viewer.initialize (true, 3);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		double xMin = 0, xMax = numPoints * dt;
		double yMin = 0, yMax = 15;
		
		double rowFactor = (rows - 1) / (yMin - yMax);
		double colFactor = (cols - 1) / (xMax - xMin);
		double t=0, f=f0, s=s0, f1, f2, s1, s2, f1t, s1t;
		int row, col;
		
		if (seed >= 0) {
			Random rnd = new Random ((long) seed);
			a = 5 * rnd.nextDouble();
			b = 5 * rnd.nextDouble();
			c = 5 * rnd.nextDouble();
			d = 5 * rnd.nextDouble();
		}
		
		for (int pts=0; running && pts<numPoints; pts++, t+=dt) {
			f1 = f * (a - b*s);
			s1 = s * (c*f - d);
			
			f1t = f + f1*dt;
			s1t = s + s1*dt;
			
			f2 = f1t * (a - b*s1t);
			s2 = s1t * (c*f1t - d);
			
			f += 0.5 * dt * (f1 + f2);
			s += 0.5 * dt * (s1 + s2);

			f = Math.max (0, f);
			s = Math.max (0, s);
			
			col = (int) (colFactor * (t - xMin));
			row = (int) (rowFactor * (f - yMax));
			viewer.update (col, row, 1);
			row = (int) (rowFactor * (s - yMax));
			viewer.update (col, row, 2);
				
			if ((pts % 10) == 0) {
				Thread.yield();
				viewer.repaint();
			}
		}
		viewer.freeResources();
	}
}
