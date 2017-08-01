package cbn.algorithms;

// LorenzAlgorithm

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;
import cbn.viewers.Viewer3D;

public class LorenzAlgorithm extends Algorithm
{
	public LorenzAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Initial skip", new Integer (2000)));
		controls.addParameter (new Parameter ("Number of points", new Integer (5000)));
		controls.addParameter (new Parameter ("A", new Double (5.0)));
		controls.addParameter (new Parameter ("B", new Double (15.0)));
		controls.addParameter (new Parameter ("C", new Double (1.0)));
		controls.addParameter (new Parameter ("Time step", new Double (0.02)));
		controls.addParameter (new Parameter ("Initial x", new Double (1)));
		controls.addParameter (new Parameter ("Initial y", new Double (1)));
		controls.addParameter (new Parameter ("Initial z", new Double (1)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		Viewer3D v3d = (Viewer3D) viewer;
		
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		int numPoints = controls.getIntegerParameterValue ("Number of points");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		double c = controls.getDoubleParameterValue ("C");
		double dt = controls.getDoubleParameterValue ("Time step");
		double x0 = controls.getDoubleParameterValue ("Initial x");
		double y0 = controls.getDoubleParameterValue ("Initial y");
		double z0 = controls.getDoubleParameterValue ("Initial z");		

		double x, y, z;
		double x1, x2, y1, y2, z1, z2;
		
		viewer.initialize (false, 2);
		v3d.clear();

		x = x0; y = y0; z = z0;

		for (int pt=0; running && pt<(numPoints+initSkip); pt++) {
		    x1 = -a * x + a * y;
		    y1 =  b * x - y - z * x;
		    z1 = -c * z + x * y;

		    x2 = -a * (dt * x1 + x) + a * (dt * y1 + y);
		    y2 =  b * (dt * x1 + x) - (dt * y1 + y) - (dt * z1 + z) * (dt * x1 + x);
		    z2 = -c * (dt * z1 + z) + (dt * x1 + x) * (dt * y1 + y);

		    x += 0.5 * dt * (x1 + x2);
		    y += 0.5 * dt * (y1 + y2);
		    z += 0.5 * dt * (z1 + z2);

		    if (pt == initSkip)
		      v3d.move (x, y, z);
		    else if (pt > initSkip)
		      v3d.vector (x, y, z);

		    if ((pt % 200) == 0) {
		      Thread.yield();
		    }
		}
		v3d.autoRanges();
		viewer.repaint();
		viewer.freeResources();
	}
}
