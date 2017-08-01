package cbn.viewers;

// Viewer2DZoom2

import java.awt.*;

import cbn.CBNApplication;
import cbn.ControlPanel;

public class Viewer2DZoom2 extends Viewer2DZoom {
  public Viewer2DZoom2 (CBNApplication applet) {
    super (applet);
  }

  public boolean mouseUp (Event evt, int x, int y) {
    ControlPanel panel = applet.getControlPanel();

    Dimension sz = size();
    double oldMinX  = panel.getDoubleParameterValue ("Minimum x");
    double oldMaxX  = panel.getDoubleParameterValue ("Maximum x");
    double oldMinY  = panel.getDoubleParameterValue ("Minimum y");
    double oldMaxY  = panel.getDoubleParameterValue ("Maximum y");

	double oldWidth = (oldMaxX - oldMinX);
	double oldMidX = oldMinX + (oldWidth / 2);
	double oldHeight = (oldMaxY - oldMinY);
	double oldMidY = oldMinY + (oldHeight / 2);
	
    double newMidX  = oldMidX - (oldWidth/2) + (ctrX * (oldWidth/sz.width));
    double newMidY  = oldMidY + (oldHeight/2) - (ctrY * (oldHeight/sz.height));
    double newWidth = (width * oldWidth) / sz.width;
    double newHeight = (width * oldHeight) / sz.height;

	double newMinX = newMidX - newWidth/2;
	double newMaxX = newMinX + newWidth;
	double newMinY = newMidY - newHeight/2;
	double newMaxY = newMinY + newHeight;
	
    panel.setDoubleParameterValue ("Minimum x", newMinX);
    panel.setDoubleParameterValue ("Maximum x", newMaxX);
    panel.setDoubleParameterValue ("Minimum y", newMinY);
    panel.setDoubleParameterValue ("Maximum y", newMaxY);

    zoomBox = null;

    return true;
  }
}
