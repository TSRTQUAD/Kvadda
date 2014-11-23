package kvaddakopter.gui;

import kvaddakopter.Mainbus.AssignmentPlanerRunnable;
import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.gui.interfaces.MockMainBus;

public class GUITESTMain{

	public static void main(String[] args) {
		MockMainBus mainbus = new MockMainBus();
		
		
		MatlabProxyConnection matlabproxy = new MatlabProxyConnection();
		mainbus.setMatlabProxyConnection(matlabproxy);
		matlabproxy.startMatlab("quiet");
	
		
		// START MODULE
	    GUIModule gui = new GUIModule(mainbus);
	    new Thread(gui).start();
	    
	    AssignmentPlanerRunnable assignmentplaner = new AssignmentPlanerRunnable(2, mainbus);
	    new Thread(assignmentplaner).start();
	}

}
