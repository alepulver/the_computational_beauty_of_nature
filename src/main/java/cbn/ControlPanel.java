package cbn;

// ControlPanel

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPanel;

public class ControlPanel extends JPanel
{
	private static final String[] ALGORITHMS = {
		"Diffusion Limited Aggregation",
		"L-Systems",
		"The Multiple Reduction Copy Machine",
		"Iterated Function Systems",
		"The Mandelbrot Set",
		"The Julia Set",
		"One-dimensional Time Series",
		"One-dimensional Phase Space",
		"One-dimensional Bifurcation",
		"The Henon Attractor",
		"Henon Bifurcation",
		"Henon Space Warping",
		"The Lorenz System",
		"The Rossler System",
		"The Mackey-Glass System",
		"The Lotka-Volterra System",
		"Henon Chaos Control",
		"Cellular Automata",
		"Conway's Game of Life",
		"The Hodgepodge Machine",
		"Termites",
		"Virtual Ants",
		"Boids"
	};
	
	public static final int ALG_DIFFUSE  =  0;
	public static final int ALG_LSYS     =  1;
	public static final int ALG_MRCM     =  2;
	public static final int ALG_IFS      =  3;
	public static final int ALG_MANDEL   =  4;
	public static final int ALG_JULIA    =  5;
	public static final int ALG_GEN1D    =  6;
	public static final int ALG_PHASE1D  =  7;
	public static final int ALG_BIFURC   =  8;
	public static final int ALG_HENON    =  9;
	public static final int ALG_HENBIF   = 10;
	public static final int ALG_HENWARP  = 11;
	public static final int ALG_LORENZ   = 12;
	public static final int ALG_ROSSLER  = 13;
	public static final int ALG_MG       = 14;
	public static final int ALG_LOTKA    = 15;
	public static final int ALG_HENCON   = 16;
	public static final int ALG_CA       = 17;
	public static final int ALG_LIFE     = 18;
	public static final int ALG_HP       = 19;
	public static final int ALG_TERMITES = 20;
	public static final int ALG_VANTS    = 21;
	public static final int ALG_BOIDS    = 22;
	
	private CBNApplication applet;
	
	private Choice algorithmSelector;
	private Panel controlsPanel;
	private Hashtable controls;
	
	private Button defaultsButton;
	private Button restartButton;
	private Button undoButton;
	
	private GridBagLayout gridBagLayout;
	private GridBagConstraints constr;
	private int rowNumber;
	
	private int currentAlgorithm;
	
	public ControlPanel (CBNApplication applet) {
		this.applet = applet;
		
		setLayout (new BorderLayout (5, 5));
		gridBagLayout = new GridBagLayout();
		
		Panel selectorPanel = new Panel();
		algorithmSelector = new Choice();
		for (int i=0; i<ALGORITHMS.length; i++)
			algorithmSelector.addItem (ALGORITHMS[i]);
		selectorPanel.add (algorithmSelector);
		
		Panel buttonPanel = new Panel();
		defaultsButton = new Button ("Defaults");
		restartButton = new Button ("Restart");
		undoButton = new Button ("Undo");
		buttonPanel.add (defaultsButton);
		buttonPanel.add (restartButton);
		buttonPanel.add (undoButton);
		
		controlsPanel = new Panel();
		controlsPanel.setLayout (gridBagLayout);
		
		add ("North", selectorPanel);
		add ("South", buttonPanel);
		add ("Center", controlsPanel);
		
		controls = new Hashtable();
		
		constr = new GridBagConstraints();
		constr.fill       = GridBagConstraints.NONE;
		constr.gridheight = 1;
		constr.gridwidth  = 1;
		constr.insets     = new Insets (3, 3, 3, 3);
		constr.ipadx      = 0;
		constr.ipady      = 0;
		constr.weightx    = 0;
		constr.weighty    = 0;
		
		currentAlgorithm = algorithmSelector.getSelectedIndex();
	}
	
	public int getSelectedAlgorithm() {
		return algorithmSelector.getSelectedIndex();
	}
	
	public void clearParameters() {
		if (controlsPanel != null) remove (controlsPanel);
		controlsPanel = new Panel();
		controlsPanel.setLayout (gridBagLayout);
		add ("Center", controlsPanel);
		controls.clear();
		rowNumber = 0;
	}
	
	public void addParameter (Parameter param) {
		Component comp;
		
		comp = param.getLabel();
		controlsPanel.add (comp);
		constr.anchor = GridBagConstraints.EAST;
		constr.gridx  = 0;
		constr.gridy  = rowNumber;
		gridBagLayout.setConstraints (comp, constr);
		
		comp = param.getControl();
		controlsPanel.add (comp);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx  = 1;
		constr.gridy  = rowNumber++;
		gridBagLayout.setConstraints (comp, constr);
		
		controls.put (param.getName(), param);
	}
	
	private Object getParameterValue (String parameterName) {
		Parameter param = (Parameter) controls.get (parameterName);
		return param.getCurrentValue();
	}
	
	public int getChoiceParameterValue (String parameterName) {
		return ((Integer) getParameterValue (parameterName)).intValue();
	}
	
	public int getIntegerParameterValue (String parameterName) {
		return ((Integer) getParameterValue (parameterName)).intValue();
	}
	
	public double getDoubleParameterValue (String parameterName) {
		return ((Double) getParameterValue (parameterName)).doubleValue();
	}
	
	public boolean getBooleanParameterValue (String parameterName) {
		return ((Boolean) getParameterValue (parameterName)).booleanValue();
	}
	
	public String getStringParameterValue (String parameterName) {
		return ((String) getParameterValue (parameterName));
	}
	
	public boolean[][] getGridParameterValue (String parameterName) {
		return ((boolean[][]) getParameterValue (parameterName));
	}
	
	private void setParameterValue (String parameterName, Object parameterValue) {
		Parameter param = (Parameter) controls.get (parameterName);
		param.setTempValue (parameterValue);
	}

    public void setIntegerParameterValue (String parameterName, int parameterValue) {
        setParameterValue (parameterName, new Integer (parameterValue));
    }

    public void setDoubleParameterValue (String parameterName, double parameterValue) {
        setParameterValue (parameterName, new Double (parameterValue));
    }

    public void setBooleanParameterValue (String parameterName, boolean parameterValue) {
        setParameterValue (parameterName, new Boolean (parameterValue));
    }

    public void setStringParameterValue (String parameterName, String parameterValue) {
        setParameterValue (parameterName, parameterValue);
    }

  // GWF: changed to allow multilinks
  public boolean handleEvent (Event evt) {
    if (evt.id == Event.ACTION_EVENT) {
      if (evt.arg.equals (defaultsButton.getLabel())) {
	processDefault();
	return true;
      } else if (evt.arg.equals (restartButton.getLabel())) {
	processRestart();
	return true;
      } else if (evt.arg.equals (undoButton.getLabel())) {
	processUndo();
	return true;
      } else {
	if (processChoice (evt.arg))
	  return true;
	else {
	  if (evt.x != 0) { // not the algorithm selector; a linked Choice
	    Component cm = (Component) evt.target;
	    if (cm instanceof Choice) {
	      Choice c = (Choice) cm;
	      Parameter pc = findChoice (c);
	      if (pc != null) {
		// GWF: begin new section
		if (pc.isMultiLink()) {
		  String[] pcls = pc.getLinks();
		  if (pcls != null) {
		    for (int i = 0; i < pcls.length; i++) {
		      String pcl = pcls[i];
		      if (pcl != null) {
			Parameter pta = (Parameter) controls.get (pcl);
			if (pta != null) {
			  int index = c.getSelectedIndex();
			  pta.setTempPreset (index);
			}
		      }
		    }
		  }
		  // GWF: end new section
		}
	      }
	    }
	  }
	  return super.handleEvent (evt);
	}
      }
    }
    return super.handleEvent (evt);
  }
	
	private Parameter findChoice (Choice c) {
		Parameter p;
		
		for (Enumeration e=controls.elements(); e.hasMoreElements(); ) {
			p = (Parameter) e.nextElement();
			if (c == p.getControl())
				return p;
		}
		return null;
	}
	
	private void processDefault() {
		Parameter p;
		
		for (Enumeration e=controls.keys(); e.hasMoreElements(); ) {
			p = (Parameter) controls.get ((String) e.nextElement());
			p.setToDefaultValue();
		}
		applet.restart();
	}
	
	private void processRestart() {
		Parameter p;
		
		for (Enumeration e=controls.keys(); e.hasMoreElements(); ) {
			p = (Parameter) controls.get ((String) e.nextElement());
			p.saveCurrentValue();
		}
		applet.restart();
	}
	
	private void processUndo() {
		Parameter p;
		
		for (Enumeration e=controls.keys(); e.hasMoreElements(); ) {
			p = (Parameter) controls.get ((String) e.nextElement());
			p.revertToSavedValue();
		}
	}
	
	private boolean processChoice (Object evtArg) {
	    int n = ALGORITHMS.length;
	    
		for (int i=0; i<n; i++)
			if (i != currentAlgorithm)
				if (evtArg.equals (ALGORITHMS[i])) {
					applet.reload();
					currentAlgorithm = i;
					return true;
				}
		return false;
	}
}
