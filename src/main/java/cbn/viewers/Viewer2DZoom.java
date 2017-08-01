package cbn.viewers;

// Viewer2DZoom

import java.awt.*;

import cbn.CBNApplication;
import cbn.ControlPanel;

public class Viewer2DZoom extends Viewer2D {
  protected int ctrX;
  protected int ctrY;
  protected int width;
  protected Rectangle zoomBox;

  public Viewer2DZoom (CBNApplication applet) {
    super (applet);
  }

  public boolean mouseDown (Event evt, int x, int y) {
    ctrX = x;
    ctrY = y;
    width = 0;
    zoomBox = new Rectangle (x, y, 0, 0);

    return true;
  }

  public boolean mouseDrag (Event evt, int x, int y) {
    width = (Math.abs (ctrX - x)) << 1;

    zoomBox.x = ctrX - (width >> 1);
    zoomBox.y = ctrY - (width >> 1);
    zoomBox.width = zoomBox.height = width;

    repaint();
    return true;
  }

  public boolean mouseUp (Event evt, int x, int y) {
    ControlPanel panel = applet.getControlPanel();

    Dimension sz = size();
    double oldCtrR  = panel.getDoubleParameterValue ("Center (real)");
    double oldCtrI  = panel.getDoubleParameterValue ("Center (imag)");
    double oldWidth = panel.getDoubleParameterValue ("Width");

    double newCtrR  = oldCtrR - (oldWidth/2) + (ctrX * (oldWidth/sz.width));
    double newCtrI  = oldCtrI + (oldWidth/2) - (ctrY * (oldWidth/sz.height));
    double newWidth = (width * oldWidth) / sz.width;

    panel.setDoubleParameterValue ("Center (real)", newCtrR);
    panel.setDoubleParameterValue ("Center (imag)", newCtrI);
    panel.setDoubleParameterValue ("Width", newWidth);

    zoomBox = null;

    return true;
  }

  public void paint (Graphics g) {
    super.paint (g);

    if (zoomBox != null) {
      g.setXORMode (Color.white);
      g.drawRect (zoomBox.x, zoomBox.y, zoomBox.width, zoomBox.height);
    }
  }
}
