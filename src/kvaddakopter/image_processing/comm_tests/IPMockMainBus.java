package kvaddakopter.image_processing.comm_tests;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.opencv.core.Core;

import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.data_types.Template;
import kvaddakopter.image_processing.programs.ImageProcessingMainProgram;
import kvaddakopter.image_processing.programs.TestBlurDetection;
import kvaddakopter.image_processing.programs.TestColorCalibration;
import kvaddakopter.interfaces.MainBusIPInterface;
import kvaddakopter.maps.GPSCoordinate;

public class IPMockMainBus implements MainBusIPInterface{
	

	
	private ColorTemplate mIPCalibTemplate;
	private FormTemplate mIPCalibFormTemplate;
	//private ImageObject mImageObject;
	private BufferedImage mIPImageToShow;
	private boolean mIsIPRunning;
	int mIPImageMode = 0;
	public static void main(String[] args) {
		//Has to be run to be working
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		IPMockMainBus mainbus = new IPMockMainBus();
		mainbus.initIPVariables();
		
	    IPTestGUI testSliders = new IPTestGUI(mainbus);
	    new Thread(testSliders).start();
	    
	    ImageProcessingMainProgram imageProcessing = new ImageProcessingMainProgram(1,mainbus);
	    Thread t1 = new Thread(imageProcessing);
	    //t1.setDaemon(true);
	    t1.start();
	    
	    while(true){
	    	
	    }
	}
	
	@Override
	public synchronized void initIPVariables() {
		// TODO More initializations needed (probably)
	
		//activateIPMode(COLOR_DETECTION_MODE);
		activateIPMode(TEMPLATE_CALIBRATION_MODE);
		setIPImageMode(TEMPLATE_CALIBRATE_IMAGE);
		//activateIPMode(TRACKING_MODE);
		//setIPImageMode(DEFAULT_IMAGE);
		mColorTemplates.add(new ColorTemplate("Pink square", 120, 200, 50, 90, 180, 245, ColorTemplate.FORM_SQUARE));	
		mColorTemplates.add(new ColorTemplate("Yellow square", 30, 120, 50, 120, 130, 255, ColorTemplate.FORM_SQUARE));
		
		mIPCalibTemplate = new ColorTemplate();
		mIPImageToShow = null;
		mIsIPRunning = false;
	}

	
	@Override
	public synchronized int[] getIPActiveModes() {
		return mIPActiveModes;
	}

@Override
	public synchronized void setIPActiveModes(int[] modes) {
		//mIPActiveModes = modes;
	}

	@Override
	public synchronized void deactivateIPMode(int i) {
		mIPActiveModes[i] = 0;
	}
	
	@Override
	public synchronized void activateIPMode(int i) {
		mIPActiveModes[i] = 1;
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
		//mColorTemplates = colorTemplates;
		
	}

	@Override
	public synchronized ArrayList<FormTemplate> getIPFormTemplates() {
		return mFormTemplates;
	}

	@Override
	public synchronized void addIPFormTemplate(FormTemplate template) {
		mFormTemplates.add(template);
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
		//mTargetList = listOfTargets;
	}

	@Override
	public synchronized void setIPImageToShow(BufferedImage image) {
		mIPImageToShow = image;
	}
	
	@Override
	public synchronized BufferedImage getIPImageToShow() {
		return mIPImageToShow;
	}

	@Override
	public synchronized void setIPCalibTemplate(ColorTemplate cTemplate) {
		mIPCalibTemplate = cTemplate;	
	}

	@Override
	public synchronized void addIPColorTemplate(ColorTemplate template) {
		mColorTemplates.add(template);
		
	}

	@Override
	public synchronized ArrayList<TargetObject> getIPTargetList() {
		return mTargetList;
	}

	@Override
	public synchronized ColorTemplate getIPCalibTemplate() {
		return mIPCalibTemplate;
	}

	@Override
	public FormTemplate getCalibFormTemplate() {
		return mIPCalibFormTemplate;
	}

	@Override
	public void setCalibFormTemplate(FormTemplate template) {
		mIPCalibFormTemplate = template;
	}
	
	
	
}
