package cbn.algorithms;

// IFSAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class IFSAlgorithm extends Algorithm
{
	public IFSAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Border", new Integer (10)));
		controls.addParameter (new Parameter ("Initial skip", new Integer (50)));
		controls.addParameter (new Parameter ("Number of iterations", new Integer (10000)));
		
		controls.addParameter (new Parameter ("Affine rule",
						      MRCMAlgorithm.PRESET_RULE_NAMES,
						      "Rule data"));
		controls.addParameter (new Parameter ("Rule data",
						      MRCMAlgorithm.PRESET_RULE_DATA[0],
						      MRCMAlgorithm.PRESET_RULE_DATA,
						      true));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int border = controls.getIntegerParameterValue ("Border");
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		int numIterations = controls.getIntegerParameterValue ("Number of iterations");
		String rulesText = controls.getStringParameterValue ("Rule data");
		
		double[][] aff = MRCMAlgorithm.parseAffineTransformation (rulesText);
		int numRules = aff.length;
		double[] prob = new double [numRules];
		double sum=0;
		
		for (int i=0; i<numRules; i++) {
		        // GWF - changed Math.min to Math.max
			prob[i] = Math.max (0.01, Math.abs (aff[i][0]*aff[i][3] - aff[i][1]*aff[i][2]));
			sum += prob[i];
		}
		for (int i=0; i<numRules; i++)
			prob[i] /= sum;

		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		int boxwidth = Math.min (cols, rows) - 2*border;
		int xoff = (cols - boxwidth) / 2;
		int yoff = (rows - boxwidth) / 2;

		Random r = new Random();
		double x = r.nextDouble(),
			   y = r.nextDouble();
		int j, rr, cc;
		double q, s, t;
		
		for (int iter=0; running && iter<(initSkip+numIterations); iter++) {
			s = prob[j=0];
			q = r.nextDouble();
			while (s < q)
			  s += prob[++j];  // GWF - changed j++ to ++j
			
			t = aff[j][0]*x + aff[j][1]*y + aff[j][4];
			y = aff[j][2]*x + aff[j][3]*y + aff[j][5];
			x = t;
			
			if (iter > initSkip) {
				cc = (int) (x*(boxwidth-1) + xoff + 0.5);
				rr = rows - (int) (y*(boxwidth-1) + yoff + 0.5);
				viewer.update (cc, rr, 1);
			}
			
			if ((iter % 50) == 0) {
				Thread.yield();
				viewer.repaint();
			}
		}
		viewer.repaint();
		
		viewer.freeResources();
	}
}
