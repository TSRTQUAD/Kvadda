package kvaddakopter;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.programs.CameraCalibration;
import kvaddakopter.image_processing.programs.TestColorDetection;

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
//		TestColorDetection program  = new TestColorDetection();
		//colorTemplates.add(new ColorTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
		
		
//		ColorDetection method = (ColorDetection)program.getCurrentMethod();
//		method.addTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE);
		program.start();
		
//		System.out.println("Image processing unit up and running!");
	}
}