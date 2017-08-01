package cbn.algorithms;

// CAAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class CAAlgorithm extends Algorithm
{
	public CAAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of states", new Integer (2)));
		controls.addParameter (new Parameter ("Radius", new Integer (1)));
		controls.addParameter (new Parameter ("Rule string", "0110"));
		controls.addParameter (new Parameter ("Initial state", "11"));
		controls.addParameter (new Parameter ("Lambda", new Double (-1.0)));
		controls.addParameter (new Parameter ("Wrap at edges", new Boolean (false)));
		controls.addParameter (new Parameter ("Strong quiescence", new Boolean (false)));
		controls.addParameter (new Parameter ("Color", new Boolean (true)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
	}
	
	public void run() {
		running = true;

		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numStates = controls.getIntegerParameterValue ("Number of states");
		int radius = controls.getIntegerParameterValue ("Radius");
		String ruleStr = controls.getStringParameterValue ("Rule string");
		String initState = controls.getStringParameterValue ("Initial state");
		double lambda = controls.getDoubleParameterValue ("Lambda");
		boolean wrap = controls.getBooleanParameterValue ("Wrap at edges");
		boolean strongQ = controls.getBooleanParameterValue ("Strong quiescence");
		boolean color = controls.getBooleanParameterValue ("Color");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		
		viewer.initialize (color, color ? numStates : 2);
		Dimension vsz = viewer.size();
		int rows = vsz.width, cols = vsz.height;
		
		Random rnd = new Random (randSeed);
		int[][] states = new int [2][cols + 2*radius + 2];
		int cur=0; // old=cur
		
		int initLen = initState.length();
		if (initState.charAt (0) == '-') {
			int odds = 2;
			try {
				odds = -Integer.parseInt (initState);
			} catch (NumberFormatException nfe) {
				controls.setStringParameterValue ("Initial state", "-"+odds);
			}
			for (int i=radius+1; i<(cols+radius+1); i++)
				if ((Math.abs (rnd.nextInt()) % odds) == 0)
					states[cur][i] = (Math.abs (rnd.nextInt()) % (numStates-1)) + 1;
		} else {
			initState = validateStates (initState, numStates);
			controls.setStringParameterValue ("Initial state", initState);
			for (int i=((cols-initLen)>>1)+radius+1, j=0; j<initLen; i++, j++)
				states[cur][i] = initState.charAt (j) - '0';
		}

		if ((lambda >= 0) && (lambda <= 1)) {
			ruleStr = (String) calculateRulesOrLambda (rnd, numStates, radius, strongQ, lambda, null);
			controls.setStringParameterValue ("Rule string", ruleStr);
		} else {
			ruleStr = validateRules (ruleStr, numStates, radius);
			controls.setStringParameterValue ("Rule string", ruleStr);
			lambda = ((Double) calculateRulesOrLambda (rnd, numStates, radius, strongQ, lambda, ruleStr)).doubleValue();
		}
		
		int sum;
		for (int r=0; running && r<rows; r++) {
			if (wrap) {
				for (int j=0; running && j<radius; j++) {
					states[cur][j+1] = states[cur][cols+1+j];
					states[cur][cols+radius+1+j] = states[cur][radius+1+j];
				}
			}
			
			sum=0;
			for (int j=0; running && j<((radius<<1)+1); j++) {
				sum += states[cur][j];
			}
				
			for (int j=radius+1; running && j<(cols+radius+1); j++) {
				sum += (states[cur][j+radius] - states[cur][j-radius-1]);
				states[1-cur][j] = ruleStr.charAt (sum) - '0';
				viewer.update (j-radius-1, r, color ? states[cur][j] :
													  (states[cur][j] != 0) ? 1 : 0);
				if ((j % 20) == 0)
					Thread.yield();
			}
			viewer.repaint();
			cur = 1 - cur;
		}

		viewer.freeResources();
	}

	private String validateStates (String str, int numStates) {
	// check that each location in str is in [0..numStates-1];
	// if wrong, reset each errant position to 0
	
		StringBuffer fixedStr = new StringBuffer (str);
		int ch=0;
		int len = str.length();
		for (int i=0; i<len; i++) {
			ch = fixedStr.charAt (i) - '0';
			if ((ch < 0) || (ch >= numStates))
				fixedStr.setCharAt (i, '0');
		}
	
		return fixedStr.toString();
	}
	
	private String validateRules (String ruleStr, int numStates, int radius) {
		int area = (radius<<1)+1;
		int len = (numStates-1)*area+1;
		StringBuffer buf = new StringBuffer();
		
		for (int i=0; i<len; i++)
			buf.append ('0');
	
	// check for correct rule string length;
	// if wrong, return 0 string of correct length
	
		if (ruleStr.length() != len)
			return buf.toString();

		return validateStates (ruleStr, numStates);
	}
	
	private Object calculateRulesOrLambda (Random rnd, int numStates, int radius, boolean strongQ,
										   double lambda, String ruleStr) {
		int area = (radius<<1)+1;
		int len = (numStates-1)*area+1;
		int n = (int) Math.pow (numStates, area);
		int[][] table = new int[area][len];
		int sum;
		
		for (int i=0; i<area; i++)
			table[0][i] = ((i<numStates) ? 1 : 0);
		for (int i=1; i<area; i++) {
			for (int j=0; j<len; j++) {
				sum=0;
				for (int k=0; k<numStates; k++) {
					if ((j-k) >= 0)
						sum += table[i-1][j-k];
					table[i][j] = sum;
				}
			}
		}
		double[] vals = new double [len];
		for (int i=0; i<len; i++)
			vals[i] = table[area-1][i] / (double) n;

	// (calculate lambda given rule)
	
		if (ruleStr != null) {
			double newlambda = 0;
			for (int i=0; i<len; i++)
				if (ruleStr.charAt (i) != '0')
					newlambda += vals[i];
			return new Double (newlambda);
		}
		
	// (randomly generate rule given lambda)

		int[] bits = new int [len];
		for (int i=0; i<len; i++)
			bits[i] = Math.abs (rnd.nextInt()) % 2;
		
		if (strongQ) {
			bits[0] = 0;
			for (int i=0; i<numStates; i++)
				bits[i*area] = 1;
		}
		
		double oldLambda=0, newLambda=0;
		for (int i=0; i<len; i++)
			oldLambda += vals[i]*bits[i];

		int a, b, aVal, bVal;
		int noImprove=0;
		while (true) {
			while (true) {
				a = Math.abs (rnd.nextInt()) % len;
				if (strongQ && ((a % area) == 0)) continue;
				aVal = Math.abs (rnd.nextInt()) % 2;
				break;
			}		
			while (true) {
				b = Math.abs (rnd.nextInt()) % len;
				if (strongQ && ((b % area) == 0)) continue;
				bVal = Math.abs (rnd.nextInt()) % 2;
				break;
			}
			newLambda = oldLambda + (aVal-bits[a])*vals[a] +
									(bVal-bits[b])*vals[b];
			if (Math.abs (newLambda-lambda) < Math.abs (oldLambda-lambda)) {
				oldLambda = newLambda;
				bits[a] = aVal;
				bits[b] = bVal;
				noImprove=0;
			} else
				noImprove++;
				
			if (noImprove >= 1000) break;
		}
		StringBuffer newRules = new StringBuffer();
		for (int i=0; i<len; i++)
			newRules.append ((char) ((bits[i] != 0) ?
									 ((Math.abs (rnd.nextInt()) % (numStates-1)) + '1') :
									 '0'));
		if (strongQ)
			for (int i=1; i<numStates; i++)
				newRules.setCharAt (i*area, (char) (i+'0'));
		return newRules.toString();
	}
}
