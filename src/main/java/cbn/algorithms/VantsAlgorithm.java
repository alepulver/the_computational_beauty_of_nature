package cbn.algorithms;

// VantsAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class VantsAlgorithm extends Algorithm
{
	public VantsAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of ants", new Integer (3)));
		controls.addParameter (new Parameter ("Rule string", "10"));
		controls.addParameter (new Parameter ("Crud density", new Double (0.001)));
		controls.addParameter (new Parameter ("Number of steps", new Integer (100000)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
		controls.addParameter (new Parameter ("Color", new Boolean (true)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numAnts = controls.getIntegerParameterValue ("Number of ants");
		String ruleStr = controls.getStringParameterValue ("Rule string");
		double density = controls.getDoubleParameterValue ("Crud density");
		int maxSteps = controls.getIntegerParameterValue ("Number of steps");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		boolean color = controls.getBooleanParameterValue ("Color");
		
		int numStates = ruleStr.length();
		viewer.initialize (color, numStates);
		Dimension vsz = viewer.size();
		int rows = vsz.width, cols = vsz.height;

		Random rnd = new Random (randSeed);
		VAnt vant[] = new VAnt [numAnts];
		for (int i=0; i<numAnts; i++)
			vant[i] = new VAnt (rnd, rows, cols);
		
		int[][] world = new int [rows][cols];
		for (int r=0; running && r<rows; r++) {
			for (int c=0; running && c<cols; c++) {
				if (density > 0) {
					if (rnd.nextDouble() < density)
						world[r][c] = Math.abs (rnd.nextInt()) % numStates;
					else
						world[r][c] = 0;
				} else
					world[r][c] = 0;
				viewer.update (c, r, world[r][c]);
			}
			Thread.yield();
			viewer.repaint();
		}
		
		Point newLoc = new Point (0, 0);
		for (int step=0; running && step<maxSteps; step++) {
			for (int a=0; running && a<numAnts; a++) {
				vant[a].move (ruleStr, world, numStates, newLoc);
				viewer.update (newLoc.x, newLoc.y, world[newLoc.y][newLoc.x]);
			}
			Thread.yield();
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
