package kvaddakopter.Mainbus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.ImageProcessingMain;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.programs.ImageProcessingMainProgram;

/**
 * Mainbus class
 * This is the main thread of the project
 * 
 * How to make new process
 * 1) Implement classes as runnables, see MyRunnable for setup
 * 2) Create thread with your runnable
 * 3) start thread
 * 
 * Ex)
 * MyRunnable myRunnable = new MyRunnable(other variables,mainbus);
 *       
 * Thread t = new Thread(myRunnable);
 * t.setPriority(1); //Sets priority (how much sheduled time the thread gets)
 * t.start(); 
 * 
 * -----------------------------------
 * If unsure about synchronization read the following:
 * http://www.javaworld.com/article/2074318/java-concurrency/java-101--understanding-java-threads--part-2--thread-synchronization.html
 * 
 */
public class Mainbus{
	//Examples
	private int var;
	public boolean condVar = false;
	
	//Programs
	MyRunnable myRunnable;
	MyRunnable2 myRunnable2;
	
	//ImageProcessingMainProgram imageProcessing;
	
	//Image processing storage
	private ArrayList<TargetObject> mTargetList;
	private BufferedImage mOutImage;
	private ArrayList<ColorTemplate> mColorTemplates;
	//Flags
	private boolean mImageProcessingRunning = false;
	private boolean mColorDetectionOn = true;
	private boolean mTemplateMatchingOn = true;
	private boolean mBackGroundSubtractionOn = true;
	
	
	public static void main(String[] args) {
		Mainbus mainbus = new Mainbus();
//		System.out.println("created mainbus");
//		ImageProcessingMainProgram imageProcessing = new ImageProcessingMainProgram(1,mainbus);
//		System.out.println("imageprocessing initiated");
		
//		Thread t3 = new Thread(imageProcessing);
//		t3.setPriority(1);
//		t3.start(); 
		
		MyRunnable myRunnable = new MyRunnable(1,mainbus);
		MyRunnable2 myRunnable2 = new MyRunnable2(2,mainbus);

		

		Thread t = new Thread(myRunnable);
		t.setPriority(1);
		t.start();
	        
		Thread t2 = new Thread(myRunnable2);
		t2.setPriority(2);
		t2.start();  
		while(true){
			//System.out.println("Mainbus running");
		}
	}
       
	
	public Mainbus(){		

		

	}
	
	
	
	public synchronized void setVar(int i){
		var = i;
	}
	
	public synchronized int getVar(){
		return var;
	}
	
	public synchronized boolean getCondVar(){
		return condVar;
	}
	
	public synchronized void setCondVar(boolean b){
		condVar = true;
	}
	
	/*
	 * Get/set functions for image processing
	 */
	public synchronized void setTargetList(ArrayList<TargetObject> targetList){
		mTargetList = targetList;
	}
	
	public synchronized ArrayList<ColorTemplate> getColorTemplates() {
		return mColorTemplates;
	}
	
	public synchronized void setColorTemplates(ArrayList<ColorTemplate> cTemplates) {
		mColorTemplates = cTemplates;
	}
	
	public synchronized boolean isImageProcessingUnitRunning() {
		return mImageProcessingRunning;
	}
	
	public synchronized void setImageProcessingUnitRunning(boolean b) {
		mImageProcessingRunning = b;
	}

	public synchronized void setColorDetectionOn(boolean b) {
		mColorDetectionOn = b;
	}
	
	public synchronized boolean isColorDetectionOn() {
		return mColorDetectionOn;
	}
	
	public synchronized boolean isTemplateMatchingOn() {
		return mTemplateMatchingOn;
	}
	
	public synchronized boolean isBackgroundSubtractionOn() {
		return mBackGroundSubtractionOn;
	}
}
