package cbn.algorithms;

// HenonControlAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class HenonControlAlgorithm extends Algorithm
{
	public HenonControlAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Initial skip", new Integer (100)));
		controls.addParameter (new Parameter ("Control on 1", new Integer (50)));
		controls.addParameter (new Parameter ("Control off", new Integer (100)));
		controls.addParameter (new Parameter ("Control on 2", new Integer (200)));
		controls.addParameter (new Parameter ("Control limit", new Double (0.2)));
		controls.addParameter (new Parameter ("Random seed", new Integer (5678)));
		controls.addParameter (new Parameter ("Gaussian noise level", new Double (0.00)));
		controls.addParameter (new Parameter ("A", new Double (1.29)));
		controls.addParameter (new Parameter ("B", new Double (0.3)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int initSkip = controls.getIntegerParameterValue ("Initial skip");
		int on1 = controls.getIntegerParameterValue ("Control on 1");
		int off = controls.getIntegerParameterValue ("Control off");
		int on2 = controls.getIntegerParameterValue ("Control on 2");
		double plimit = controls.getDoubleParameterValue ("Control limit");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		double gauss = controls.getDoubleParameterValue ("Gaussian noise level");
		double a = controls.getDoubleParameterValue ("A");
		double b = controls.getDoubleParameterValue ("B");
		
		viewer.initialize (true, 6);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;

		double yMin = -2, yMax = 2;
		double x, y, tmp, p;
		int row, r0=0, r1=0;
		
		double eu[] = new double [2];
		double es[] = new double [2];
		double gu[] = new double [2];
		double k[]  = new double [2];
		
		double xf = 0.5 * ((b-1) + Math.sqrt ((b-1)*(b-1) + 4*a));
		double lu = -xf - Math.sqrt (xf*xf + b);
		double ls = -xf + Math.sqrt (xf*xf + b);
		
		eu[0] = lu / Math.sqrt (lu*lu + 1);
		eu[1] = 1 / Math.sqrt (lu*lu + 1);
		es[0] = ls / Math.sqrt (ls*ls + 1);
		es[1] = 1 / Math.sqrt (ls*ls + 1);
		gu[0] = 1 / (eu[0] - es[0] * eu[1] / es[1]);
		gu[1] = -gu[0] * es[0] / es[1];
		k[0]  = gu[0] * -lu / gu[0];
		k[1]  = gu[1] * -lu / gu[0];
		
		Random rnd = new Random ((long) randSeed);		
		x = (rnd.nextDouble() - 0.5) / 5;
		y = (rnd.nextDouble() - 0.5) / 5;
		
		for (int r=0; r<rows; r+=5) {
			viewer.update (on1, r, 2);
			viewer.update (off, r, 5);
			viewer.update (on2, r, 2);
		}
		viewer.repaint();
		
		for (int pt=0; running && pt<(cols + initSkip); pt++) {
			if (((pt >= (initSkip + on1)) && (pt < (initSkip + off))) ||
			   (pt >= (initSkip + on2))) {
				p = k[0]*(x-xf) + k[1]*(y-xf);
				p = (Math.abs(p) > plimit) ? 0 : p;
			} else
				p = 0;
		
			tmp = a - x*x + b*y + p + gauss*rnd.nextGaussian();
			y = x + gauss*rnd.nextGaussian();
			x = tmp;
			
			if (pt >= initSkip) {
				if ((y >= yMin) && (y <= yMax)) {
					row = (int) (((rows>>1)-1) * (y - yMax) / (yMin - yMax));
					viewer.updateLine (pt-initSkip-1, r0, pt-initSkip, row, 3);
					r0 = row;
				}
				row = (int) ((rows>>1) + ((rows>>1)-1) * (p - plimit) / (-plimit - plimit));
				viewer.updateLine (pt-initSkip-1, r1, pt-initSkip, row, 1);
				r1 = row;
			}
				
			if ((pt % 50) == 0) {
				Thread.yield();
				viewer.repaint();
			}
		}
		viewer.repaint();
		viewer.freeResources();
	}
}
