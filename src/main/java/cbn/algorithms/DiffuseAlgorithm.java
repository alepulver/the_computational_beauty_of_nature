package cbn.algorithms;

// DiffuseAlgorithm

import java.awt.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class DiffuseAlgorithm extends Algorithm
{
	public DiffuseAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of particles", 20));
		controls.addParameter (new Parameter ("Number of steps", 10000));
		controls.addParameter (new Parameter ("Random seed", 0));
		controls.addParameter (new Parameter ("Number of colors", 100));
		controls.addParameter (new Parameter ("Color", Boolean.TRUE));
		controls.addParameter (new Parameter ("Invisible particles", Boolean.TRUE));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int particles = controls.getIntegerParameterValue ("Number of particles");
		int steps = controls.getIntegerParameterValue ("Number of steps");
		int seed = controls.getIntegerParameterValue ("Random seed");
		int colors = controls.getIntegerParameterValue ("Number of colors");
		boolean color = controls.getBooleanParameterValue ("Color");
		boolean invisible = controls.getBooleanParameterValue ("Invisible particles");
		
		viewer.initialize (color, colors+1);
		Dimension vsz = viewer.size();
		int cols = vsz.width, rows = vsz.height;
		
		boolean[][] grid = new boolean [rows][cols];

		DiffuseParticle[] p = new DiffuseParticle [particles];
		DiffuseParticle.setGrid (grid);
		DiffuseParticle.setViewer (viewer, invisible);
		DiffuseParticle.setMisc (steps/colors, seed);
		for (int i=0; i<particles; i++)
			p[i] = new DiffuseParticle();
		
		grid[cols/2][rows/2] = true;
		viewer.update (cols/2, rows/2, 1);
		
		for (int step=0; running && step<steps; step++) {
			for (int part=0; part<particles; part++) {
				p[part].move (step);
			}
			Thread.yield();
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
