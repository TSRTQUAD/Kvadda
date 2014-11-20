package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.algorithms.BlurDetection;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.data_types.Template;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;


public class ImageProcessingMainProgram extends ProgramClass{

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

	public ImageProcessingMainProgram(int threadid, MainBusIPInterface mainbus)  {
		super(threadid,mainbus);
	}

	@Override
	public void init() {
		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();

		//mDecoder.initialize("tcp://192.168.1.1:5555"/*FFMpegDecoder.STREAM_ADDR_BIPBOP*/);
		mDecoder.initialize("mvi2.mp4");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();

		//Create and add background subtraction method and add it to the list of active methods
		//mBackgroundSubtraction = new BackgroundSubtraction();

		//Color detection
		mColorDetection = new ColorDetection();

		//Color Template matching
		//mTemplateMatch = new TemplateMatch();

		// Blur detection 
		mBlurDetection = new BlurDetection();

		//Create Trackers
		//mTracker = new Tracking();
		
	}

	public void update(){
		BufferedImage out = null;
		BufferedImage colorDetectionImage = null;
		BufferedImage templateMatchingImage = null;
		Mat image = getNextFrame();
		ImageObject imageObject = new ImageObject(image);
		
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		
		//GPSCoordinate gpsCoordinate = mMainbus.getGPSCoordinate(); 
		
		int[] modes = mMainbus.getIPActiveModes(); 
	    
		if(modes[MainBusIPInterface.COLOR_CALIBRATION_MODE] == 1){
			// Show Color calibration image
			ColorTemplate cTemplate = mMainbus.getIPCalibTemplate();
			imageObject.thresholdImage(cTemplate);
			out = ImageConversion.mat2Img(imageObject.getImage());
			mMainbus.setIPImageToShow(out);
			updateJavaWindow(mMainbus.getIPImageToShow());//TODO should be in the gui
			
		}else{
			if(modes[MainBusIPInterface.BLUR_DETECTION_MODE] == 1){
				// DO SOMETHING
				mBlurDetection.runMethod(imageObject);
				//TODO some way to present the blur data
			}

			if(modes[MainBusIPInterface.COLOR_DETECTION_MODE] == 1){
				// DO SOMETHING
				ArrayList<ColorTemplate> colorTemplates = mMainbus.getIPColorTemplates();	
				mColorDetection.setTemplates(colorTemplates);
				targetObjects.addAll(mColorDetection.runMethod(imageObject));
				colorDetectionImage = ImageConversion.mat2Img(mColorDetection.getIntermediateResult());
				
			}
			if(modes[MainBusIPInterface.BACKGROUND_SUBTRACION_MODE] == 1){
				// DO SOMETHING
				targetObjects.addAll(mBackgroundSubtraction.runMethod(imageObject));
			}
			if(modes[MainBusIPInterface.TEMPLATE_MATCHING_MODE] == 1){
				// DO SOMETHING
				ArrayList<Template> formTemplates = mMainbus.getIPFormTemplates();
				targetObjects.addAll(mTemplateMatch.runMethod(imageObject));
				templateMatchingImage= ImageConversion.mat2Img(mColorDetection.getIntermediateResult());
			}
			if(modes[MainBusIPInterface.TRACKING_MODE] == 1){
				// DO SOMETHING
				if(targetObjects.size() > 0){
					mTracker.update(targetObjects);
				}
			}
			
			mMainbus.setIPTargetList(targetObjects);
			
			//What image to show
			switch(mMainbus.getIPImageMode()){
				case MainBusIPInterface.DEFAULT_IMAGE:
					out = ImageConversion.mat2Img(imageObject.getImage());
					break;
				case MainBusIPInterface.CUT_OUT_IMAGE:
					out = colorDetectionImage;
					System.out.println("set cut out image");
					break;
				case MainBusIPInterface.TARGET_IMAGE:
					//imageFromMethod = mTracker.getImage();
					//out = ImageConversion.mat2Img(imageFromMethod);
					break;
				case MainBusIPInterface.SUPRISE_IMAGE :
					out = ImageConversion.loadImageFromFile("suprise_image.jpg");
					break;
				case MainBusIPInterface.TEMPLATE_MATCHING_IMAGE :
					out = templateMatchingImage;
					break;
			}
			updateJavaWindow(colorDetectionImage);
		}

		//For debugging
		//Select Method that u want 2 dbug
//		DetectionClass debuggedMethod = getDetectionMethod(BackgroundSubtraction.class);
//		if(debuggedMethod == null || !debuggedMethod.isMethodActive(mMainbus)){
//			if(!userHasBeenWarned){
//				userHasBeenWarned= true;
//				System.err.println("ImageProcessing  WARING: Selected Detection method for debugging has been not created or has not activated");
//			}
//		}else{	
//			if(debuggedMethod.hasIntermediateResult()){
//				Mat output = debuggedMethod.getIntermediateResult();
//				//Convert Mat to BufferedImage
//				BufferedImage out = ImageConversion.mat2Img(output);
//				output.release();
//				updateJavaWindow(out);
//			}
//		}
	}	


//	private DetectionClass getDetectionMethod(Object object ){
//		for(DetectionClass detectionMethod : mDetectionMethodList){
//			if(detectionMethod.getClass().equals(object))
//				return detectionMethod;
//		}
//		return null;
//	}
}

