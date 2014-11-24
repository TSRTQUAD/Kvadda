package kvaddakopter.image_processing.comm_tests;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import org.opencv.core.Core;

import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.data_types.Template;
import kvaddakopter.image_processing.programs.ImageProcessingMainProgram;
import kvaddakopter.image_processing.programs.TestBlurDetection;
import kvaddakopter.image_processing.programs.TestColorCalibration;
import kvaddakopter.interfaces.MainBusIPInterface;
import kvaddakopter.maps.GPSCoordinate;

public class IPMockMainBus implements MainBusIPInterface{
	
	private ArrayList<TargetObject> mTargetList;
	private ArrayList<ColorTemplate> mColorTemplates;
	private ColorTemplate mIPCalibTemplate;
	//private ImageObject mImageObject;
	private Image mIPImageToShow;
	private boolean mIsIPRunning;
	private int[] mIPActiveModes;
	private int mIPImageMode = 0;
	
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
		mIPActiveModes = new int[6];
		activateIPMode(COLOR_DETECTION_MODE);
		//activateIPMode(TEMPLATE_MATCHING_MODE);
		activateIPMode(TRACKING_MODE);
		setIPImageMode(DEFAULT_IMAGE);
		mTargetList = new ArrayList<TargetObject>();
		
		mColorTemplates = new ArrayList<ColorTemplate>();
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
		mIPActiveModes = modes;
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
	public synchronized void addIPFormTemplate(Template template) {
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
	public synchronized void setIPImageToShow(BufferedImage image) {
		WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        mIPImageToShow = wr;
	}
	
	@Override
	public synchronized Image getIPImageToShow() {
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
	
	
	
}
