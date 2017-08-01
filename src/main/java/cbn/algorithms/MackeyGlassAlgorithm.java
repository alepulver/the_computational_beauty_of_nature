package cbn.algorithms;

// MackeyGlassAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;
import cbn.viewers.Viewer3D;

public class MackeyGlassAlgorithm extends Algorithm
{
	public MackeyGlassAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Initial skip", new Integer (2000)));
		controls.addParameter (new Parameter ("Number of points", new Integer (2000)));
		controls.addParameter (new Parameter ("Delay", new Integer (6)));
		controls.addParameter (new Parameter ("Tau", new Integer (17)));
		controls.addParameter (new Parameter ("A", new Double (0.2)));
		controls.addParameter (new Parameter ("B", new Double (0.1)));
		controls.addParameter (new Parameter ("Time step", new Double (0.5)));
		controls.addParameter (new Parameter ("Initial x", new Double (1.23456789)));
		//controls.addParameter (new Parameter ("Auto-scale factor", new Double (0.2)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		Viewer3D v3d = (Viewer3D) viewer;
		
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		int numPoints = controls.getIntegerParameterValue ("Number of points");
		int delay = controls.getIntegerParameterValue ("Delay");
		int tau = controls.getIntegerParameterValue ("Tau");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		double dt = controls.getDoubleParameterValue ("Time step");
		double x0 = controls.getDoubleParameterValue ("Initial x");
		double factor = 0.0;
		//double factor = controls.getDoubleParameterValue ("Auto-scale factor");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		int ttau = (int) (1 / dt * tau + 0.5);
		int tdt  = (int) (1 / dt * delay + 0.5);
		double x, y, z, xx=0, yy=0, zz=0;
		double xtau, xt, temp;
		double axtau, xt10p1, x1, x2;
		int hsz = Math.max (2*tdt+1, ttau+1);
		double hold[] = new double [hsz];
		int r, c, rr, cc;
		
		int h=0;
		for (int i=0; i<hsz; i++)
			hold[i] = x0;
		
		v3d.clear();

		for (int pt=0; running && pt<(numPoints+initSkip+tdt+1); pt++) {
			xtau = hold[(h + hsz - (ttau+1)) % hsz];
			xt = hold[(h + hsz -1) % hsz];
			
			axtau = a*xtau;
			xt10p1 = 1 + Math.pow (xtau, 10);
			x1 = x2 = axtau/xt10p1;
			x1 -= b*xt;
			x2 -= b*(dt * x1 + xt);
			x = xt + 0.5 * dt * (x1+x2);

			hold[h++] = x;
			h %= hsz;
			
			y = hold[(h + hsz - tdt) % hsz];
			z = hold[(h + hsz - 2 * tdt) % hsz];
					
			if (pt == (initSkip + 2 * delay + 1)) {
				v3d.move (x, y, z);
				
			}
			else if (pt > (initSkip + 2 * delay + 1)) {
				v3d.vector (x, y, z);
			}
			xx = x;
			yy = y;

			if ((pt % 200) == 0) {
				Thread.yield();
				//viewer.repaint();
			}
		}
		v3d.autoRanges();
		viewer.repaint();
		viewer.freeResources();
	}
}
