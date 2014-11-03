package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ColorDetection  extends DetectionClass{

	// Color threshold
	static final int HUE_LOW = 175 - 20;
	static final int HUE_HIGH = 175 + 20;
	static final int SATURATION_LOW = 50;
	static final int SATURATION_HIGH = 255;
	static final int VALUE_LOW = 50;
	static final int VALUE_HIGH = 255;


	//Morphology 
	static final int MORPH_KERNEL_SIZE = 16;
	static final int MORPH_KERNEL_TYPE = Imgproc.MORPH_ELLIPSE;

	// Color templates
	ArrayList<ColorTemplate> colorTemplates;


	public ColorDetection(){
		super();
		colorTemplates = new ArrayList<ColorTemplate>();
		colorTemplates.add(new ColorTemplate("Blue ball", 90, 140, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
		//colorTemplates.add(new ColorTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
	}

	@Override
	public ArrayList<TargetObject> start(ImageObject imageObject) {

		// Convert RGB to HSV
		Mat HSVImage = new Mat();
		Imgproc.cvtColor(imageObject.getImage(), HSVImage, Imgproc.COLOR_BGR2HSV);

		Mat resultImage = new Mat(HSVImage.rows(), HSVImage.cols(), CvType.CV_8U);
		resultImage.setTo(new Scalar(0));


		Mat thresholdImage = new Mat();
		Size morphSize = new Size(MORPH_KERNEL_SIZE, MORPH_KERNEL_SIZE);
		Mat dilatedImage = new Mat();
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();


		for(ColorTemplate colorTemplate : colorTemplates){
			// Threshold with inRange
			Core.inRange(HSVImage, colorTemplate.getLower(), colorTemplate.getUpper(), thresholdImage);


			// Do morphological operations		
			Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, morphSize);
			Imgproc.morphologyEx(thresholdImage, dilatedImage, Imgproc.MORPH_OPEN, kernel);

			// Convert blobs to target objects
			//TargetObject target = new TargetObject();


			// Add results to binary result image
			Core.bitwise_or(resultImage, dilatedImage, resultImage);
		}

		// Create an intermediate result image
		mIntermeditateResult = resultImage;

		return targetObjects;

	}

	// Adds a color template and returns handler id (ArrayList id)
	public int addTemplate(String description_, int hueLow_, int hueHigh_, int saturationLow_, int saturationHigh_, int valueLow_, int valueHigh_, int form_type_){
		colorTemplates.add(new ColorTemplate(description_, hueLow_, hueHigh_, saturationLow_, saturationHigh_, valueLow_, valueHigh_, form_type_));
		return colorTemplates.size() - 1;
	}

	public void activateTemplate(int id){
		synchronized (colorTemplates) {
			if(id >= colorTemplates.size() || id < 0) return;
			colorTemplates.get(id).activate();
		}
	}

	public void deactivateTemplate(int id){
		if(id >= colorTemplates.size() || id < 0) return;
		colorTemplates.get(id).deactivate();
	}
}
