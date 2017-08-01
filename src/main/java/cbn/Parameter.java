package cbn;

// Parameter

// GWF: I added multilink capability
//      more changes than are noted.  Got tired.

import java.awt.*;

public class Parameter {
	private Label label;
	private Component component;
	private Object currentValue;
	private Object defaultValue;
	private String[] textAreaValues;
        private Object[] allObjValues;
        private boolean[][][] gridValues;
        private String[] AllLinks;  // GWF
        private boolean textAreaP;

  ///////////////////////////////////////////////////////////////////////////

  // GWF - all below are new versions

  public Parameter (String name, Object init) {
    this (name, init, null, false);  // simple control widget
  }

  public Parameter (String name, Object init, Object[] allvals) {
    this (name, init, allvals, false); // indirect simple control widget
  }

  public Parameter (String name, String[] names, String[] vars) {
    this (name, names, vars, false); // master mult-indirect choice widget
  }

  public Parameter (String name, String[] names, String var) {
    this (name, names, new String[] { var }, false); // master single-indirect choice widget
  }

  // GWF - revamped below to handle all cases in one function

  public Parameter (String name, Object init, Object[] allvals, boolean special) {

    this.textAreaP = special;
    label = new Label (name);

    if (init instanceof Integer)
      component = new TextField (8);
    else if (init instanceof Double)
      component = new TextField (12);
    else if (init instanceof Boolean)
      component = new Checkbox();
    else if (init instanceof boolean[][]) {
      component = new GridControl (35, 35, 4);
      gridValues = (boolean[][][]) allvals;
    }
    else if (init instanceof String && !special)
      component = new TextField (10);
    else if (init instanceof String && special) {
      component = new TextArea (4, 20);
      component.setFont (new Font ("Courier", Font.PLAIN, 10));
      textAreaValues = (String []) allvals;	
    }    
    else if (init instanceof String[]) {
      component = new Choice();
      String[] initVals = (String[]) init;
      int n = initVals.length;
      for (int i=0; i<n; i++)
	((Choice) component).addItem (initVals[i]);
      
      if (allvals instanceof String[]) { // Indirect choice object
	AllLinks = (String[]) allvals;
      }
    }
    
    if (!(init instanceof String[]))  // Object to be modified inderectly
      allObjValues = allvals;

    if (component instanceof Choice)
      defaultValue = currentValue = new Integer (0);
    else
      defaultValue = currentValue = init;

    setControl();
  }

  ///////////////////////////////////////////////////////////////////////////

	public void saveCurrentValue() {
		if (currentValue instanceof Integer) {
			if (component instanceof Choice) {
				currentValue = new Integer (((Choice) component).getSelectedIndex());
			} else {
				try {
					currentValue = Integer.valueOf (((TextField) component).getText());
				} catch (NumberFormatException nfe) {
					setControl();
				}
			}
		} else if (currentValue instanceof Double) {
			try {
				currentValue = Double.valueOf (((TextField) component).getText());
			} catch (NumberFormatException nfe) {
				setControl();
			}
	 	} else if (currentValue instanceof Boolean) {
	 		currentValue = new Boolean (((Checkbox) component).getState());
	 	} else if (currentValue instanceof String) {
			currentValue = ((TextComponent) component).getText();
	 	} else if (currentValue instanceof boolean[][]) {
	 		currentValue = ((GridControl) component).getState();
	 	}
	}
	
	public void revertToSavedValue() {
		setControl();
	}
	
	public void setToDefaultValue() {
		currentValue = defaultValue;
		setControl();
	}
	
	public Object getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue (Object newValue) {
		currentValue = newValue;
		setControl();
	}
	
	public void setTempValue (Object newValue) {
	 	if (newValue instanceof Boolean)
	 		((Checkbox) component).setState (((Boolean) newValue).booleanValue());
	 	else if (newValue instanceof boolean[][]) {
	 		((GridControl) component).setState ((boolean[][]) newValue);
			component.repaint();
	 	} else {
	 		if (component instanceof Choice)
	 			((Choice) component).select (((Integer) newValue).intValue());
	 		else
				((TextComponent) component).setText (newValue.toString());
		}
	}
	
        // GWF - changed a bit
	public void setTempPreset (int index) {
		if (textAreaValues != null)
			((TextComponent) component).setText (textAreaValues[index]);
		else if (gridValues != null) {
			((GridControl) component).setState (gridValues[index]);
			component.repaint();
		}
		else if (allObjValues != null) {
		    ((TextField) component).setText(allObjValues[index].toString());
		}
	}
	
	public String getName() {
		return label.getText();
	}
	
	public Component getLabel() {
		return label;
	}
	
	public Component getControl() {
		return component;
	}

        // GWF - changed
	public String[] getLinks() {
		return AllLinks;
	}
	
        // GWF - new
	public boolean isMultiLink() {
		return (AllLinks != null);
	}
	
	private void setControl() {
	 	if (currentValue instanceof Boolean)
	 		((Checkbox) component).setState (((Boolean) currentValue).booleanValue());
	 	else if (currentValue instanceof boolean[][]) {
	 		((GridControl) component).setState ((boolean[][]) currentValue);
			component.repaint();
	 	} else {
	 		if (component instanceof Choice)
	 			((Choice) component).select (((Integer) currentValue).intValue());
	 		else
				((TextComponent) component).setText (currentValue.toString());
		}
	}
}
