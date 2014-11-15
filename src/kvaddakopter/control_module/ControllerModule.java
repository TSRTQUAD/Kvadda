package kvaddakopter.control_module;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;


public class ControllerModule{
	
	
	public ControlSignal 				 controlsignal = new ControlSignal();
	public RefinedSensorData 			 rsdata = new RefinedSensorData();
	public ReferenceData 				 rrdata = new ReferenceData();
	public double						 sampletime	= 0.02;
	public Controller					 controller = new Controller(sampletime);
	
	
	
	
	public void start() {
		/*Initialize
		* 1: Read sensor data and set origo @ intilong init lat
		* 2: 
		*/
				
		
		/*Control loop
		* 1: Update Reference ..
		* 2: Transformation into correct coordinate system
		* 3: Check mission type:
		* 		Type 1: Get control signal Mission
		* 				controller.GetControlSignalMission(rsdata, rrdata);
		* 		Type 0: Get control signal single
		* 				controller.GetControlSignalSingle(rsdata, rrdata);
		* 4: Saturate control signal?
		* 5: Update to mainbus?
		*/

	}
}


