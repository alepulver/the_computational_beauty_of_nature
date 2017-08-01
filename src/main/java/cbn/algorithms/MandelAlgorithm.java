package cbn.algorithms;

// MandelAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class MandelAlgorithm extends Algorithm
{
	public MandelAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of Colors", new Integer (256)));
		controls.addParameter (new Parameter ("Maximum iterations", new Integer (128)));
		controls.addParameter (new Parameter ("Cutoff", new Double (4.0)));
		controls.addParameter (new Parameter ("Center (real)", new Double (-0.75)));
		controls.addParameter (new Parameter ("Center (imag)", new Double (0.0)));
		controls.addParameter (new Parameter ("Width", new Double (4.0)));
		controls.addParameter (new Parameter ("Color", new Boolean (true)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numColors = controls.getIntegerParameterValue ("Number of Colors");
		int maxIter = controls.getIntegerParameterValue ("Maximum iterations");
		double cutoff = controls.getDoubleParameterValue ("Cutoff");
		boolean color = controls.getBooleanParameterValue ("Color");
		double ctrR = controls.getDoubleParameterValue ("Center (real)");
		double ctrI = controls.getDoubleParameterValue ("Center (imag)");
		double width = controls.getDoubleParameterValue ("Width");
		
		viewer.initialize (color, numColors);
		Dimension vsz = viewer.size();
		int rows = vsz.width, cols = vsz.height;

		int iter;
		double zr, zi, dr, di, zr0, zi0, cr, ci;
		
		dr = width / rows;
		di = width / cols;
		ci = ctrI + width/2 + di;
		for (int r=0; running && r<rows; r++) {
			ci -= di;
			cr = ctrR - width/2 - dr;
			for (int c=0; running && c<cols; c++) {
				cr += dr;
				
				zr = zi = iter = 0;
				while ((iter < maxIter) && ((zr*zr + zi*zi) < cutoff)) {
					zr0 = zr*zr - zi*zi + cr;
					zi0 = 2*zr*zi + ci;
					zr = zr0;
					zi = zi0;
					
					iter++;
				} 
				
				viewer.update (c, r, (iter==maxIter ? -1 : iter%numColors));
				if ((c % 20) == 0)
					Thread.yield();
			}
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
