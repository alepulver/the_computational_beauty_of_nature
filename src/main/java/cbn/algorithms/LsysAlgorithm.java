package cbn.algorithms;

// LsysAlgorithm - Gary William Flake

import java.awt.*;
import java.util.*;

import cbn.Algorithm;
import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Parameter;
import cbn.Viewer;

public class LsysAlgorithm extends Algorithm
{
  public static final String[] PRESET_NAME = {
    "Big-H",
    "Two Y's",
    "Koch Island",
    "Twig",
    "Weed",
    "Bush",
    "Tree",
    "Carpet",
    "Sierpinski Arrowhead",
    "Penrose Snowflake",
    "Penrose Tiles",
    "Dragon Curve"
  };

  public static final String[] PRESET_AXIOM = {
    "[f]--f",
    "[f]4-f",
    "f++f++f",
    "f",
    "f",
    "f",
    "f",
    "f-f-f-f",
    "f",
    "f4-f4-f4-f4-f",
    "[x]++[x]++[x]++[x]++[x]",
    "f"
  };

  public static final String[] PRESET_RULES = {
    "f=|[+f][-f]",
    "f=|[+f][-f]",
    "f=f-f++f-f",
    "f=|[+f][-f]",
    "f=|[-f]|[+f][-f]f",
    "f=ff+[+f-f-f]-[-f+f+f]",
    "f=|[5+f][7-f]-|[4+f][6-f]-|[3+f][5-f]-|f",
    "f=f[f]-f+f[--f]+f-f",
    "f=[-g+++f][-g+f][gg--f]\n" + "g=gg",
    "f=f4-f4-f10-f++f4-f",
    "w=yf++zf4-xf[-yf4-wf]++\n" + "x=+yf--zf[3-wf--xf]+\n"
       + "y=-wf++xf[3+yf++zf]-\n" + "z=--yf4+wf[+zf4+xf]--xf\n" + "f=",
    "f=[+f][+g--g4-f]\n" + "g=-g++g-"
  };

  public static final Integer[] PRESET_DEPTH = {
    new Integer(8), 
    new Integer(8), 
    new Integer(4), 
    new Integer(7),
    new Integer(4),
    new Integer(4),
    new Integer(4),
    new Integer(4),
    new Integer(6),
    new Integer(4),
    new Integer(5),
    new Integer(11)
  };

  public static final Double[] PRESET_INITIAL_ANGLE = {
    new Double(0.0),
    new Double(0.0),
    new Double(90.0),
    new Double(0.0),
    new Double(0.0),
    new Double(0.0),
    new Double(0.0),
    new Double(0.0),
    new Double(90.0),
    new Double(18.0),
    new Double(0.0),
    new Double(90.0)
  };

  public static final Double[] PRESET_DELTA_ANGLE = {
    new Double(90.0),
    new Double(45.0),
    new Double(60.0),
    new Double(20.0),
    new Double(20.0),
    new Double(25.0),
    new Double(8.0),
    new Double(90.0),
    new Double(60.0),
    new Double(18.0),
    new Double(36.0),
    new Double(45.0)
  };

  public static final Double[] PRESET_DELTA_STEP = {
    new Double(0.667),
    new Double(0.667),
    new Double(1.0),
    new Double(0.5),
    new Double(0.6),
    new Double(1.0),
    new Double(0.4),
    new Double(1.0),
    new Double(1.0),
    new Double(1.0),
    new Double(1.0),
    new Double(1.0)
  };


  private int maxx = -10000, maxy = -10000, minx = 10000, miny = 10000;
  private int depth, border;
  private double a0, da, ds, unoise, x, y, a, s, xs, ys, xo, yo;
  private double xpmin, xpmax, ypmin, ypmax;
  private String rules[] = new String[26];
  private String axiom;
  private Random r = new Random();
  private int width, height;

  public LsysAlgorithm (CBNApplication applet) {
    super (applet);
  }

  public void addParametersToControlPanel() {
    ControlPanel controls = applet.getControlPanel();
    
    controls.addParameter (new Parameter("Depth", PRESET_DEPTH[0], PRESET_DEPTH));
    controls.addParameter (new Parameter ("Initial angle", PRESET_INITIAL_ANGLE[0],
					  PRESET_INITIAL_ANGLE));

    controls.addParameter (new Parameter ("Delta angle", PRESET_DELTA_ANGLE[0],
					  PRESET_DELTA_ANGLE));
    controls.addParameter (new Parameter ("Delta step size", PRESET_DELTA_STEP[0],
					  PRESET_DELTA_STEP));
    controls.addParameter (new Parameter ("Uniform noise", new  Double (0.0)));
    controls.addParameter (new Parameter ("Axiom", PRESET_AXIOM[0], PRESET_AXIOM));

    String[] links = { "Depth", "Initial angle", "Delta angle", "Delta step size",
		       "Axiom", "Rules" };

    controls.addParameter (new Parameter ("Preset Values", PRESET_NAME, links));
    controls.addParameter (new Parameter ("Rules", PRESET_RULES[0], PRESET_RULES, true));
  }

  public void run() {
    running = true;

    ControlPanel controls = applet.getControlPanel();
    Viewer viewer = applet.getViewer();
		
    depth = controls.getIntegerParameterValue ("Depth");
    border = 10;
    a0 = controls.getDoubleParameterValue ("Initial angle");
    da = controls.getDoubleParameterValue ("Delta angle");
    ds = controls.getDoubleParameterValue ("Delta step size");
    unoise = controls.getDoubleParameterValue ("Uniform noise");
    axiom = controls.getStringParameterValue ("Axiom");

    viewer.initialize (false, 2);
    Dimension vsz = viewer.size();
    width = vsz.width;
    height = vsz.height;

    char c;
    int i = 0;
    for (c = 'a'; c <= 'z'; c++)
      rules[i++] = String.valueOf(c);      

    parseRules();

    a0 = a0 * Math.PI / 180.0;
    da = da * Math.PI / 180.0;
    x = 0; y = 0; a = a0; s = 1;    

    /* Calculate the bounding box size. */
    r.setSeed(0);
    computeFigure(axiom, depth, true);

    /* Calculate two possible scalings, but pick the one that is biggest
     * for both so that the scaling is identical.
     */
    xs = (maxx - minx) / (double) width;
    ys = (maxy - miny) / (double) height;
    if(ys > xs) xs = ys; else ys = xs;

    /* Calculate offsets to maintain a correct aspect ratio with respect
     * to the screen width, height, and desired border.
     */
    if((maxx - minx) > (maxy - miny)) {
      yo = (maxx - minx - maxy + miny) - 2 * border * ys;
      xo = -2 * border * xs;
    }
    else {
      xo = (maxy - miny - maxx + minx) - 2 * border * xs;
      yo = -2 * border * ys;
    }

    /* Set the plotting ranges appropriately. */
    xpmin = xs * width + minx - xo / 2 - 0.5;
    xpmax = maxx - xs * width + xo / 2 + 0.5;
    ypmin = ys * height + miny - yo / 2 - 0.5;
    ypmax = maxy - ys * height + yo / 2 + 0.5;
    
    x = 0; y = 0; a = a0; s = 1;
    r.setSeed(0);
    computeFigure(axiom, depth, false);
    viewer.freeResources();
  }
  
  public String removeWhiteSpace(String orig) {
    int i, j = 0;
    char buf[] = new char[orig.length()+1];
    for (i = 0; i < orig.length(); i++)
      if (orig.charAt(i) != ' ')
	buf[j++] = orig.charAt(i);
    buf[j] = (char)0;
    return new String(buf);
  }

  public void parseRules() {
    char c;
    int start, i = 0;
    for (c = 'a'; c <= 'z'; c++)
      rules[i++] = String.valueOf(c);      
    
    ControlPanel controls = applet.getControlPanel();
    String newrules = new String(controls.getStringParameterValue ("Rules"));
    newrules.toLowerCase();
    newrules.trim();
    start = 0;
    for (i = 0; i < newrules.length(); i++) {
      if (newrules.charAt(i) == '\n') {
	String thisrule = newrules.substring(start, i);
	thisrule = removeWhiteSpace(thisrule);
	if (thisrule.charAt(0) >= 'a' && thisrule.charAt(0) <= 'z'
	    && thisrule.charAt(1) == '=') {
	  rules[thisrule.charAt(0) - 'a'] = thisrule.substring(2);
	}
	else {
	  System.out.print("rejected rule " + thisrule + "\n");
	}
	start = i+1;
      }
      else if (i == newrules.length() - 1) {
	String thisrule = newrules.substring(start, i + 1);
	thisrule = removeWhiteSpace(thisrule);
	if (thisrule.charAt(0) >= 'a' && thisrule.charAt(0) <= 'z'
	    && thisrule.charAt(1) == '=') {
	  rules[thisrule.charAt(0) - 'a'] = thisrule.substring(2);
	}
	else {
	  System.out.print("rejected rule " + thisrule + "\n");
	}
	start = i+1;
      }
    }
  }
  

  public void computeFigure(String rule, int d, boolean calcbounds) {
    double sx, sy, sa, ss;  /* Used to save states between calls. */
    int len, i, ax, ay, bx, by, num; /* Mostly temporary variables. */
    Viewer viewer = applet.getViewer();

    if (running) {
      Thread.yield();
      num = 0;
      len = rule.length();

      /* For each character in the rule ... */
      for (i = 0; running && i < len; i++) {
    
	/* If it is a letter or a '|' ... */
	if ((rule.charAt(i) >= 'a' && rule.charAt(i) <= 'z') || rule.charAt(i) == '|') {

	  /* For any letter, reduce the scale and recursively expand things
	   * by expanding the letter's rule.  Restore the scale afterwards.
	   */
	  if (d > 0 && rule.charAt(i) != '|') {
	    ss = s; s *= ds;
	    computeFigure(rules[rule.charAt(i) - 'a'], d - 1, calcbounds);
	    s = ss;
	  }
	
	  /* It is a command that requires movement of some form. */
	  else if (rule.charAt(i) == 'f' || rule.charAt(i) == 'g' || rule.charAt(i) == '|') {
	  
	    /* Calculate where we should step. */
	    sx = x + Math.sin(a + unoise * (2 * r.nextDouble() - 1)) * s;
	    sy = y + Math.cos(a + unoise * (2 * r.nextDouble() - 1)) * s;
						
	    /* We need to plot any 'f' or '|' commands ... */
	    if (rule.charAt(i) == 'f' || rule.charAt(i) == '|') {
	      /* Calculate the line segments two endpoints properly scaled. */
	      ax = (int)(width * x + 0.5);
	      ay = (int)(height * y + 0.5);
	      bx = (int)(width * sx + 0.5);
	      by = (int)(height * sy + 0.5);
	      
	      /* Save the boundaries if appropriate ... */
	      if (calcbounds) {
		maxx = Math.max(maxx, Math.max(width - ax, width - bx));
		maxy = Math.max(maxy, Math.max(height - ay, height - by));
		minx = Math.min(minx, Math.min(width - ax, width - bx));
		miny = Math.min(miny, Math.min(height - ay, height - by));
	      }
	      /* ... or plot the line. */
	      else {
		viewer.updateLine((int)((width - ax - xpmin) / (xpmax - xpmin) * width), 
				  (int)(((ypmin - (height - ay)) / (ypmax - ypmin) + 1.0) * height), 
				  (int)((width - bx - xpmin) / (xpmax - xpmin) * width), 
				  (int)(((ypmin - (height - by)) / (ypmax - ypmin) + 1.0) * height), 
				  1);
		Thread.yield();
		viewer.repaint();
	      }

	    }
	    /* Set the current position to the new position calculated. */
	    x = sx; y = sy;
	  }
	}
	/* If it is a number digit, then calculate the num for future use. */
	else if (rule.charAt(i) >= '0' && rule.charAt(i) <= '9')
	  num = num * 10 + (rule.charAt(i) - '0');
	
	/* If it is a turn request ... */
	else if (rule.charAt(i) == '+' || rule.charAt(i) == '-') {
	  /* ... then use any saved num and modify the angle. */
	  num = (num == 0) ? 1 : num;
	  a += (rule.charAt(i) == '+') ? num * da : num * -da;
	  num = 0;
	}
	
	/* If it is a state save request ... */
	else if (rule.charAt(i) == '[') {
	  /* ... save the state on the stack, ... */
	  sx = x; sy = y; sa = a; ss = s;
	  /* ... recursively call on the next characters, ... */
	  computeFigure(rule.substring(i + 1), d, calcbounds);
	  /* ... and restore the state. */
	  x = sx; y = sy; a = sa; s = ss; num = 1;
	  
	  /* Everything between the '[' and ']' characters has been interpreted,
	   * so gobble up everything until we see a properly nested pair of
	   * brackets.
	   */
	  do {
	    i++;
	    num = num + ((rule.charAt(i) == '[') ? 1 : (rule.charAt(i) == ']') ? -1 : 0);
	  } while(num != 0);
	}
	
	/* If it is a state restore request, then just return.  The
	 * previous state will be restored on the caller's side.
	 */
	else if(rule.charAt(i) == ']') return;
      }
    }
  }
}



