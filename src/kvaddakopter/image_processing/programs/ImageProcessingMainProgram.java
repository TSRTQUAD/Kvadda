package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;

import org.opencv.core.Mat;


public class ImageProcessingMainProgram extends ProgramClass{
	//private volatile Container container;
	private Mainbus mMainbus;
	
    private int mThreadId;
    
    private ColorDetection mColorDetection;
    //private TemplateMatch mTemplateMatch;
    private BackgroundSubtraction mBackgroundSubtraction;
    private Tracking mTracker;
    
	//Sleep time / FPS
	private long mSleepTime = 20;
	
	int count = 0;

    public ImageProcessingMainProgram(int threadid, Mainbus mainbus)  {
    	System.out.println("Constructor for image processing");
        mMainbus = mainbus;
        mThreadId = threadid;
    }
    
    @Override
    protected void init() {
    	//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();
		mDecoder.initialize("tcp://192.168.1.1:5555");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();
		
		//Open window 
		openVideoWindow();
		

		
		//mTracker = new Tracking();
		mColorDetection = new ColorDetection();
		//mTemplateMatch = new TemplateMatch();
		//mBackgroundSubtraction = new BackgroundSubtraction();
		
		//Initiating targets for colordetection
		initiateColorDetection();
    }
	
	public void update(){
		if (count == 0){
			init();
			count++;
		}

		//checkIsRunning();
		//Get image
		System.out.println("ImageProcessingUpdate");
		Mat currentImage = getNextFrame();
		System.out.println(1);
		ImageObject imageObject = new ImageObject(currentImage);
		
		// TODO Run appropriate programs
		ArrayList<TargetObject> targetList = new ArrayList<TargetObject>();
		
		System.out.println(1);
		if(mMainbus.isColorDetectionOn()){
			targetList.addAll(mColorDetection.start(imageObject));
		}
		if(mMainbus.isTemplateMatchingOn()){
			//targetList.addAll(mTemplateMatch.start(imageObject));
		}
		if(mMainbus.isBackgroundSubtractionOn()){
			//targetList.addAll(mBackgroundSubtraction.start(imageObject));
		}
		System.out.println(2);
		if(mColorDetection.hasIntermediateResult()){
			Mat output = mColorDetection.getIntermediateResult();
			//Convert Mat to BufferedImage
			BufferedImage out = ImageConversion.mat2Img(output);
			output.release();
			updateJavaWindow(out);
		}
		
		if(targetList.size() > 0){
			//mTracker.update(targetList);
		}
		
		mMainbus.setTargetList(targetList);
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
