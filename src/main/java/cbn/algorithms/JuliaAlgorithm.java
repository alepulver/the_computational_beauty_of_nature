package cbn.algorithms;

// JuliaAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class JuliaAlgorithm extends Algorithm
{
	public JuliaAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of Colors", new Integer (256)));
		controls.addParameter (new Parameter ("Maximum iterations", new Integer (128)));
		controls.addParameter (new Parameter ("Cutoff", new Double (4.0)));
		controls.addParameter (new Parameter ("Center (real)", new Double (0.0)));
		controls.addParameter (new Parameter ("Center (imag)", new Double (0.0)));
		controls.addParameter (new Parameter ("C (real)", new Double (-0.7795)));
		controls.addParameter (new Parameter ("C (imag)", new Double (0.134)));
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
		double cr = controls.getDoubleParameterValue ("C (real)");
		double ci = controls.getDoubleParameterValue ("C (imag)");
		double width = controls.getDoubleParameterValue ("Width");
		
		viewer.initialize (color, numColors);
		Dimension vsz = viewer.size();
		int rows = vsz.width, cols = vsz.height;

		int iter;
		double zr, zi, dr, di, zr0, zi0, zr1, zi1;
		
		dr = width / rows;
		di = width / cols;
		zi = ctrI + width/2 + di;
		for (int r=0; running && r<rows; r++) {
			zi -= di;
			zr = ctrR - width/2 - dr;
			for (int c=0; running && c<cols; c++) {
				zr += dr;
				
				zr1 = zr;
				zi1 = zi;
				iter = 0;
				while ((iter < maxIter) && ((zr1*zr1 + zi1*zi1) < cutoff)) {
					zr0 = zr1*zr1 - zi1*zi1 + cr;
					zi0 = 2*zr1*zi1 + ci;
					zr1 = zr0;
					zi1 = zi0;
					
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
