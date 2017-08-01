package cbn.algorithms;

// BifurcAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class BifurcAlgorithm extends Algorithm
{
	public BifurcAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		String[] mapTypes = {"Logistic", "Tent", "Sinusoidal", "Gaussian"};
		
		controls.addParameter (new Parameter("Map type", mapTypes));
		controls.addParameter (new Parameter ("Initial skip", new Integer (500)));
		controls.addParameter (new Parameter ("Minimum x", new Double (0.0)));
		controls.addParameter (new Parameter ("Maximum x", new Double (1.0)));
		controls.addParameter (new Parameter ("Multiplicative factor", new Double (2.0)));
		controls.addParameter (new Parameter ("Minimum y", new Double (0.0)));
		controls.addParameter (new Parameter ("Maximum y", new Double (1.0)));
		controls.addParameter (new Parameter ("Auxiliary parameter", new Double (1.0)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int mapType = controls.getChoiceParameterValue ("Map type");
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		double rMin = controls.getDoubleParameterValue ("Minimum x");
		double rMax = controls.getDoubleParameterValue ("Maximum x");
		double factor = controls.getDoubleParameterValue ("Multiplicative factor");
		double yMin = controls.getDoubleParameterValue ("Minimum y");
		double yMax = controls.getDoubleParameterValue ("Maximum y");
		double aux = controls.getDoubleParameterValue ("Auxiliary parameter");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		rMin = (rMin < 0) ? 0 : ((rMin > 1) ? 1 : rMin);
		rMax = (rMax < 0) ? 0 : ((rMax > 1) ? 1 : rMax);
		double rInc = (rMax - rMin) / (cols - 1);
		double tol = 0.01 / rows;
		double r=rMin, x;
		double xt4, xt3, xt2, xt1;
		double rowFactor = (rows - 1) / (yMin - yMax);
		int row;
		
		for (int col=0; running && col<cols; col++, r+=rInc) {
			x = 0.5;
			for (int j=0; running && j<initSkip; j++)
				x = map (mapType, x, r, aux);
			
			xt4 = 5; xt3 = 4; xt2 = 3; xt1 = 2;
			
			for (int j=0; running && j<rows*factor; j++) {
				xt4=xt3; xt3=xt2; xt2=xt1; xt1=x;
				x = map (mapType, x, r, aux);
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
}
