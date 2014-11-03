package kvaddakopter;
import kvaddakopter.image_processing.programs.CameraCalibration;

import org.opencv.core.Core;




public class ImageProcessingMain {
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		/*
		 * Select your program here. The program must be derived 
		 * from the Program class. The programs
		 * reside in the src/programs - package.
		 * 
		 * Create a new program by subclassing ProgramClass
		 * and then override the run-function and possibly the init-function. 
		 * See TestBackgroundSubtraction for an example.
		 * 
		 */
		
//		TestBackgroundSubtraction program  = new TestBackgroundSubtraction();
		CameraCalibration program  = new CameraCalibration();
		program.run();
	}
}