package cbn;

// AlgorithmFactory

import cbn.algorithms.BifurcAlgorithm;
import cbn.algorithms.BoidsAlgorithm;
import cbn.algorithms.CAAlgorithm;
import cbn.algorithms.DiffuseAlgorithm;
import cbn.algorithms.Gen1DAlgorithm;
import cbn.algorithms.HPAlgorithm;
import cbn.algorithms.HenonAlgorithm;
import cbn.algorithms.HenonBifurcAlgorithm;
import cbn.algorithms.HenonControlAlgorithm;
import cbn.algorithms.HenonWarpAlgorithm;
import cbn.algorithms.IFSAlgorithm;
import cbn.algorithms.JuliaAlgorithm;
import cbn.algorithms.LifeAlgorithm;
import cbn.algorithms.LorenzAlgorithm;
import cbn.algorithms.LotkaAlgorithm;
import cbn.algorithms.LsysAlgorithm;
import cbn.algorithms.MRCMAlgorithm;
import cbn.algorithms.MackeyGlassAlgorithm;
import cbn.algorithms.MandelAlgorithm;
import cbn.algorithms.Phase1DAlgorithm;
import cbn.algorithms.RosslerAlgorithm;
import cbn.algorithms.TermitesAlgorithm;
import cbn.algorithms.VantsAlgorithm;

public class AlgorithmFactory {
	public static Algorithm createAlgorithm (int algorithmNumber, CBNApplication applet) {
		Algorithm a = null;
		
		switch (algorithmNumber) {
			case ControlPanel.ALG_DIFFUSE:
				a = new DiffuseAlgorithm(applet);
				break;
			case ControlPanel.ALG_LSYS:
				a = new LsysAlgorithm(applet);
				break;
			case ControlPanel.ALG_MRCM:
				a = new MRCMAlgorithm(applet);
				break;
			case ControlPanel.ALG_IFS:
				a = new IFSAlgorithm(applet);
				break;
			case ControlPanel.ALG_MANDEL:
				a = new MandelAlgorithm(applet);
				break;
			case ControlPanel.ALG_JULIA:
				a = new JuliaAlgorithm(applet);
				break;
			case ControlPanel.ALG_GEN1D:
				a = new Gen1DAlgorithm(applet);
				break;
			case ControlPanel.ALG_PHASE1D:
				a = new Phase1DAlgorithm(applet);
				break;
			case ControlPanel.ALG_BIFURC:
				a = new BifurcAlgorithm(applet);
				break;
			case ControlPanel.ALG_HENON:
				a = new HenonAlgorithm(applet);
				break;
			case ControlPanel.ALG_HENBIF:
				a = new HenonBifurcAlgorithm(applet);
				break;
			case ControlPanel.ALG_HENWARP:
				a = new HenonWarpAlgorithm(applet);
				break;
			case ControlPanel.ALG_LORENZ:
				a = new LorenzAlgorithm(applet);
				break;
			case ControlPanel.ALG_ROSSLER:
				a = new RosslerAlgorithm(applet);
				break;
			case ControlPanel.ALG_MG:
				a = new MackeyGlassAlgorithm(applet);
				break;
			case ControlPanel.ALG_LOTKA:
				a = new LotkaAlgorithm(applet);
				break;
			case ControlPanel.ALG_HENCON:
				a = new HenonControlAlgorithm(applet);
				break;
			case ControlPanel.ALG_CA:
				a = new CAAlgorithm(applet);
				break;
			case ControlPanel.ALG_LIFE:
				a = new LifeAlgorithm(applet);
				break;
			case ControlPanel.ALG_HP:
				a = new HPAlgorithm(applet);
				break;
			case ControlPanel.ALG_TERMITES:
				a = new TermitesAlgorithm(applet);
				break;
			case ControlPanel.ALG_VANTS:
				a = new VantsAlgorithm(applet);
				break;
			case ControlPanel.ALG_BOIDS:
				a = new BoidsAlgorithm(applet);
				break;
		}
		return a;
	}
}
