package cbn.algorithms;

// LifeAlgorithm

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;
import cbn.viewers.ViewerLife;

public class LifeAlgorithm extends Algorithm
{
	private static final byte LIVE = 3;
	private static final byte DIE_MIN = 2;
	private static final byte DIE_MAX = 3;
	
	private static final String[] CONFIG_NAMES = {
		"random", "p8", "12gliders", "clock", "benchmark",
		"cmu", "glider", "largefish", "gardenofeden", "oscillator1",
		"blinkerpuffer"
	};
	private static final boolean[][][] CONFIG_DATA = {
// random
{{false}},
// p8
{{false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, true, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false},
{false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false},
{false, false, true, true, false, true, false, false, false, true, false, true, true, false, false, true, true, false, true, false, false, false, true, false, true, true, false, false},
{false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false},
{false, false, false, true, true, false, false, false, false, false, true, true, false, false, false, false, true, true, false, false, false, false, false, true, true, false, false, false},
{true, true, true, false, false, true, true, true, true, true, false, false, true, true, true, true, false, false, true, true, true, true, true, false, false, true, true, true},
{true, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, true},
{false, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false}},
// 12gliders
{{false, false, true, true, false, true, true, false, false},
{false, true, false, true, false, true, false, true, false},
{true, false, false, true, false, true, false, false, true},
{false, true, false, true, false, true, false, true, false},
{false, false, true, true, false, true, true, false, false}},
// clock
{{false, false, false, false, true, true, false, false, false, false, false, false},
{false, false, false, false, true, true, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, true, true, true, true, false, false, false, false},
{true, true, false, true, false, false, true, false, true, false, false, false},
{true, true, false, true, false, true, false, false, true, false, false, false},
{false, false, false, true, false, true, false, false, true, false, true, true},
{false, false, false, true, false, false, false, false, true, false, true, true},
{false, false, false, false, true, true, true, true, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, true, true, false, false, false, false},
{false, false, false, false, false, false, true, true, false, false, false, false}},
// benchmark
{{true, true, false},
{false, true, false},
{false, true, false},
{false, true, true}},
// cmu
{{true, true, true, false, false, true, false, true, false, true, false, false, true, true, true},
{true, false, true, false, false, true, false, true, false, true, false, false, false, false, true},
{true, false, true, false, false, true, true, true, true, true, false, false, true, true, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{true, true, true, false, false, false, false, false, false, false, false, false, true, true, true},
{false, false, true, false, false, false, false, false, false, false, false, false, true, false, false},
{true, true, true, false, false, false, false, false, false, false, false, false, true, true, true},
{false, false, true, false, false, false, false, false, false, false, false, false, true, false, false},
{true, true, true, false, false, false, false, false, false, false, false, false, true, true, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
{true, true, true, false, false, true, true, true, true, true, false, false, true, false, true},
{true, false, false, false, false, true, false, true, false, true, false, false, true, false, true},
{true, true, true, false, false, true, false, true, false, true, false, false, true, true, true}},
// glider
{{false, false, true},
{true, false, true},
{false, true, true}},
// largefish
{{true, true, true, true, true, true, false},
{true, false, false, false, false, false, true},
{true, false, false, false, false, false, false},
{false, true, false, false, false, false, false}},
// gardenofeden
{{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
{true, true, false, true, false, true, true, true, false, true, true, true, false, true, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true},
{true, false, true, false, true, true, true, false, true, true, true, false, true, true, true, true, false, true, true, true, false, true, false, true, false, true, false, true, false, true, false, true, false},
{true, true, true, true, true, false, true, true, true, false, true, true, true, false, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
{true, false, true, false, true, true, false, true, true, true, false, true, true, true, false, true, false, true, true, true, false, true, false, true, false, true, false, true, false, true, false, true, false},
{true, true, true, true, false, true, true, true, false, true, true, true, false, true, true, true, true, true, false, true, true, false, true, false, true, false, true, false, true, false, true, false, true},
{false, true, true, false, true, true, true, false, true, true, true, false, true, true, true, false, true, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true},
{true, true, false, true, true, false, true, true, true, false, true, true, true, false, true, true, false, true, true, true, true, false, true, false, true, false, true, false, true, false, true, false, true},
{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true}},
// oscillator1
{{true, true, false, false, false},
{true, false, true, false, false},
{false, false, false, false, false},
{false, false, true, false, true},
{false, false, false, true, true}},
// blinkerpuffer
{{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, true, false, false, true, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true},
{false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, true, false, false, false, false, true, true, true, true},
{true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, true, true, false, false, false, false, false, false},
{false, false, true, false, false, true, true, true, false, false, false, false, false, true, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false},
{true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, true, true, false, false, false, false, false, false},
{false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, true, false, false, false, false, true, true, true, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, true, false, false, true, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false},
{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, false, false, false, false, false, false, false}}
};
	
	public LifeAlgorithm (CBNApplication applet) {
		super (applet);
	}
	
	public void addParametersToControlPanel() {
		ControlPanel controls = applet.getControlPanel();
		
		controls.addParameter (new Parameter("Number of steps", new Integer (1000)));
		controls.addParameter (new Parameter ("Wrap at edges", new Boolean (true)));
		controls.addParameter (new Parameter ("Random seed", new Integer (0)));
		controls.addParameter (new Parameter ("Random density", new Double (0.1)));
		
		controls.addParameter (new Parameter ("Preset configuration", CONFIG_NAMES,
						      "Configuration data"));
		controls.addParameter (new Parameter ("Configuration data", CONFIG_DATA[0],
						      CONFIG_DATA));
	}
	
	public void run() {
		running = true;
		
		ControlPanel controls = applet.getControlPanel();
		Viewer viewer = applet.getViewer();
		
		int numSteps = controls.getIntegerParameterValue ("Number of steps");
		boolean wrap = controls.getBooleanParameterValue ("Wrap at edges");
		int randSeed = controls.getIntegerParameterValue ("Random seed");
		double density = controls.getDoubleParameterValue ("Random density");
		
		viewer.initialize (false, 2);
		Dimension vsz = viewer.size();
		int factor = ((ViewerLife) viewer).getFactor();
		int rows = vsz.width / factor, cols = vsz.height / factor;
		
	// initialize world
		
		Random rnd;
		boolean[][] world = new boolean [rows][cols];
		byte[][][] sum = new byte [2][rows][cols];
		int cur = 0;
		
		int initConfig = controls.getChoiceParameterValue ("Preset configuration");
		if (initConfig == 0) {
			rnd = new Random (randSeed);
			for (int r=0; running && r<rows; r++) {
				for (int c=0; running && c<cols; c++) {
					world[r][c] = (rnd.nextDouble() < density);
					if (world[r][c]) {
						updateCounts (sum[cur], rows, cols, r, c, wrap, true);
						viewer.update (c, r, 1);
					}
				}
				Thread.yield();
			}
		} else {
			boolean[][] configState = controls.getGridParameterValue ("Configuration data");
			loadPresetConfiguration (world, configState);
			for (int r=0; running && r<rows; r++) {
				for (int c=0; running && c<cols; c++) {
					if (world[r][c]) {
						updateCounts (sum[cur], rows, cols, r, c, wrap, true);
						viewer.update (c, r, 1);
					}
				}
				Thread.yield();
			}
		}
		viewer.repaint();

	// run life
		
		for (int step=0; running && step<numSteps; step++) {
			for (int r=0; running && r<rows; r++)
				System.arraycopy (sum[cur][r], 0, sum[1-cur][r], 0, cols);
			for (int r=0; running && r<rows; r++) {
				for (int c=0; running && c<cols; c++) {
					if ((!world[r][c]) && (sum[cur][r][c] == LIVE)) {
						world[r][c] = true;
						updateCounts (sum[1-cur], rows, cols, r, c, wrap, true);
						viewer.update (c, r, 1);
					} else if (world[r][c] &&
							   ((sum[cur][r][c] < DIE_MIN) ||
							    (sum[cur][r][c] > DIE_MAX))) {
						world[r][c] = false;
						updateCounts (sum[1-cur], rows, cols, r, c, wrap, false);
						viewer.update (c, r, 0);
					}
				}
				Thread.yield();
			}
			cur = 1 - cur;
			viewer.repaint();
		}

		viewer.freeResources();
	}

	private void loadPresetConfiguration (boolean[][] w, boolean[][] p) {
		int wr = w.length, wc = w[0].length;
		int pr = p.length, pc = p[0].length;
		int ro = (wr-pr)>>1, co = (wc-pc)>>1;
		
		for (int r=0; r<pr; r++)
			for (int c=0; c<pc; c++)
				if (p[r][c])
					w[ro+r][co+c] = true;
	}

	private void updateCounts (byte[][] s, int rows, int cols, int r, int c,
							   boolean wrap, boolean add) {
		if (wrap) {
			for (int i=-1; i<=1; i++)
				for (int j=-1; j<=1; j++)
					if ((i != 0) || (j != 0))
						s[(r+i+rows)%rows][(c+j+cols)%cols] += (byte) (add ? 1 : -1);
		} else {
			for (int i=-1; i<=1; i++)
				for (int j=-1; j<=1; j++)
					if (((i != 0) || (j != 0)) &&
					    ((r+i) >= 0) && ((r+i) < rows) &&
					    ((c+j) >= 0) && ((c+j) < cols))
							s[r+i][c+j] += (byte) (add ? 1 : -1);
		}
	}
}
