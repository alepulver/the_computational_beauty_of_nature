package cbn.algorithms;

// HPAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;
import cbn.viewers.ViewerLife;

public class HPAlgorithm extends Algorithm
{
	public HPAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of states", new Integer (100)));
		controls.addParameter (new Parameter ("Number of steps", new Integer (200)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
		controls.addParameter (new Parameter ("Color", new Boolean (false)));
		controls.addParameter (new Parameter ("Diagonal neighbors", new Boolean (false)));
		controls.addParameter (new Parameter ("Wrap around", new Boolean (true)));
		controls.addParameter (new Parameter ("Infection progress (g)", new Double (38)));
		controls.addParameter (new Parameter ("Weighting parameter (k1)", new Double (1.2)));
		controls.addParameter (new Parameter ("Weighting parameter (k2)", new Double (2)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numStates = controls.getIntegerParameterValue ("Number of states");
		int maxSteps = controls.getIntegerParameterValue ("Number of steps");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		boolean color = controls.getBooleanParameterValue ("Color");
		boolean diag = controls.getBooleanParameterValue ("Diagonal neighbors");
		boolean wrap = controls.getBooleanParameterValue ("Wrap around");
		double g = controls.getDoubleParameterValue ("Infection progress (g)");
		double k1 = controls.getDoubleParameterValue ("Weighting parameter (k1)");
		double k2 = controls.getDoubleParameterValue ("Weighting parameter (k2)");
		
		viewer.initialize (color, numStates);
		Dimension vsz = viewer.size();
		int factor = ((ViewerLife) viewer).getFactor();
		int rows = vsz.width / factor, cols = vsz.height / factor;

		Random rnd = new Random (randSeed);
		int[][][] state = new int [rows][cols][2];
		int curr = 0;
		
		for (int r=0; running && r<rows; r++) {
			for (int c=0; running && c<cols; c++) {
				state[r][c][curr] = Math.abs (rnd.nextInt()) % numStates;
				viewer.update (c, r, state[r][c][curr]);
				if ((c % 20) == 0)
					Thread.yield();
			}
			viewer.repaint();
		}
		
		int nInfected, nSick, sum;
		int sickState = numStates - 1;
		int absIndSum, nr, nc;
		
		for (int step=0; step<maxSteps; step++) {
			for (int r=0; running && r<rows; r++) {
				for (int c=0; running && c<cols; c++) {
				// calc current situation
					nInfected = nSick = sum = 0;
					for (int i=-1; i<=1; i++)
						for (int j=-1; j<=1; j++) {
							absIndSum = Math.abs(i) + Math.abs(j);
							if (absIndSum == 0) continue;
							if (!diag && (absIndSum == 2)) continue;
							nc = c + i;
							nr = r + j;
							if (!wrap && ((nc < 0) || (nc >= cols) ||
										  (nr < 0) || (nr >= rows))) continue;
							nc = (nc + cols) % cols;
							nr = (nr + rows) % rows;
							
							sum += state[nr][nc][curr];
							if (state[nr][nc][curr] == sickState) nSick++;
							else if (state[nr][nc][curr] > 0) nInfected++;
						}
						
				// update state
					if (state[r][c][curr] == 0)
						state[r][c][1-curr] = (int) (Math.floor (nInfected / k1) +
											  		 Math.floor (nSick / k2));
					else if (state[r][c][curr] < sickState)
						state[r][c][1-curr] = (int) ((sum / (nInfected+1)) + g);
					else
						state[r][c][1-curr] = 0;
						
					// GWF - changed line below
					//state[r][c][1-curr] %= numStates;
					if (state[r][c][1-curr] > numStates - 1)
					  state[r][c][1-curr] = numStates - 1;

					viewer.update (c, r, state[r][c][1-curr]);
					Thread.yield();
				}
				viewer.repaint();
			}
			curr = 1 - curr;
		}
		viewer.freeResources();
	}
}
