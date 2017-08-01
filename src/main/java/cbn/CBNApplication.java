package cbn;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CBNApplication extends JFrame
{
	private ControlPanel controlPanel;
	private Algorithm algorithm;
	private Viewer viewer;
	private JPanel panel;
	
	public void init() {
		setLayout (new GridLayout(1, 2, 5, 5));

		if (controlPanel == null)
		{
			controlPanel = new ControlPanel(this);
			controlPanel.setVisible(true);
		}
		
		int bkgd = 0xFFFFFF;
		try {
			bkgd = Integer.parseInt (getParameter ("background color"), 16);
		} catch (NumberFormatException nfe) { }
		setBackground (new Color (bkgd));
	}
	
	public void start() {
        int algorithmID = controlPanel.getSelectedAlgorithm();

        algorithm = AlgorithmFactory.createAlgorithm (algorithmID, this);
        algorithm.addParametersToControlPanel();

        viewer = ViewerFactory.createViewer (algorithmID, this);
        viewer.setVisible(true);

        panel = new JPanel();
        panel.setVisible(true);
        panel.add (viewer);
        add (panel);
        add (controlPanel);

        runAlgorithmTask(algorithm);
        validate();
	}
	
	public void stop() {
        if (panel != null) {
            remove(panel);
            remove(controlPanel);
            panel = null;
        }

        if (algorithm != null) {
		    algorithm.requestStop();
		    algorithm = null;
        }

        if (viewer != null) {
		    viewer.freeResources();
		    viewer = null;
        }
	}

	public void reload() {
        stop();
        controlPanel.clearParameters();
        start();
	}
	
	public void restart() {
		int algorithmID = controlPanel.getSelectedAlgorithm();
		
		if (algorithm != null) {
		    algorithm.requestStop();
        }

 		algorithm = AlgorithmFactory.createAlgorithm (algorithmID, this);
        runAlgorithmTask(algorithm);
	}

	public void runAlgorithmTask(Algorithm algorithm) {
        Thread thr = new Thread (algorithm);
        thr.setPriority (Thread.NORM_PRIORITY-1);
        thr.start();
    }

	public ControlPanel getControlPanel() {
		return controlPanel;
	}
	
	public Viewer getViewer() {
		return viewer;
	}

	public String getParameter(String name) {
	    Map<String,String> parameters = new HashMap<>();
	    parameters.put("viewer width", "500");
        parameters.put("viewer height", "500");
        parameters.put("background color", "FFFFFF");

		return parameters.get(name);
	}

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CBNApplication application = new CBNApplication();
            application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            application.setVisible(true);

            application.init();
            application.start();
            application.pack();
        });
    }
}
