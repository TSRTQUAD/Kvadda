package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Mat;

public class DetectionClass {
	Mat mIntermeditateResult;
	
	public ArrayList<TargetObject> start(ImageObject imageObject){
		return null;
	};
	
	public Mat getIntermediateResult(){
		return mIntermeditateResult;
	}
	
	public boolean hasIntermediateResult(){
		return mIntermeditateResult != null && !mIntermeditateResult.empty();
	}
	

}
