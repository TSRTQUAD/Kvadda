package kvaddakopter.gui;

import kvaddakopter.gui.interfaces.MockMainBus;

public class GUITESTMain{

	public static void main(String[] args) {
		// START MODULE
	    GUIModule gui = new GUIModule(new MockMainBus());
	    new Thread(gui).start();

	}

}
