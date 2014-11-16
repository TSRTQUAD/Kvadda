package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

public class TemplateMatch  extends DetectionClass{



	@Override
	public boolean isMethodActive(Mainbus mainbus) {
		return mainbus.isTemplateMatchingOn();
	}
	@Override
	public ArrayList<TargetObject> runMethod(ImageObject imageObject) {


		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
//		TargetObject target = new TargetObject();


		return targetObjects;
	}
}
