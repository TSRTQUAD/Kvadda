package kvaddakopter.interfaces;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.*;
import kvaddakopter.maps.GPSCoordinate;
public interface MainBusIPInterface {
	public static final int COLOR_DETECTION_MODE			= 0;
	public static final int TEMPLATE_MATCHING_MODE 			= 1;
	public static final int BACKGROUND_SUBTRACION_MODE 		= 2;
	public static final int BLUR_DETECTION_MODE 			= 3;
	public static final int COLOR_CALIBRATION_MODE 			= 4;
	public static final int TRACKING_MODE		 			= 5;

	public static final int DEFAULT_IMAGE				= 0;
	public static final int TARGET_IMAGE 				= 2;
	public static final int CUT_OUT_IMAGE 				= 3;
	public static final int SUPRISE_IMAGE 				= 4;

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
	public ArrayList<Template> getIPFormTemplates();
	public void setIPFormTemplates(ArrayList<Template> templates);
	public void addIPFormTemplate(Template template);
	
	//GPS
	public void setIPGPSCoordinate(GPSCoordinate coord);
	public GPSCoordinate getGPSCoordinate();
	
	//Targets
	public void setIPTargetList(ArrayList<TargetObject> listOfTargets);
	public ArrayList<TargetObject> getIPTargetList();
	
	//Image object
	public void setIPImageToShow(BufferedImage image);
	public BufferedImage getIPImageToShow();
	
	//Color Calibration
	public void setIPCalibTemplate(ColorTemplate cTemplate);
	public ColorTemplate getIPCalibTemplate();
}
