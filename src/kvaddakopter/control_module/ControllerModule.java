package kvaddakopter.control_module;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;


public class ControllerModule{
	
	

	public double						 sampletime	= 0.02;

	
	
	
	
	public void start() {


	}
}


*		CONTROLLER
*		i: Update Reference ..
*		ii: Transformation into correct coordinate system
*		iii: Check mission type:
* 			Type 1: Get control signal Mission
* 					controller.GetControlSignalMission(rsdata, rrdata);
* 			Type 0: Get control signal single
* 					controller.GetControlSignalSingle(rsdata, rrdata);
*		vi: Saturate control signal?
*		v: Update to mainbus?