package kvaddakopter.interfaces;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.scene.image.Image;
import kvaddakopter.image_processing.data_types.*;
import kvaddakopter.maps.GPSCoordinate;
public interface MainBusIPInterface {
	
	public static final int MODE_COLOR_DETECTION			= 0;
	public static final int MODE_TEMPLATE_MATCHING 			= 1;
	public static final int MODE_BACKGROUND_SUBTRACION 		= 2;
	public static final int MODE_BLUR_DETECTION 			= 3;
	public static final int MODE_COLOR_CALIBRATION 			= 4;
	public static final int MODE_TRACKING		 			= 5;
	public static final int MODE_TEMPLATE_CALIBRATION		= 6;

	public static final int IMAGE_DEFAULT				= 0;
	public static final int IMAGE_TARGET 				= 2;
	public static final int IMAGE_CUT_OUT 				= 3;
	public static final int IMAGE_SURPRISE 				= 4;
	public static final int IMAGE_TEMPLATE_MATCHING		= 5;
	public static final int IMAGE_TEMPLATE_CALIBRATE	= 6;
	public static final int IMAGE_COLOR_CALIBRRATE		= 7;

	//Interface variables
	
	//Lists
	ArrayList<FormTemplate> mFormTemplates = new ArrayList<FormTemplate>();
	ArrayList<TargetObject> mTargetList = new ArrayList<TargetObject>();
	ArrayList<ColorTemplate> mColorTemplates = new ArrayList<ColorTemplate>();
	
	FormTemplate mIPCalibFormTemplate[] = new FormTemplate[1];
	ColorTemplate mIPCalibTemplate[] = new ColorTemplate[1];
	
	//Modes
	int[] mIPActiveModes = new int[10];
	int[] mIPImageMode =new int[]{IMAGE_DEFAULT};
	
	Image mIPImageToShow[] = new Image[1];
	
	//Intiation
	public void initIPVariables();
	
	
	
	//Flags
	/**
	 * 0:Colordetection, 1:Background subtraction 2:Template match, 3:Blur detection, 4:Color calibration, 5: Tracking
	 * @return
	 */
	public int[] getIPActiveModes();
	public void setIPActiveModes(int[] modes);
	public void activateIPMode(int i);
	public void deactivateIPMode(int i);
	//public boolean getIsIPModeActive(int i); //Not needed really but might be handier than to fetch all modes
	public boolean getIsIPRunning();
	public void setIsIPRunning(boolean b);
	
	//Image flags
	public int getIPImageMode();
	public void setIPImageMode(int imageMode);
	
	//Templates
	//Color
	public ArrayList<ColorTemplate> getIPColorTemplates();
	public void setIPColorTemplates(ArrayList<ColorTemplate> colorTemplates);
	public void addIPColorTemplate(ColorTemplate template);
	
	//Form
	public ArrayList<FormTemplate> getIPFormTemplates();
	public void addIPFormTemplate(FormTemplate template);
	public FormTemplate getCalibFormTemplate();
	public void setCalibFormTemplate(FormTemplate template);
	
	//GPS
	public void setIPGPSCoordinate(GPSCoordinate coord);
	public GPSCoordinate getGPSCoordinate();
	
	//Targets
	public void setIPTargetList(ArrayList<TargetObject> listOfTargets);
	public ArrayList<TargetObject> getIPTargetList();
	
	//Image object
	public void setIPImageToShow(BufferedImage image);
	public Image getIPImageToShow();
	
	//Color Calibration
	public void setIPCalibTemplate(ColorTemplate cTemplate);
	public ColorTemplate getIPCalibTemplate(); 
}
