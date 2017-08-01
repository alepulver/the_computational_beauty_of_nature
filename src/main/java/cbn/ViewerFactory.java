package cbn;

// ViewerFactory

import cbn.viewers.Viewer2D;
import cbn.viewers.Viewer2DZoom;
import cbn.viewers.Viewer2DZoom2;
import cbn.viewers.Viewer2DZoom3;
import cbn.viewers.Viewer3D;
import cbn.viewers.ViewerLife;

public class ViewerFactory {
	public static Viewer createViewer (int algorithmNumber, CBNApplication applet) {
		Viewer v = null;
		
		switch (algorithmNumber) {
			case ControlPanel.ALG_DIFFUSE:
			case ControlPanel.ALG_LSYS:
			case ControlPanel.ALG_MRCM:
			case ControlPanel.ALG_IFS:
			case ControlPanel.ALG_GEN1D:
			case ControlPanel.ALG_PHASE1D:
			case ControlPanel.ALG_LOTKA:
			case ControlPanel.ALG_HENCON:
			case ControlPanel.ALG_CA:
			case ControlPanel.ALG_VANTS:
			case ControlPanel.ALG_BOIDS:
				v = new Viewer2D(applet);
				break;
			case ControlPanel.ALG_MANDEL:
			case ControlPanel.ALG_JULIA:
				v = new Viewer2DZoom(applet);
				break;
			case ControlPanel.ALG_BIFURC:
			case ControlPanel.ALG_HENON:
			case ControlPanel.ALG_HENWARP:
				v = new Viewer2DZoom2(applet);
				break;
			case ControlPanel.ALG_HENBIF:
				v = new Viewer2DZoom3(applet);
				break;
			case ControlPanel.ALG_LORENZ:
			case ControlPanel.ALG_ROSSLER:
			case ControlPanel.ALG_MG:
				v = new Viewer3D(applet);
				break;
			case ControlPanel.ALG_TERMITES:
				v = new ViewerLife(applet, 4);
				break;
			case ControlPanel.ALG_HP:
			case ControlPanel.ALG_LIFE:
				v = new ViewerLife (applet, 2);
				break;
		}
		return v;
	}
}
