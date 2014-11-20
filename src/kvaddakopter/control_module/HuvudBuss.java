package kvaddakopter.control_module;


public class HuvudBuss{

	public static void main(String[] args) {
		// START MODULE
	    Sensorfusionmodule module = new Sensorfusionmodule(new Mockmainbus());
	    new Thread(module).start();
	    
	    

	}

}
