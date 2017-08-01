package cbn.algorithms;

// Gen1DAlgorithm

// GWF - changed to plot lines.  also expanded x factor

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class Gen1DAlgorithm extends Algorithm
{
	public Gen1DAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		String[] mapTypes = {"Logistic", "Tent", "Sinusoidal", "Gaussian"};
		
		controls.addParameter (new Parameter("Map type", mapTypes));
		controls.addParameter (new Parameter ("Initial skip", new Integer (100)));
		controls.addParameter (new Parameter ("Initial value", new Double (0.123456)));
		controls.addParameter (new Parameter ("Parameter (r)", new Double (1.0)));
		controls.addParameter (new Parameter ("Parameter (aux)", new Double (1.0)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int mapType = controls.getChoiceParameterValue ("Map type");
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		double x0 = controls.getDoubleParameterValue ("Initial value");
		double r = controls.getDoubleParameterValue ("Parameter (r)");
		double aux = controls.getDoubleParameterValue ("Parameter (aux)");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		double x=x0;
		double yMin=0.0, yMax=1.0;
		double rowFactor = (rows - 1) / (yMin - yMax);
		int row = 0, lx = 0, ly = 0, xfactor = 4;
		
		for (int pt=0; running && pt<(initSkip+cols/xfactor); pt++) {
			x = map (mapType, x, r, aux);
			
			if (pt == initSkip) {
				row = (int) (rowFactor * (x - yMax));
				viewer.update ((pt-initSkip)*xfactor, row, 1);
				Thread.yield();
				viewer.repaint();
			}

			else if (pt > initSkip) {
				row = (int) (rowFactor * (x - yMax));
				viewer.updateLine (lx, ly, (pt-initSkip)*xfactor, row, 1);
				Thread.yield();
				viewer.repaint();
			}
			lx = (pt-initSkip)*xfactor;
			ly = row;
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
