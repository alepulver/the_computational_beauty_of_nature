package cbn.algorithms;

// BoidsAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class BoidsAlgorithm extends Algorithm
{
	public BoidsAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of boids", new Integer (12)));
//		controls.addParameter (new Parameter ("Number of steps", new Integer (100000)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
		controls.addParameter (new Parameter ("View angle", new Double (270.0)));
		controls.addParameter (new Parameter ("Visual avoidance angle", new Double (90.0)));
		controls.addParameter (new Parameter ("Copy radius", new Double (80.0)));
		controls.addParameter (new Parameter ("Centroid radius", new Double (30.0)));
		controls.addParameter (new Parameter ("Avoidance radius", new Double (15.0)));
		controls.addParameter (new Parameter ("Visual avoidance radius", new Double (40.0)));
		controls.addParameter (new Parameter ("Copy weight", new Double (0.2)));
		controls.addParameter (new Parameter ("Centroid weight", new Double (0.4)));
		controls.addParameter (new Parameter ("Avoidance weight", new Double (1.0)));
		controls.addParameter (new Parameter ("Visual avoidance weight", new Double (0.8)));
		controls.addParameter (new Parameter ("Random weight", new Double (0.0)));
//		controls.addParameter (new Parameter ("Time step", new Double (3.0)));
//		controls.addParameter (new Parameter ("Momentum", new Double (0.95)));
//		controls.addParameter (new Parameter ("Minimum velocity", new Double (0.5)));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numBoids = controls.getIntegerParameterValue ("Number of boids");
		long maxSteps = 10000000000L; //controls.getIntegerParameterValue ("Number of steps");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		double viewA = controls.getDoubleParameterValue ("View angle");
		double vAvoidA = controls.getDoubleParameterValue ("Visual avoidance angle");
		double copyR = controls.getDoubleParameterValue ("Copy radius");
		double centroidR = controls.getDoubleParameterValue ("Centroid radius");
		double avoidR = controls.getDoubleParameterValue ("Avoidance radius");
		double vAvoidR = controls.getDoubleParameterValue ("Visual avoidance radius");
		double copyW = controls.getDoubleParameterValue ("Copy weight");
		double centroidW = controls.getDoubleParameterValue ("Centroid weight");
		double avoidW = controls.getDoubleParameterValue ("Avoidance weight");
		double vAvoidW = controls.getDoubleParameterValue ("Visual avoidance weight");
		double randW = controls.getDoubleParameterValue ("Random weight");
		double dt = 3.0; // controls.getDoubleParameterValue ("Time step");
		double ddt = 0.95; // controls.getDoubleParameterValue ("Momentum");
		double minV = 0.5; // controls.getDoubleParameterValue ("Minimum velocity");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int rows = vsz.width, cols = vsz.height;

		Random rnd = new Random (randSeed);
		
		Boid.initMisc (rnd, rows, cols, viewer, viewA, vAvoidA, minV);
		Boid.initRadii (copyR, centroidR, avoidR, vAvoidR);
		Boid.initWeights (copyW, centroidW, avoidW, vAvoidW, randW);
		Boid.initTime (dt, ddt);
		
		Boid boid[] = new Boid [numBoids];
		for (int i=0; running && i<numBoids; i++)
			boid[i] = new Boid();
		
		Boid.initPopulation (boid);
		
		for (long step=0; running && step<maxSteps; step++) {
			for (int b=0; running && b<numBoids; b++)
				boid[b].computeNewHeading (b);
			Thread.yield();

			for (int b=0; running && b<numBoids; b++)
				boid[b].update();
			Thread.yield();
			viewer.repaint();
		}
		viewer.freeResources();
	}
}
