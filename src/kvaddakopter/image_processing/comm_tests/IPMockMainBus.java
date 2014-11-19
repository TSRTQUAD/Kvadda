package kvaddakopter.image_processing.comm_tests;

import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.data_types.Template;
import kvaddakopter.image_processing.programs.TestBlurDetection;
import kvaddakopter.image_processing.programs.TestColorCalibration;
import kvaddakopter.interfaces.MainBusIPInterface;
import kvaddakopter.maps.GPSCoordinate;

public class IPMockMainBus implements MainBusIPInterface{
	
	private ArrayList<TargetObject> mTargetList;
	private ArrayList<ColorTemplate> mColorTemplates;
	private ColorTemplate mIPCalibTemplate;
	private ImageObject mImageObject;
	private boolean mIsIPRunning = true;
	//0:Colordetection, 1:Background subtraction 2:Template match, 3:Blur detection,
	//4:Color calibration, 5: Tracking
	private int[] mIPActiveModes = {0, 0, 0, 0,1,0};
	private int mIPImageMode = 0;
	
	public static void main(String[] args) {
		IPMockMainBus mainbus = new IPMockMainBus();
		mainbus.initIPVariables();
		
	    TestSliders testSliders = new TestSliders(mainbus);
	    new Thread(testSliders).start();
	    
	    TestBlurDetection imageProcessing = new TestBlurDetection(1,mainbus);
	    new Thread(imageProcessing).start();
	    
	    while(true){
	    	
	    }
	}

	
	@Override
	public synchronized int[] getIPActiveModes() {
		return mIPActiveModes;
	}

	@Override
	public synchronized void setIPActiveModes(int[] modes) {
		mIPActiveModes = modes;
	}

	@Override
	public synchronized void removeIPMode(int i) {
		mIPActiveModes[i] = 0;
	}

	@Override
	public synchronized boolean getIsIPRunning() {
		return mIsIPRunning;
	}

	@Override
	public synchronized void setIsIPRunning(boolean b) {
		mIsIPRunning = b;
	}

	@Override
	public synchronized int getIPImageMode() {
		return mIPImageMode;
	}

	@Override
	public synchronized void setIPImageMode(int imageMode) {
		mIPImageMode = imageMode;
	}

	@Override
	public synchronized ArrayList<ColorTemplate> getIPColorTemplates() {
		return mColorTemplates;
	}

	@Override
	public synchronized void setIPColorTemplates(ArrayList<ColorTemplate> colorTemplates) {
		mColorTemplates = colorTemplates;
		
	}

	@Override
	public synchronized ArrayList<Template> getIPFormTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void setIPFormTemplates(ArrayList<Template> templates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void setIPGPSCoordinate(GPSCoordinate coord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized GPSCoordinate getGPSCoordinate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void setIPTargetList(ArrayList<TargetObject> listOfTargets) {
		mTargetList = listOfTargets;
	}

	@Override
	public synchronized void setIPImageObject(ImageObject imageObject) {
		mImageObject = imageObject;
		
	}

	@Override
	public synchronized void setIPCalibTemplate(ColorTemplate cTemplate) {
		mIPCalibTemplate = cTemplate;	
	}

	@Override
	public synchronized void initIPVariables() {
		// TODO More initializations needed (probably)
		mTargetList = new ArrayList<TargetObject>();
		mColorTemplates = new ArrayList<ColorTemplate>();
		mIPCalibTemplate = new ColorTemplate();
		//imageObject = new ImageObject(); 
		//TODO this constructor does not exist, 
		// should we return buffered image instead?
	}

	@Override
	public synchronized void addIPColorTemplate(ColorTemplate template) {
		mColorTemplates.add(template);
		
	}

	@Override
	public synchronized void addIPFormTemplates(Template template) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized ArrayList<TargetObject> getIPTargetList() {
		return mTargetList;
	}

	@Override
	public synchronized ColorTemplate getIPCalibTemplate() {
		return mIPCalibTemplate;
	}
	
	
	
}
