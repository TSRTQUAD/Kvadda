package kvaddakopter.image_processing.test_programs;

import java.awt.image.BufferedImage;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.comm_tests.IPMockMainBus;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.programs.ProgramClass;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.image_processing.utils.KeyBoardHandler;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class TestBackgroundSubtraction extends ProgramClass{



	enum PoseState{
		SelectFirstPose,
		ConfirmFirstPose,
		SelectSecondPose,
		ConfirmSecondPose,
		ViewResult
	}

	PoseState state;

	Mat mFirstPose = null;
	Mat mSecondPose = null;

	int FRAME_COUNTER = 0;
	
	boolean fileWritten = false;
	
	public TestBackgroundSubtraction(int threadid, IPMockMainBus mainbus) {
		super(threadid, mainbus);
	}

	
	public void init() {

		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();
		mDecoder.initialize("tcp://192.168.1.1:5555");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();

		//Selecting method/algorithm
		mCurrentMethod = new BackgroundSubtraction();

		//Listen to keyboard input
		mKeyBoardHandler = new KeyBoardHandler();
		mKeyBoardHandler.setListner(this);
		mKeyBoardHandler.start();
		System.out.println("Select first pose by press Enter");

		state = PoseState.SelectFirstPose;
	}

	@Override
	protected void update() {
		Mat currentImage = getNextFrame();
		boolean photoShoot = false;
		if(!photoShoot){
			ImageObject imageObject = new ImageObject(currentImage);
			mCurrentMethod.runMethod(imageObject);


			if(mCurrentMethod.hasIntermediateResult()){
				updateWindow(mCurrentMethod.getIntermediateResult());
			}
		}else{
			if(state == PoseState.SelectFirstPose|| state == PoseState.SelectSecondPose ){
				updateWindow(currentImage);
				
				return;
			}

			if(state == PoseState.ConfirmFirstPose){
				updateWindow(mFirstPose.clone());
				if(!fileWritten){
					Highgui.imwrite("kaffe.jpg",mFirstPose);
					fileWritten = true;
				}
				return;
			}

			if(state == PoseState.ConfirmSecondPose){
				updateWindow(mSecondPose.clone());
				return;
			}
	
			if(FRAME_COUNTER % 2 == 0){
				FRAME_COUNTER++;
				ImageObject imageObject = new ImageObject(mFirstPose);
				mCurrentMethod.runMethod(imageObject);
			}else{
				FRAME_COUNTER++;
				ImageObject imageObject = new ImageObject(mSecondPose);
				mCurrentMethod.runMethod(imageObject);

				if(mCurrentMethod.hasIntermediateResult()){
					updateWindow(mCurrentMethod.getIntermediateResult());
				}
				
				while(state == PoseState.ViewResult){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
	}

	private void updateWindow(Mat img){
		Mat output  = img;
		//Convert Mat to BufferedImage
		BufferedImage out = ImageConversion.mat2Img(output);
		output.release();
		updateJavaWindow(out);
	}



	@Override
	public void onKeyBoardInput(String inputString) {
		switch (state) {
		case SelectFirstPose:			
			// Reading image from decoder
			mFirstPose = getNextFrame();
			while(mFirstPose == null){
				mFirstPose = getNextFrame();
			};

			//Selecting the first image 
			state =PoseState.ConfirmFirstPose;
			System.out.println("Viewing first pose");

			break;
		case ConfirmFirstPose:
			state =PoseState.SelectSecondPose;	
			System.out.println("Select second pose");
			break;
		case SelectSecondPose:	
			// Reading image from decoder
			mSecondPose = getNextFrame();
			while(mSecondPose == null){
				mSecondPose = getNextFrame();
			};

			state =PoseState.ConfirmSecondPose;
			System.out.println("Viewing second pose");


			break;
		case ConfirmSecondPose:
			state =PoseState.ViewResult;	
			System.out.println("Viewing result. \n Press Enter to restart");
			break;
		case ViewResult:
			state =PoseState.SelectFirstPose;
			System.out.println("Select first pose");
			break;

		default:
			break;
		}
	}

}
