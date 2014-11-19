package kvaddakopter.interfaces;

import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.*;
import kvaddakopter.maps.GPSCoordinate;
public interface MainBusIPInterface {
	//Intiation
	public void initIPVariables();
	
	//Flags
	public int[] getIPActiveModes();
	public void setIPActiveModes(int[] modes);
	public void removeIPMode(int i);
	public boolean getIsIPRunning();
	public void setIsIPRunning(boolean b);
	
	//Image flags
	public int getIPImageMode();
	public void setIPImageMode(int imageMode);
	
	//Templates
	public ArrayList<ColorTemplate> getIPColorTemplates();
	public void setIPColorTemplates(ArrayList<ColorTemplate> colorTemplates);
	public void addIPColorTemplate(ColorTemplate template);
	
	public ArrayList<Template> getIPFormTemplates();
	public void setIPFormTemplates(ArrayList<Template> templates);
	public void addIPFormTemplates(Template template);
	
	//GPS
	public void setIPGPSCoordinate(GPSCoordinate coord);
	public GPSCoordinate getGPSCoordinate();
	
	//Targets
	public void setIPTargetList(ArrayList<TargetObject> listOfTargets);
	public ArrayList<TargetObject> getIPTargetList();
	
	//Image object
	public void setIPImageObject(ImageObject imageObject);
	
	//Color Calibration
	public void setIPCalibTemplate(ColorTemplate cTemplate);
	public ColorTemplate getIPCalibTemplate();
}
