package kvaddakopter;
import java.util.Scanner;

import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.programs.CameraCalibration;
import kvaddakopter.image_processing.programs.TestBackgroundSubtraction;
import kvaddakopter.image_processing.programs.TestBlurDetection;
import kvaddakopter.image_processing.programs.TestColorDetection;
import kvaddakopter.image_processing.programs.TestTemplateMatching;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Core;

public class ImageProcessingMain{
	
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
//		TestBackgroundSubtraction program  = new TestBackgroundSubtraction(0, null);
		TestTemplateMatching program  = new TestTemplateMatching(0, null);
//		CameraCalibration program  = new CameraCalibration();
		//TestColorDetection program  = new TestColorDetection(0, null);
//		TestBlurDetection program  = new TestBlurDetection(0, null);
		//colorTemplates.add(new ColorTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
		
		
		//ColorDetection method = (ColorDetection)program.getCurrentMethod();
		//method.addTemplate("Pink square", 160, 255, 70, 150, 150, 255, ColorTemplate.FORM_SQUARE);
		//method.addTemplate("Yellow square", 0, 100, 80, 150, 130, 255, ColorTemplate.FORM_SQUARE);
		

        Thread t = new Thread(program);
        t.setPriority(1);
        t.start();
		
//		System.out.println("Image processing unit up and running!");
	}
}