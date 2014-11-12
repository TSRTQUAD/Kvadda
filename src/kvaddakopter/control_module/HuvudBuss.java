package kvaddakopter.control_module;


public class HuvudBuss{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Controller bus running ...");			
		//ControllerModule cmodule = new ControllerModule();		
		//module.start();
		
		Sensorfusionmodule smodule = new Sensorfusionmodule();		
		smodule.start();
		
	
		
		
		
	}
}
