package cbn.algorithms;

// TermitesAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;
import cbn.viewers.ViewerLife;

public class TermitesAlgorithm extends Algorithm
{
	public TermitesAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of termites", new Integer (5)));
		controls.addParameter (new Parameter ("Chip density", new Double (0.3)));
		controls.addParameter (new Parameter ("Number of steps", new Integer (100000)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numTermites = controls.getIntegerParameterValue ("Number of termites");
		double density = controls.getDoubleParameterValue ("Chip density");
		int maxSteps = controls.getIntegerParameterValue ("Number of steps");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int factor = ((ViewerLife) viewer).getFactor();
		int rows = vsz.width / factor, cols = vsz.height / factor;
		
		Random rnd = new Random (randSeed);
		boolean[][] world = new boolean [rows][cols];
		
		Termite.setup (rows, cols, rnd, world, viewer);
		Termite termite[] = new Termite [numTermites];
		for (int i=0; i<numTermites; i++)
			termite[i] = new Termite();
		
	// initialize chips
		
		for (int r=0; running && r<rows; r++) {
			for (int c=0; running && c<cols; c++) {
				world[r][c] = (rnd.nextDouble() < density);
				if (world[r][c]) viewer.update (c, r, 1);
			}
			Thread.yield();
			viewer.repaint();
		}

	// simulated life
		
		for (int step=0; running && step<maxSteps; step++) {
			for (int t=0; running && t<numTermites; t++) {
				termite[t].move();
			}
			Thread.yield();
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
