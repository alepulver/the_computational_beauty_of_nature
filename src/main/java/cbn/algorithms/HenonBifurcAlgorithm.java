package cbn.algorithms;

// HenonBifurcAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class HenonBifurcAlgorithm extends Algorithm
{
	public HenonBifurcAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		String[] plotParams = {"A", "B"};
		
		controls.addParameter (new Parameter("Initial skip", new Integer (500)));
		controls.addParameter (new Parameter ("Minimum A or B", new Double (0.0)));
		controls.addParameter (new Parameter ("Maximum A or B", new Double (1.4)));
		controls.addParameter (new Parameter ("Plot parameter", plotParams));
		controls.addParameter (new Parameter ("A", new Double (1.29)));
		controls.addParameter (new Parameter ("B", new Double (0.3)));
		controls.addParameter (new Parameter ("Multiplicative factor", new Double (2.0)));
		controls.addParameter (new Parameter ("Minimum y", new Double (-1.75)));
		controls.addParameter (new Parameter ("Maximum y", new Double (1.75)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		double abMin = controls.getDoubleParameterValue ("Minimum A or B");
		double abMax = controls.getDoubleParameterValue ("Maximum A or B");
		int plotParam = controls.getChoiceParameterValue ("Plot parameter");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		double factor = controls.getDoubleParameterValue ("Multiplicative factor");
		double yMin = controls.getDoubleParameterValue ("Minimum y");
		double yMax = controls.getDoubleParameterValue ("Maximum y");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		abMin = (abMin < 0) ? 0 : ((abMin > 2) ? 2 : abMin);
		abMax = (abMax < 0) ? 0 : ((abMax > 2) ? 2 : abMax);
		double abInc = (abMax - abMin) / (cols - 1);
		double tol = 0.01 / rows;
		double ab=abMin, x, y, t;
		double xt4, xt3, xt2, xt1;
		double rowFactor = (rows - 1) / (yMin - yMax);
		int row;
		boolean isA = (plotParam == 0);
		
		for (int col=0; running && col<cols; col++, ab+=abInc) {
			x = y = 0;
			for (int j=0; running && j<initSkip; j++) {
				if ((Math.abs (x) > 10) || (Math.abs (y) > 10)) break;
				t = (isA ? ab : a) - x*x + (isA ? b : ab) * y; y = x; x = t;
			}
			if ((Math.abs (x) > 10) || (Math.abs (y) > 10)) break;
			
			xt4 = 5; xt3 = 4; xt2 = 3; xt1 = 2;
			
			for (int j=0; running && j<rows*factor; j++) {
				xt4=xt3; xt3=xt2; xt2=xt1; xt1=x;
				t = (isA ? ab : a) - x*x + (isA ? b : ab) * y; y = x; x = t;
				row = (int) (rowFactor * (x - yMax));
				viewer.update (col, row, 1);
				if ((j % 20) == 0)
					Thread.yield();
				
				if ((Math.abs (x-xt1) < tol) || (Math.abs (x-xt2) < tol) || 
				    (Math.abs (x-xt3) < tol) || (Math.abs (x-xt4) < tol))
				    break;
			}
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
