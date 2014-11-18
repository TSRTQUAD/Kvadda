package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.algorithms.DetectionClass;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;

import org.opencv.core.Mat;


public class ImageProcessingMainProgram extends ProgramClass{

	private ColorDetection mColorDetection;
	private TemplateMatch mTemplateMatch;
	private BackgroundSubtraction mBackgroundSubtraction;
	private Tracking mTracker;

	//Debug Warning
	boolean userHasBeenWarned = false;

	//Sleep time / FPS
	private long mSleepTime = 20;

	int count = 0;

	public ImageProcessingMainProgram(int threadid, Mainbus mainbus)  {
		super(threadid,mainbus);
	}

	@Override
	public void init() {
		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();

		mDecoder.initialize(/*"tcp://192.168.1.1:5555"*/FFMpegDecoder.STREAM_ADDR_BIPBOP);

		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();


		mDetectionMethodList = new ArrayList<DetectionClass>();

		//Create and add background subtraction method and add it to the list of active methods
		mBackgroundSubtraction = new BackgroundSubtraction();
		mDetectionMethodList.add(mBackgroundSubtraction);

		//Color detection
		mColorDetection = new ColorDetection();
		initiateColorDetection();
		mDetectionMethodList.add(mColorDetection);

		//Color Template matching
		mTemplateMatch = new TemplateMatch();
		mDetectionMethodList.add(mTemplateMatch);

		//Create Trackers
		mTracker = new Tracking();

	}

	public void update(){

		// Detta behövs inte  då init() körs vid skapandet av en ProgramClass.
		// Om vi vill kunna köra init() explicit får vi ändra om lite. 
		/*if (count == 0){
			init();
			count++;
		}*/
		//checkIsRunning();
		
		
		//Get image
		Mat currentImage = getNextFrame();
		ImageObject imageObject = new ImageObject(currentImage);

		ArrayList<TargetObject> targetList = new ArrayList<TargetObject>();
		for(DetectionClass detectionMethod : mDetectionMethodList){
			if(detectionMethod.isMethodActive(mMainbus)){
				targetList.addAll(detectionMethod.runMethod(imageObject));
			}
		}
		/*
		System.out.println(1);
//		if(mMainbus.isColorDetectionOn()){
//			targetList.addAll(mColorDetection.start(imageObject));
//		}
		if(mMainbus.isTemplateMatchingOn()){
			//targetList.addAll(mTemplateMatch.start(imageObject));
		}
		if(mMainbus.isBackgroundSubtractionOn()){
			//targetList.addAll(mBackgroundSubtraction.start(imageObject));
		}*/



		//For debugging
		//Select Method that u want 2 dbug
		DetectionClass debuggedMethod = getDetectionMethod(BackgroundSubtraction.class);
		if(debuggedMethod == null || !debuggedMethod.isMethodActive(mMainbus)){
			if(!userHasBeenWarned){
				userHasBeenWarned= true;
				System.err.println("ImageProcessing  WARING: Selected Detection method for debugging has been not created or has not activated");
			}
		}else{	
			if(debuggedMethod.hasIntermediateResult()){
				Mat output = debuggedMethod.getIntermediateResult();
				//Convert Mat to BufferedImage
				BufferedImage out = ImageConversion.mat2Img(output);
				output.release();
				updateJavaWindow(out);
			}
		}

		if(targetList.size() > 0){
			//mTracker.update(targetList);
		}

		mMainbus.setTargetList(targetList);
	}	


	private DetectionClass getDetectionMethod(Object object ){
		for(DetectionClass detectionMethod : mDetectionMethodList){
			if(detectionMethod.getClass().equals(object))
				return detectionMethod;
		}
		return null;
	}


	private void checkIsRunning(){
		while(!mMainbus.isImageProcessingUnitRunning()){
			synchronized(mMainbus){
				try {
					mMainbus.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void initiateColorDetection(){
		mColorDetection.addTemplate("Pink square", 160, 255, 70, 150, 150, 255, ColorTemplate.FORM_SQUARE);
		mColorDetection.addTemplate("Yellow square", 0, 100, 80, 150, 130, 255, ColorTemplate.FORM_SQUARE);
		//		for(ColorTemplate template:mMainbus.getColorTemplates()){
		//			mColorDetection.addTemplate(template);
		//		}
	}

}
