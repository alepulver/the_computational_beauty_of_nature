package cbn.viewers;

// Viewer2DZoom

import java.awt.*;
import java.util.Vector;

import cbn.CBNApplication;
import cbn.ControlPanel;
import cbn.Viewer;

public class Viewer3D extends Viewer
{
  private boolean resourcesInUse;

  int originRadius = 0;
  private double rot[][] = new double[3][3];
  private double offset[] = new double[2];
  private double min[] = new double[3];
  private double max[] = new double[3];
  private int mx, my, kstate, mstate;
  private Vector objs;
  private Vector cube;

  private double tmprot[][] = new double[3][3];
  private double newrot[][] = new double[3][3];
  private double savrot[][] = new double[3][3];

  private Image image, offScreenImage;
  protected Graphics g, offScreenGC;

  private static final double HCAT2 = 0.5 * Math.cos(Math.atan(2.0));
  private static final double HSAT2 = 0.5 * Math.sin(Math.atan(2.0));

  /***************************************************************************/

  public Viewer3D (CBNApplication applet) {
    super (applet);
    int w = Integer.parseInt (applet.getParameter ("viewer width"));
    int h = Integer.parseInt (applet.getParameter ("viewer height"));
    resize (w, h);

    min[0] = min[1] = min[2] = -1;
    max[0] = max[1] = max[2] = 1;
    objs = new Vector();
    for(int i = 0; i < 3; i++)
      for(int j = 0; j < 3; j++)
	if(i == j && i < 3) rot[i][j] = 0.25;
	else rot[i][j] = 0.0;

    // Define a cube to display while rotating.
    cube = new Vector();
    cube.addElement(new Obj3d(Obj3d.CIRCLE, -1, -1, -1));
    cube.addElement(new Obj3d(Obj3d.MOVE, -1, -1, -1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, 1, -1, -1));
    cube.addElement(new Obj3d(Obj3d.LINE, 1, -1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, 1, 1, -1));
    cube.addElement(new Obj3d(Obj3d.LINE, 1, 1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, -1, 1, -1));
    cube.addElement(new Obj3d(Obj3d.LINE, -1, 1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, -1, -1, -1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, -1, -1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, 1, -1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, 1, 1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, -1, 1, 1));
    cube.addElement(new Obj3d(Obj3d.VECTOR, -1, -1, 1));
  }

  public synchronized void initialize (boolean color, int numColors) {
    try {
      if (resourcesInUse) wait();
    } catch (InterruptedException e) { }
	    
    Dimension size = size();
    int w=size.width, h=size.height;
		
    image = createImage (w, h);
    g = image.getGraphics();
    resourcesInUse = true;
		
    ControlPanel controlPanel = applet.getControlPanel();
		
    g.setColor (Color.black);
    g.fillRect (0, 0, w, h);
		
  }

  public synchronized void freeResources() {
    g.dispose();
    image.flush();
    resourcesInUse = false;
    notify();
  }

  /***************************************************************************/

  public void setRanges(double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax) {
    int sz = cube.size();
    for(int i = 0; i < sz; i++) {
      Obj3d obj3d = (Obj3d) cube.elementAt(i);
      for(int j = 0; j < 3; j++)
	obj3d.point[j] = (obj3d.point[j] - min[j]) / (max[j] - min[j]);
      obj3d.point[0] = obj3d.point[0] * (xmax - xmin) + xmin;
      obj3d.point[1] = obj3d.point[1] * (ymax - ymin) + ymin;
      obj3d.point[2] = obj3d.point[2] * (zmax - zmin) + zmin;
    }
    min[0] = xmin; min[1] = ymin; min[2] = zmin;
    max[0] = xmax; max[1] = ymax; max[2] = zmax;
  }

  /***************************************************************************/

  public void autoRanges() {
    double min[] = new double[3], max[] = new double[3];
    int i, j, sz = objs.size();
    Obj3d obj3d = (Obj3d) objs.elementAt(0);
    for(i = 0; i < 3; i++)
      min[i] = max[i] = obj3d.point[i];
    for(i = 1; i < sz; i++) {
      obj3d = (Obj3d) objs.elementAt(i);
      for(j = 0; j < 3; j++) {
	if(min[j] < obj3d.point[j]) min[j] = obj3d.point[j];
	if(max[j] > obj3d.point[j]) max[j] = obj3d.point[j];
      }
    }
    setRanges(min[0], max[0], min[1], max[1], min[2], max[2]);
  }

  /***************************************************************************/

  public boolean mouseDown(Event e, int x, int y) {
    mx = x; my = y;
    kstate = 0;
    mstate = 1;
    if(e.shiftDown() && e.controlDown()) kstate = 3;
    else if(e.shiftDown()) kstate = 1;
    else if(e.controlDown()) kstate = 2;
    return true;
  }

  /***************************************************************************/

  public boolean mouseDrag(Event e, int x, int y) {
    if(kstate == 0) rotate_xy(x, y, false);
    else if(kstate == 1) translate(x, y, false);
    else if(kstate == 2) scale(x, y, false);
    else rotate_z(x, y, false);
    return true;
  }

  /***************************************************************************/

  public boolean mouseUp(Event e, int x, int y) {
    if(kstate == 0) rotate_xy(x, y, true);
    else if(kstate == 1) translate(x, y, true);
    else if(kstate == 2) scale(x, y, true);
    else rotate_z(x, y, true);
    mstate = 0;
    paint(this.getGraphics());
    return true;
  }

  /***************************************************************************/

  private void matrix_mult(double a[][], double b[][], double c[][]) {
    for(int i = 0; i < 3; i++) 
      for(int j = 0; j < 3; j++) {
	double sum = 0;
	for(int k = 0; k < 3; k++)
	  sum += a[i][k] * b[k][j];
	c[i][j] = sum;
      }
  }

  /***************************************************************************/

  private void translate(int ex, int ey, boolean store) {
    double dx = (ex - mx)  / (double)this.size().width;
    double dy = (my - ey)  / (double)this.size().height;    
    offset[0] += dx;
    offset[1] += dy;
    paint(this.getGraphics());
    if(!store) {
      offset[0] -= dx;
      offset[1] -= dy;
    }
  }

  /***************************************************************************/

  private void scale(int ex, int ey, boolean store) {
    double tmprot[][] = new double[3][3];
    double newrot[][] = new double[3][3];
    double savrot[][] = new double[3][3];

    savrot = rot;
    
    double cx = (double)this.size().width / 2;
    double cy = (double)this.size().height / 2;
    double de, dm, s, norm;

    de = Math.sqrt((cx - ex) * (cx - ex) + (cy - ey)  * (cy - ey));
    dm = Math.sqrt((cx - mx) * (cx - mx) + (cy - my)  * (cy - my));
    norm = Math.sqrt(this.size().height * this.size().height +
      this.size().width * this.size().width);
    s = (de - dm) / norm + 1;
    if(s == 1 || s == 0) return;
    tmprot[0][0] = s;
    tmprot[1][1] = s;
    tmprot[2][2] = s;
    matrix_mult(tmprot, rot, newrot);
    rot = newrot;

    paint(this.getGraphics());
    if(!store) rot = savrot;
  }

  /***************************************************************************/

  private void rotate_xy(int ex, int ey, boolean store) {
    double swaprot[][];
    double angx, cx, sx;
    
    for(int i = 0; i < 3; i++)
      for(int j = 0; j < 3; j++)
	savrot[i][j] = rot[i][j];

    angx = (mx - ex) / (double)this.size().width * Math.PI * 2;
    cx = Math.cos(angx);
    sx = Math.sin(angx);
    tmprot[0][0] = cx;  tmprot[0][1] = 0; tmprot[0][2] = sx;
    tmprot[1][0] = 0;   tmprot[1][1] = 1; tmprot[1][2] = 0;
    tmprot[2][0] = -sx; tmprot[2][1] = 0; tmprot[2][2] = cx;
    matrix_mult(tmprot, rot, newrot);
    swaprot = rot; rot = newrot; newrot = swaprot;

    angx = (my - ey) / (double)this.size().height * Math.PI;
    cx = Math.cos(angx);
    sx = Math.sin(angx);
    tmprot[0][0] = 1;  tmprot[0][1] = 0;  tmprot[0][2] = 0;
    tmprot[1][0] = 0;  tmprot[1][1] = cx; tmprot[1][2] = -sx;
    tmprot[2][0] = 0;  tmprot[2][1] = sx; tmprot[2][2] = cx;
    matrix_mult(tmprot, rot, newrot);
    swaprot = rot; rot = newrot; newrot = swaprot;
 
    paint(this.getGraphics());
    if(!store)
      for(int i = 0; i < 3; i++)
	for(int j = 0; j < 3; j++)
	  rot[i][j] = savrot[i][j];
  }

  /***************************************************************************/

  private void rotate_z(int ex, int ey, boolean store) {
    double tmprot[][] = new double[3][3];
    double newrot[][] = new double[3][3];
    double savrot[][] = new double[3][3];
    double angx, cx, sx;
    
    savrot = rot;

    angx = ((mx - ex) / (double)this.size().width +
      (my - ey) / (double)this.size().height) * Math.PI * 2;
    cx = Math.cos(angx);
    sx = Math.sin(angx);
    tmprot[0][0] = cx; tmprot[0][1] = -sx;
    tmprot[1][0] = sx; tmprot[1][1] = cx;
    tmprot[2][2] = 1;
    matrix_mult(tmprot, rot, newrot);
    rot = newrot;
 
    paint(this.getGraphics());
    if(!store) rot = savrot;
  }

  /***************************************************************************/

  private void projection(double point[], double p[], Graphics g) {
    double x[] = new double[3];
    double y[] = new double[3];
    double sum, xx, yy;
    int i, j;

    // Normalize to a [-1, 1] range.
    for(i = 0; i < 3; i++)
      x[i] = 2 * (point[i] - min[i]) / (max[i] - min[i]) - 1;

    // Compute the projection
    for(i = 0; i < 3; i++) {
      sum = 0;
      for(j = 0; j < 3; j++)
	sum += rot[i][j] * x[j];
      y[i] = sum;
    }
    xx = y[0] + y[2] * HCAT2 + offset[0];
    yy = y[1] + y[2] * HSAT2 + offset[1];

    // Rescale the points to canvas space.
    p[0] = xx * (this.size().width - 1) + this.size().width / 2;
    p[1] = yy * (this.size().height - 1) + this.size().height / 2;
    p[1] = this.size().height - p[1];
  }

  /***************************************************************************/

  public void paint_helper(Graphics g, Vector dispobj) {
    int i, sz;
    double p[] = new double[2];
    double x = 0, y = 0;
    
    sz = dispobj.size();
    for(i = 0; i < sz; i++) {
      Obj3d o = (Obj3d) dispobj.elementAt(i);
      projection(o.point, p, g);
      if(o.type == o.CIRCLE) {
	if(originRadius > 0)
	  g.drawOval((int)(p[0] + 0.5) - originRadius,
		     (int)(p[1] + 0.5) - originRadius,
		     originRadius * 2, originRadius * 2);
      }
      else {
	if(o.type == o.LINE || o.type == o.VECTOR) {
	  g.drawLine((int)(x + 0.5), (int)(y + 0.5),
		     (int)(p[0] + 0.5), (int)(p[1] + 0.5));
	}
	if(o.type == o.MOVE || o.type == o.VECTOR) {
	  x = p[0]; y = p[1];
	}
      }
    }
  }

  /***************************************************************************/

  /* Override resize() for double buffering */
  public void resize(int w, int h) {
    offScreenImage = null;
    super.resize(w, h);
  }

  
  /* Override update() for double buffering */
  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    /* For double buffering */
    if (offScreenImage == null) {
      offScreenImage = createImage(size().width, size().height);
      offScreenGC = offScreenImage.getGraphics();
    }
    offScreenGC.setColor(Color.black);
    offScreenGC.fillRect(0, 0, size().width, size().height);

    offScreenGC.setColor(Color.red);
    paint_helper(offScreenGC, cube);
    offScreenGC.setColor(Color.white);
    if(mstate == 0) paint_helper(offScreenGC, objs);

    g.drawImage(offScreenImage, 0, 0, this);
  }


  /***************************************************************************/

  public void move(double x, double y, double z) {
    objs.addElement(new Obj3d(Obj3d.MOVE, x, y, z));
  }

  /***************************************************************************/

  public void vector(double x, double y, double z) {
    objs.addElement(new Obj3d(Obj3d.VECTOR, x, y, z));
  }
  /***************************************************************************/

  public void line(double x, double y, double z) {
    objs.addElement(new Obj3d(Obj3d.LINE, x, y, z));
  }

  /***************************************************************************/

  public void clear() {
    objs = new Vector();
  }

  /***************************************************************************/


  public void update (int x, int y, int color) {
  }
  
  public void updateLine (int x, int y, int x1, int y1, int color) {
  }


}

/*****************************************************************************/
