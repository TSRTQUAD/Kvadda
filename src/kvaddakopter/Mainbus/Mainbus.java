package kvaddakopter.Mainbus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import kvaddakopter.assignment_planer.AssignmentPlanerRunnable;
import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.communication.Communication;
import kvaddakopter.communication.ManualControl;
import kvaddakopter.communication.NavData;
import kvaddakopter.communication.QuadData;
import kvaddakopter.communication.Security;
import kvaddakopter.control_module.Sensorfusionmodule;
import kvaddakopter.gui.GUIModule;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.programs.ImageProcessingMainProgram;
import kvaddakopter.interfaces.AssignmentPlanerInterface;
import kvaddakopter.interfaces.ControlMainBusInterface;
import kvaddakopter.interfaces.IPAndGUIInterface;
import kvaddakopter.interfaces.MainBusCommInterface;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.interfaces.MainBusIPInterface;
import kvaddakopter.interfaces.ManualControlInterface;
import kvaddakopter.maps.GPSCoordinate;

// import org.opencv.core.Core;


/**
 * Mainbus class
 * This is the main thread of the project
 * 
 * How to make new process
 * 1) Implement classes as runnables, see MyRunnable for setup
 * 2) Create thread with your runnable
 * 3) start thread
 * 
 */
public class Mainbus implements ManualControlInterface, MainBusCommInterface, ControlMainBusInterface, AssignmentPlanerInterface, MainBusGUIInterface, MainBusIPInterface, IPAndGUIInterface{
	
	//Image processing storage
	private boolean mIsIPRunning;
	private ArrayList<TargetObject> mTargetList = new ArrayList<TargetObject>();
	Image mIPImageToShow[] = new Image[1];
	
	
	//GENERAL
	private boolean isStarted = false;
	private boolean shouldStart = false;
	private boolean mIsArmed = false;
	
	//Assignment planer storage
	private MatlabProxyConnection matlabproxy;
	private MissionObject missionobject;
	private double nrofvisitedpoints = 0;
	//Flags
	private boolean mAssignmentPlanerRunning = false;
	
	//Communication
	//Communication communicationtest;
	static float[] ControlSignal = {1f,0,0,0,0};
	private String mode;
	public boolean selfCheck = false;
	float speed = -1f;
    boolean runcontroller = true;
	public boolean EmerStop = false;
	public int seq = 0;
	public int seq_signal = 0;
	QuadData quadData = new QuadData();

	private boolean gpsFixOk;
	private boolean wifiFixOk;
	
	//Control modules	
	@Override
	public synchronized QuadData getQuadData() {				
		return this.quadData;
	}
	
	
	public void setControlSignalobject(
		kvaddakopter.control_module.signals.ControlSignal csignal) {		
		if (this.runcontroller){
		//Controlsignal[Landing/Start Roll Pitch Gaz Yaw ]		
		ControlSignal[0] = csignal.getStart();
		ControlSignal[1] = (float) 		csignal.getLateralvelocity();
		ControlSignal[2] = (float) 		-csignal.getForwardvelocity();
		ControlSignal[3] = (float)  	csignal.getHeightvelocity();
		ControlSignal[4] = (float)  	csignal.getYawrate();
		}
	}
	
	
	public static void main(String[] args) {


		Mainbus mainbus = new Mainbus();
		
		(new Thread(new Runnable(){
			@Override
			public void run(){
				//Setting up a Matlab Proxy Server
				MatlabProxyConnection matlabproxy = new MatlabProxyConnection();
				mainbus.setMatlabProxyConnection(matlabproxy);
				matlabproxy.startMatlab("quiet");

			}

		})).start();

		AssignmentPlanerRunnable assignmentplanerrunnable = new AssignmentPlanerRunnable(3,mainbus);
		Thread t4 = new Thread(assignmentplanerrunnable);
		t4.setPriority(1);
		t4.start();

		
		try{
			Communication communication = new Communication(3,mainbus,"Communication");
			Thread t7 = new Thread(communication);
			t7.setDaemon(true);
			t7.setPriority(1);
			t7.start();


			NavData navdata = new NavData(4,mainbus,"NavData", communication);	
			Thread t5 = new Thread(navdata);
			t5.setDaemon(true);
			t5.setPriority(1);
			t5.start();
		
			
			ManualControl manualcontrol = new ManualControl(5,mainbus);
			Thread t2 = new Thread(manualcontrol);
			t2.setDaemon(true);
			t2.setPriority(1);
			t2.start();

		} catch (Exception ex1){

			Security security = new Security(5,mainbus);
			Thread t6 = new Thread(security);
			t6.setDaemon(true);
			t6.setPriority(1);
			t6.start();
			System.out.println("Security-link initiated");

			ex1.printStackTrace();	
		}
		
		//GUI MODULE
		GUIModule guiModule = new GUIModule(mainbus);
		Thread t10 = new Thread(guiModule);
		t10.setDaemon(true);
		t10.setPriority(5);
		t10.start();
		
		// Control module MODULE	    	
		Sensorfusionmodule sensmodule = new Sensorfusionmodule(mainbus);
		Thread t8 = new Thread(sensmodule);
		t8.setDaemon(true);
		t8.setPriority(1);
		t8.start();
		
		// Image processing MODULE
		ImageProcessingMainProgram imageProcessing = new ImageProcessingMainProgram(1,mainbus);
		mainbus.initIPVariables();
		Thread t9 = new Thread(imageProcessing);
		t9.setDaemon(true);
		t9.setPriority(1);
		t9.start();
		
		while(true){
			//			System.out.println("Mainbus running");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public Mainbus(){		
	}
	
	/*
	 * Get/set functions for Mission Planing
	 */
	public synchronized void setMissionObject(MissionObject MO){
		missionobject = MO;
	}
	
	public synchronized MissionObject getMissionObject() {
		return missionobject;
	}
	
	public synchronized void setAssignmentPlanerOn(boolean state){
		mAssignmentPlanerRunning = state;
	}
	
	public synchronized boolean isAssignmentPlanerOn() {
		return mAssignmentPlanerRunning;
	}

	public void setMatlabProxyConnection(MatlabProxyConnection MPC) {
		this.matlabproxy = MPC;
		
	}

	public MatlabProxyConnection getMatlabProxyConnection() {
		return this.matlabproxy;
	}
	
	//Communication
	@Override
	public synchronized float[] getControlSignal(){
		
		seq_signal = seq_signal + 1;
		//System.out.println("Pos 1:   " + ControlSignal[1] + "Pos 2:   " + ControlSignal[2]);
		return ControlSignal;
	}
	

	@Override
	public synchronized void setQuadData(QuadData quadData){
		seq = seq + 1;
		this.quadData = quadData;
	}
	
	@Override
	public synchronized void setSelfCheck(boolean b){
	    selfCheck = true;
	}
	
	@Override
	public synchronized String getMode(){
		return mode; 
	}
	
	
	@Override
	public synchronized boolean EmergencyStop(){
		return EmerStop;
	}
	
	public synchronized void setEmergencyStop(boolean newBool){
		EmerStop = newBool;
	}
	
	@Override
	public double getCurrentSpeed() {
		return Math.sqrt(Math.pow(quadData.getVx(), 2) + Math.pow(quadData.getVy(), 2) + Math.pow(quadData.getVz(), 2));
	}


	@Override
	public GPSCoordinate getCurrentQuadPosition() {
		return new GPSCoordinate(quadData.getGPSLat() , quadData.getGPSLong());
	}

	@Override
	public boolean wifiFixOk() {
		return this.wifiFixOk;
	}

	@Override
	public boolean gpsFixOk() {
		return this.gpsFixOk;
	}

	@Override
	public synchronized HashMap<String, GPSCoordinate> getTargets() {
		HashMap<String, GPSCoordinate> hashMap = new HashMap<String, GPSCoordinate>();
		for(TargetObject target : mTargetList){
			hashMap.put(Integer.toString(target.getID()), target.getGPSCoordinate());
		}
		return hashMap;
	}

	//Image processing-------------------------------------
	
	@Override
	public synchronized void initIPVariables() {
		mColorTemplates.add(new ColorTemplate("Pink square", 120, 200, 50, 90, 180, 245, ColorTemplate.FORM_SQUARE));
		mColorTemplates.add(new ColorTemplate("Yellow square", 30, 120, 50, 120, 130, 255, ColorTemplate.FORM_SQUARE));
		mColorTemplates.get(0).deactivate();
		mColorTemplates.get(1).deactivate();
		mTargetList = new ArrayList<TargetObject>();
		mIPCalibTemplate[0] = new ColorTemplate();
		mIPImageToShow[0] = null;
		
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
		return mIPImageMode[0];
	}

	@Override
	public synchronized void setIPImageMode(int imageMode) {
		mIPImageMode[0] =imageMode;
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
        mIPImageToShow[0] = wr;
	}
	
	@Override
	public synchronized Image getIPImageToShow() {
		return mIPImageToShow[0];
	}

	@Override
	public synchronized void setIPCalibTemplate(ColorTemplate cTemplate) {
		mIPCalibTemplate[0] = cTemplate;	
	}

	@Override
	public synchronized void addIPColorTemplate(ColorTemplate template) {
		mColorTemplates.add(template);
		
	}

	@Override
	public synchronized ColorTemplate getIPCalibTemplate() {
		return mIPCalibTemplate[0];
	}

	@Override
	public synchronized FormTemplate getCalibFormTemplate() {
		return mIPCalibFormTemplate[0];
	}

	@Override
	public synchronized void setCalibFormTemplate(FormTemplate template) {
		mIPCalibFormTemplate[0] = template;
	}

	@Override
	public synchronized Image getImage() {
		return mIPImageToShow[0];
	}
	
	@Override
	public synchronized boolean toggleController(){
		this.runcontroller = !this.runcontroller;
		return this.runcontroller;
	}
	
	
	@Override
	public synchronized float getBattery(){
		return quadData.getBatteryLevel();
	}


	@Override
	public synchronized void setIsStarted(boolean isStarted) {
		this.isStarted = isStarted;
		
	}
	
	@Override
	public synchronized boolean isStarted() {
		return this.isStarted;
		
	}


	@Override
	public synchronized void setShouldStart(boolean b) {
		this.shouldStart = b;
		
	}


	@Override
	public synchronized boolean shouldStart() {
		return this.shouldStart;
	}


	@Override
	public synchronized void setGpsFixOk(boolean b) {
		this.gpsFixOk = b;
		
	}


	@Override
	public synchronized void setWifiFixOk(boolean b) {
		this.wifiFixOk = b;
		
	}



	@Override
	public synchronized void setSpeed(float spd) {
		speed = spd;		
	}

	@Override
	public synchronized boolean getManualControl() {
		return !runcontroller;
	}

	@Override
	public synchronized void setManualControl(boolean mcb) {
		this.runcontroller = !mcb;
	}

	@Override
	public synchronized void setControlSignal(float[] controlsignal) {
		ControlSignal = controlsignal;
	}


	@Override
	public synchronized boolean getRunController() {
	return this.runcontroller;
	}

	@Override
	public void setRunController(boolean runctrl) {
		// TODO Auto-generated method stub
		this.runcontroller = runctrl;	
	}


	@Override
	public boolean getIsArmed() {
		return mIsArmed;
	}


	@Override
	public void setIsArmed(boolean b) {
		mIsArmed = true;
		
	}
	
	public boolean getStartPermission() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setVisitedPoints(int counter) {
		this.nrofvisitedpoints = counter;
		
	}


	@Override
	public double getVisitedPoints() {
		return this.nrofvisitedpoints;
		
	}


}
