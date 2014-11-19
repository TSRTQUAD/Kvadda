package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.FormSubmitEvent.MethodType;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.algorithms.BlurDetection;
import kvaddakopter.image_processing.algorithms.DetectionClass;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.comm_tests.IPMockMainBus;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.maps.GPSCoordinate;

import org.opencv.core.Mat;


public class ImageProcessingMainProgram extends ProgramClass{


	public static int COLOR_DETECTION 		= 0;
	public static int TEMPLATE_MATCHING 	= 1;
	public static int BACKGROUND_SUBTRACION = 2;
	public static int BLUR_DETECTION 		= 3;
	public static int COLOR_CALIBRATION 	= 4;
	public static int TRACKING			 	= 5;

	public static enum ImageType{
		DEFAULT,
		COLOR_CALIBRATION_IMAGE,
		TARGET_IMAGE,
		SUPRISE_IMAGE,
		CUT_OUT_IMAGE
	};


	private ColorDetection mColorDetection;
	private TemplateMatch mTemplateMatch;
	private BackgroundSubtraction mBackgroundSubtraction;
	private BlurDetection mBlurDetection;
	private Tracking mTracker; 

	//Debug Warning
	boolean userHasBeenWarned = false;

	//Sleep time / FPS
	private long mSleepTime = 20;

	int count = 0;

	public ImageProcessingMainProgram(int threadid, IPMockMainBus mainbus)  {
		super(threadid,mainbus);
	}

	@Override
	public void init() {
		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();

		mDecoder.initialize("tcp://192.168.1.1:5555"/*FFMpegDecoder.STREAM_ADDR_BIPBOP*/);

		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();

		//Create and add background subtraction method and add it to the list of active methods
		mBackgroundSubtraction = new BackgroundSubtraction();

		//Color detection
		mColorDetection = new ColorDetection();

		initiateColorDetection();

		//Color Template matching
		mTemplateMatch = new TemplateMatch();

		// Blur detection 
		mBlurDetection = new BlurDetection();

		//Create Trackers
		mTracker = new Tracking();

	}

	public void update(){
		checkIsRunning();
		
		Mat image = getNextFrame();
		ImageObject imageObject = new ImageObject(image);
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		
		//GPSCoordinate gpsCoordinate = mMainbus.getGPSCoordinate(); 
		
		
		int[] modes = new int[1]; // mMainBus.getModes() 
	    
		if(modes[COLOR_CALIBRATION] == 1){
			// DO SOMETHING
			
		}else{
			if(modes[BLUR_DETECTION] == 1){
				// DO SOMETHING
				targetObjects.addAll(mColorDetection.runMethod(imageObject));
			}

			if(modes[COLOR_DETECTION] == 1){
				// DO SOMETHING
				ArrayList<ColorTemplate> colorTemplates = mMainbus.getIPColorTemplate();		
				targetObjects.addAll(mColorDetection.runMethod(imageObject));
			}
			if(modes[BACKGROUND_SUBTRACION] == 1){
				// DO SOMETHING
				targetObjects.addAll(mBackgroundSubtraction.runMethod(imageObject));
			}
			if(modes[TEMPLATE_MATCHING] == 1){
				// DO SOMETHING
				ArrayList<FormTemplat> formTemplates = mMainbus.getFormTemplate();
				targetObjects.addAll(mTemplateMatch.runMethod(imageObject));
			}
			if(modes[TRACKING] == 1){
				// DO SOMETHING
				mTracker.update(targetObjects);
			}
			
//			mMainbus.setIPImageObject(imageObject);
//			mMainbus.setIPTargetList(targetList);
		}

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

	private void busActions(){





		/*
		 * 
		 * IN:
		 * - Läs vilka metoder som ska aktivera
		 * - Läs tempalate ( färg och form)
		 * - GPS DATA/HEADING(?)
		 * -
		 * UT:
		 * - TRACK DATA
		 * - OUTPUT IMG
		 * - STYRSIGNAL
		 * 
		 * 
		 */

	}

}

