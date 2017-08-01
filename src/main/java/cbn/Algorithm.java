package cbn;

// Algorithm

abstract public class Algorithm implements Runnable {
	protected CBNApplication applet;
	protected boolean running;

	public Algorithm (CBNApplication applet) {
		this.applet = applet;
	}
	
	abstract public void addParametersToControlPanel();
	
	public void requestStop() {
		running = false;
	}
}
