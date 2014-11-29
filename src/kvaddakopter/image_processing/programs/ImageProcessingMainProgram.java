package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.communication.QuadData;
import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.algorithms.BlurDetection;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;

/**
 * Main class of the Image Processing
 * Initiates detection methods, tracking, decoder
 * 
 * Checks colorTemplates, FormTamplates on mainbus
 * Checks detection modes, image modes on mainbus
 * 
 * runs main loop: depending on image processing settings on mainbus
 *	
 */
public class ImageProcessingMainProgram extends ProgramClass{

	private ColorDetection mColorDetection;
	private TemplateMatch mTemplateMatch;
	private BackgroundSubtraction mBackgroundSubtraction;
	private BlurDetection mBlurDetection;
	private Tracking mTracker;

	int count = 0;
	
	private boolean mStandAloneTest = false;
	
	public ImageProcessingMainProgram(int threadid, MainBusIPInterface mainbus)  {
		super(threadid,mainbus);
	}

	@Override
	public void init() {
		
		//Sleep time / FPS
		mSleepTime = 20;
		
		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();

		mDecoder.initialize("tcp://192.168.1.1:5555"/*FFMpegDecoder.STREAM_ADDR_BIPBOP*/);
		//mDecoder.initialize(FFMpegDecoder.STREAM_ADDR_BIPBOP);
		//mDecoder.initialize("mvi2.mp4");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window
		if(mStandAloneTest)
			openVideoWindow();

		//Create and add background subtraction method and add it to the list of active methods
		//mBackgroundSubtraction = new BackgroundSubtraction();

		//Color detection
		mColorDetection = new ColorDetection();
		ArrayList<ColorTemplate> colorTemplates = mMainbus.getIPColorTemplates();	
		mColorDetection.setTemplates(colorTemplates);

		//Color Template matching
		mTemplateMatch = new TemplateMatch();
		ArrayList<FormTemplate> formTemplates = mMainbus.getIPFormTemplates();
		mTemplateMatch.setTemplates(formTemplates);

		// Blur detection 
		mBlurDetection = new BlurDetection();

		//Create Trackers
		mTracker = new Tracking();
	}

	public void update(){		
		//Images to show
		checkIsRunning();
		QuadData currentQuadData = new QuadData(mMainbus.getQuadData());
		BufferedImage out = null;
		Mat colorDetectionImage = null;
		Mat colorCalibrationImage = null;
		Mat templateMatchingImage = null;
		Mat trackingImage = null;

		Mat image = getNextFrame();
		ImageObject imageObject = new ImageObject(image);

		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();

		int[] modes = mMainbus.getIPActiveModes(); 

		if(modes[MainBusIPInterface.MODE_COLOR_CALIBRATION] == 1){
			// Show Color calibration image
			ColorTemplate cTemplate = mMainbus.getIPCalibTemplate();
			imageObject.thresholdImage(cTemplate);
			colorCalibrationImage = imageObject.getImage();			
		}else{
			if(modes[MainBusIPInterface.MODE_BLUR_DETECTION] == 1){
				mBlurDetection.runMethod(imageObject);
				if(imageObject.getBlurLevels().h > BlurDetection.MAX_H_BLUR || imageObject.getBlurLevels().v > BlurDetection.MAX_V_BLUR)
					return;
			}

			if(modes[MainBusIPInterface.MODE_COLOR_DETECTION] == 1){
				targetObjects.addAll(mColorDetection.runMethod(imageObject));
				colorDetectionImage = mColorDetection.getIntermediateResult();
			}
			if(modes[MainBusIPInterface.MODE_BACKGROUND_SUBTRACION] == 1){
				targetObjects.addAll(mBackgroundSubtraction.runMethod(imageObject));
			}
			if(modes[MainBusIPInterface.MODE_TEMPLATE_MATCHING] == 1){

				targetObjects.addAll(mTemplateMatch.runMethod(imageObject));
				templateMatchingImage= mTemplateMatch.getIntermediateResult();
			}
			if(modes[MainBusIPInterface.MODE_TRACKING] == 1){
				if(targetObjects.size() > 0){
					mTracker.update(targetObjects,currentQuadData);
					Mat currentImage = imageObject.getImage();
					trackingImage = mTracker.getImage(
							currentImage.width(),
							currentImage.height(),
							currentImage);
				}
			}
			if(modes[MainBusIPInterface.MODE_TEMPLATE_CALIBRATION] == 1){
				FormTemplate formTemplate = mMainbus.getCalibFormTemplate();
				if(formTemplate != null){
					mTemplateMatch.calibrateTemplate(formTemplate);
					templateMatchingImage = mTemplateMatch.getIntermediateResult();
				}
			}

			mMainbus.setIPTargetList(mTracker.getTargets());


		}
		//What image to show
		switch(mMainbus.getIPImageMode()){
		case MainBusIPInterface.IMAGE_DEFAULT:
			out = ImageConversion.mat2Img(imageObject.getImage());
			break;
		case MainBusIPInterface.IMAGE_CUT_OUT:
			if(colorDetectionImage!=null)
				out = ImageConversion.mat2Img(colorDetectionImage);
			break;
		case MainBusIPInterface.IMAGE_TARGET:
			if(trackingImage!= null)
				out = ImageConversion.mat2Img(trackingImage);
			break;
		case MainBusIPInterface.IMAGE_SURPRISE:
			out = ImageConversion.loadImageFromFile("suprise_image.jpg");
			break;
		case MainBusIPInterface.IMAGE_TEMPLATE_MATCHING :
			if(templateMatchingImage != null)
				out = ImageConversion.mat2Img(templateMatchingImage);
			break;
		case MainBusIPInterface.IMAGE_TEMPLATE_CALIBRATE :
			if(templateMatchingImage != null)
				out = ImageConversion.mat2Img(templateMatchingImage);
			break;

		case MainBusIPInterface.IMAGE_COLOR_CALIBRRATE:
			if(colorCalibrationImage != null)
				out = ImageConversion.mat2Img(colorCalibrationImage);
			break;
		}
		
		if(out != null){
			mMainbus.setIPImageToShow(out);
			if(mStandAloneTest)
				updateJavaWindow(out);
		}
	}	

}

