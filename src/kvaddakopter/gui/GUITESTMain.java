package kvaddakopter.gui;

import matlabcontrol.MatlabConnectionException;
import kvaddakopter.Mainbus.AssignmentPlanerRunnable;
import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.gui.interfaces.MockMainBus;

public class GUITESTMain{

	public static void main(String[] args) {
		MockMainBus mainbus = new MockMainBus();
		
		MatlabProxyConnection proxy = new MatlabProxyConnection();
		try {
			proxy.startMatlab("quiet");
		} catch (MatlabConnectionException e) {
			System.out.println("Could not start Matlab");
			e.printStackTrace();
		}
		
		// START MODULE
	    GUIModule gui = new GUIModule(mainbus);
	    new Thread(gui).start();
	    
	    AssignmentPlanerRunnable assignmentplaner = new AssignmentPlanerRunnable(2, mainbus);
	    new Thread(assignmentplaner).start();
	}

}
