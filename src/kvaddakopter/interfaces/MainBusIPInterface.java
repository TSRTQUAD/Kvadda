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
	public static final int SURPRISE_IMAGE 				= 4;
	public static final int TEMPLATE_MATCHING_IMAGE		= 5;

	//Intiation
	public void initIPVariables();
	
	//Flags
	/**
	 * 0:Colordetection, 1:Background subtraction 2:Template match, 3:Blur detection, 4:Color calibration, 5: Tracking
	 * @return
	 */
	public int[] getIPActiveModes();
	public void setIPActiveModes(int[] modes); //TODO in GUI interface
	public void activateIPMode(int i); //TODO in GUI interface
	public void deactivateIPMode(int i); //TODO in GUI interface
	//public boolean getIsIPModeActive(int i); //Not needed really but might be handier than to fetch all modes
	public boolean getIsIPRunning();
	public void setIsIPRunning(boolean b); //TODO in GUI interface
	
	//Image flags
	public int getIPImageMode();
	public void setIPImageMode(int imageMode);//TODO in GUI interface
	
	//Templates
	//Color
	public ArrayList<ColorTemplate> getIPColorTemplates();
	public void setIPColorTemplates(ArrayList<ColorTemplate> colorTemplates); //TODO in GUI interface
	public void addIPColorTemplate(ColorTemplate template); //TODO in GUI interface
	
	//Form
	public ArrayList<Template> getIPFormTemplates();
	public void setIPFormTemplates(ArrayList<Template> templates); //TODO in GUI interface
	public void addIPFormTemplate(Template template); //TODO in GUI interface
	
	//GPS
	public void setIPGPSCoordinate(GPSCoordinate coord); //TODO somewhere
	public GPSCoordinate getGPSCoordinate();
	
	//Targets
	public void setIPTargetList(ArrayList<TargetObject> listOfTargets);
	public ArrayList<TargetObject> getIPTargetList(); //TODO in GUI interface
	
	//Image object
	public void setIPImageToShow(BufferedImage image);
	public BufferedImage getIPImageToShow(); //TODO in GUI interface
	
	//Color Calibration
	public void setIPCalibTemplate(ColorTemplate cTemplate); //TODO in GUI interface
	public ColorTemplate getIPCalibTemplate(); 
}
