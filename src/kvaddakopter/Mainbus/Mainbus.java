package kvaddakopter.Mainbus;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.communication.Communication;
import kvaddakopter.communication.QuadData;
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
public class Mainbus extends Frame implements KeyListener,MainBusCommInterface, ControlMainBusInterface, AssignmentPlanerInterface, MainBusGUIInterface, MainBusIPInterface, IPAndGUIInterface{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Image processing storage
	private boolean mIsIPRunning;
	private ArrayList<TargetObject> mTargetList;

	
	//Assignment planer storage
	private MatlabProxyConnection matlabproxy;
	private MissionObject missionobject;
	//Flags
	private boolean mAssignmentPlanerRunning = false;
	
	//Communication
	Communication communicationtest;
	static float[] ControlSignal = new float[5];
	private String mode;
	private boolean StartPermission;
	public boolean selfCheck = false;
	float speed = (float)0.1;
	float batteryLevel = 99f;
    boolean shift = false;
    boolean runcontroller = false;
    boolean space_bar = false; //true = Takeoff, false = Landing
	public boolean EmerStop = false;
	double[][] NavDataOverAll = new double[3000][6];
	double[][] ControlSignalAll = new double[3000][5];
	public int seq = 0;
	public int seq_signal = 0;
	QuadData quadData = new QuadData();
	//
	
	
	
	//Control modules	
	@Override
	public synchronized QuadData getQuadData() {				
		return this.quadData;
	}
	
	
	public void setControlSignalobject(
		kvaddakopter.control_module.signals.ControlSignal csignal) {		
		if (true == this.runcontroller){
		//Controlsignal[Landing/Start Roll Pitch Gaz Yaw ]		
		//ControlSignal[0] = csignal.getStart();
		ControlSignal[1] = (float) csignal.getLateralvelocity();
		ControlSignal[2] = (float) -csignal.getForwardvelocity();
		//ControlSignal[3] = (float)  csignal.getHeightvelocity();
		ControlSignal[4] = (float)  -csignal.getYawrate();
		}
	}
	

	

	public static void main(String[] args) {

		//M�ste laddas i b�rjan av programmet... F�rslagsvis h�r.
		// 	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mainbus mainbus = new Mainbus();
		
		
//		//Setting up a Matlab Proxy Server
//		MatlabProxyConnection matlabproxy = new MatlabProxyConnection();
//		mainbus.setMatlabProxyConnection(matlabproxy);
//		matlabproxy.startMatlab("quiet");
//		
//		AssignmentPlanerRunnable assignmentplanerrunnable = new AssignmentPlanerRunnable(3,mainbus);
//		Thread t4 = new Thread(assignmentplanerrunnable);
//		t4.setPriority(1);
//		t4.start();
//
//		//Communication
//		try{
//			ControlSignal = new float[] {0, 0, 0, 0, 0};
//			Communication communicationtest = new Communication(3,mainbus,"Communication");
//			Thread t7 = new Thread(communicationtest);
//			t7.setDaemon(true);
//			t7.setPriority(1);
//			t7.start();
//			System.out.println("Communication-link initiated");
//
//
//			NavData navdatatest = new NavData(4,mainbus,"NavData", communicationtest);	
//			Thread t5 = new Thread(navdatatest);
//			t5.setDaemon(true);
//			t5.setPriority(1);
//			t5.start();
//			System.out.println("NavData-link initiated");
//
//
//
//		} catch (Exception ex1){
//
//			Security security = new Security(5,mainbus);
//			Thread t6 = new Thread(security);
//			t6.setDaemon(true);
//			t6.setPriority(1);
//			t6.start();
//			System.out.println("Security-link initiated");
//
//			ex1.printStackTrace();	
//		}
		
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
		addKeyListener(this); 
        setSize(320, 160);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);	
            }		
          });
		

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
	/*	ControlSignalAll[seq][0] = (double)ControlSignal[0];
		ControlSignalAll[seq][1] = (double)ControlSignal[1];
		ControlSignalAll[seq][2] = (double)ControlSignal[2];
		ControlSignalAll[seq][3] = (double)ControlSignal[3];
		ControlSignalAll[seq][4] = (double)ControlSignal[4];
 	*/
		seq_signal = seq_signal + 1;
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
	public synchronized boolean getStartPermission(){
		return StartPermission;
	}
	
	@Override
	public synchronized boolean EmergencyStop(){
		return EmerStop;
	}
	
	public synchronized void setEmergencyStop(boolean newBool){
		EmerStop = newBool;
	}
	
	public void keyTyped(KeyEvent e) {
        ;
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        try {
        control(keyCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        //if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) speed = (float)0.1; //Reset speed
        if (keyCode == KeyEvent.VK_SHIFT) shift = false;
    }

	
    //Control AR.Drone via AT commands per key code
    public void control(int keyCode) throws Exception {
        //System.out.println("Key: " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")");
    	
    	switch (keyCode) {
    		case KeyEvent.VK_C:
    			if (false == this.runcontroller){
    				this.runcontroller = true;
    			}
    			else if (true == this.runcontroller){
    				this.runcontroller = false;
    			}
     	    case KeyEvent.VK_1:
     	    	speed = (float)0.1;
    	    	break;
    	    case KeyEvent.VK_2:
    	    	speed = (float)0.2;
    	    	break; 
    	    case KeyEvent.VK_SHIFT:
    	        shift = true;
    	    	break;
    	    case KeyEvent.VK_E:
    	    	EmerStop = true;
    	    	break;
    	    case KeyEvent.VK_UP:
    	    	if (shift) {
    	    		
    		    System.out.println("Go Up (gaz+)");   
    		   // communicationtest.send_pcmd(1, 0, 0, speed, 0);
    	      // Controlsignal[Landing/Start Roll Pitch Gaz Yaw ] 
    	    		ControlSignal[4] =ControlSignal[4] + speed;
	    		
    	    	} else {
    	    	    System.out.println("Go Forward (pitch+)");   	    	   
     	    	  //  communicationtest.send_pcmd(1, 0, 0, 0, 0);
    	    	    ControlSignal[2] = ControlSignal[2] -  speed;
    	    	}
    	    	break;
    	    	
    	    case KeyEvent.VK_DOWN:
    	    	
    	    	if (shift) {
    	    	    System.out.println("Go Down (gaz-)");
    	    	    // communicationtest.send_pcmd(1, 0, 0, -speed, 0);
    	    	    ControlSignal[4] =ControlSignal[4] -speed;
    	    	} else {
    	    	    System.out.println("Go Backward (pitch-)");
    	    	    // communicationtest.send_pcmd(1, -speed, 0, 0, 0);
    	    	    ControlSignal[2] = ControlSignal[2] +speed;
    	    	}
       	    	break;
       	    	
    	    case KeyEvent.VK_LEFT:
    	        
    	    	if (shift) {
    	            System.out.println("Rotate Left (yaw-)");
    	            // communicationtest.send_pcmd(1, 0, 0, 0, -speed);
    	            ControlSignal[3]  =  ControlSignal[3] -speed;
    	            
    	    	} else {
    	    		System.out.println("Go Left (roll-)");
    	    		// communicationtest.send_pcmd(1, 0, -speed, 0, 0);
    	    	    ControlSignal[1]  = ControlSignal[1] -speed;
    	    	}
    	    	
   	    	break;
    	    case KeyEvent.VK_RIGHT:
    		if (shift) {
    			
    		    System.out.println("Rotate Right (yaw+)");
    		    // communicationtest.send_pcmd(1, 0, 0, 0, speed);
    		    ControlSignal[3] = ControlSignal[3] + speed;
    		    
    		    
		} else {
		    System.out.println("Go Right (roll+)");
		//    communicationtest.send_pcmd(1, 0, speed, 0, 0);
		    	ControlSignal[1] = ControlSignal[1] + speed;
		}
    	    	break;
    	    case KeyEvent.VK_SPACE:
    	    	space_bar = !space_bar;

    	    	
    	    	
    	    	
   	    if (space_bar && (ControlSignal[0]) == 0) {
    	    	 System.out.println("Takeoff");
   	    		 ControlSignal[0] = 1;


   	    } else if (space_bar && ControlSignal[0] == 1 ) {
   	    	System.out.println("Landing");
   	    	new MatFileHandler().createMatFileFromFlightData("FlightData", NavDataOverAll);
   	    	new MatFileHandler().createMatFileFromFlightData("ControlData", ControlSignalAll);
   	    	ControlSignal[0] = 0;
    	}

    	    	break;   	    	
    	    	
    	    	
    	    case KeyEvent.VK_CONTROL:
    	    	System.out.println("Hovering");
    	    	
    	    	// communicationtest.send_pcmd(1, 0, 0, 0, 0);
    	    	
    	    			   for(int i = 1; i < 5; i = i+1) {
    	    				   ControlSignal[i] = 0;
    	    			      }
    	    			   
		speed = (float)0.1; //Reset speed
    	    	break;
    	    	
    	    	
    	    default:
    	    	break;
    	}
    	
    	if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) System.out.println("Speed: " + speed);
    }


	@Override
	public double getCurrentSpeed() {
		return 1.23;
	}


	@Override
	public GPSCoordinate getCurrentQuadPosition() {
		return new GPSCoordinate(quadData.getGPSLat() , quadData.getGPSLong());
	}

	@Override
	public boolean wifiFixOk() {
		return true;
	}

	@Override
	public boolean gpsFixOk() {
		return true;
	}



	//Image processing-------------------------------------
	
	@Override
	public synchronized void initIPVariables() {
	
		//activateIPMode(COLOR_DETECTION_MODE);
		//activateIPMode(TEMPLATE_CALIBRATION_MODE);
		//setIPImageMode(TEMPLATE_CALIBRATE_IMAGE);
		//activateIPMode(TRACKING_MODE);
		//setIPImageMode(DEFAULT_IMAGE);
		
		mColorTemplates.add(new ColorTemplate("Pink square", 120, 200, 50, 90, 180, 245, ColorTemplate.FORM_SQUARE));	
		mColorTemplates.add(new ColorTemplate("Yellow square", 30, 120, 50, 120, 130, 255, ColorTemplate.FORM_SQUARE));
		
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
	public synchronized ArrayList<TargetObject> getIPTargetList() {
		return mTargetList;
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
	public synchronized void toggleController(){
		this.runcontroller = !this.runcontroller;
	}
	
	@Override
	public synchronized float getSpeed(){
		return speed;
	}
	
	@Override
	public synchronized float getBattery(){
		return batteryLevel;
	}
}
