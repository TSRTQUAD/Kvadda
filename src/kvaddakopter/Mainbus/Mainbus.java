package kvaddakopter.Mainbus;

import kvaddakopter.communication.*;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// import org.opencv.core.Core;








import kvaddakopter.ImageProcessingMain;
import kvaddakopter.assignment_planer.AssignmentPlanerRunnable;
import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.control_module.Mockmainbus;
import kvaddakopter.control_module.Sensorfusionmodule;
import kvaddakopter.control_module.signals.ControlSignal;
import kvaddakopter.control_module.signals.SensorData;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.programs.ImageProcessingMainProgram;
import kvaddakopter.interfaces.AssignmentPlanerInterface;
import kvaddakopter.interfaces.ControlMainBusInterface;


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
public class Mainbus extends Frame implements KeyListener,ControlMainBusInterface, AssignmentPlanerInterface{
	//Examples
	private int var;
	public boolean condVar = false;

	
	
	//Programs
	MyRunnable myRunnable;
	MyRunnable2 myRunnable2;
	AssignmentPlanerRunnable assignmentplanerrunnable;
	
	ImageProcessingMainProgram imageProcessing;
	
	//Image processing storage
	//TODO
	
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
    boolean shift = false;
    boolean runcontroller = false;
    boolean space_bar = false; //true = Takeoff, false = Landing
	public boolean EmerStop = false;
	double[] NavData = new double[6];
	double[][] NavDataOverAll = new double[500000][6];
	double[][] ControlSignalAll = new double[500000][5];
	public int seq = 0;
	public int seq_signal = 0;
	//
	
	
	
	//Control modules	
	public synchronized double[] getSensorVector() {				
		return this.NavData;
	}
	
	
	public void setControlSignalobject(
		kvaddakopter.control_module.signals.ControlSignal csignal) {		
		if (true == this.runcontroller){
		//Controlsignal[Landing/Start Roll Pitch Gaz Yaw ]		
		//ControlSignal[0] = csignal.getStart();
		//ControlSignal[1] = (float) 	csignal.getLateralvelocity();
		//ControlSignal[2] = (float) 	csignal.getForwardvelocity();
		//ControlSignal[3] = (float)  	csignal.getHeightvelocity();
		ControlSignal[4] = (float)  	csignal.getYawrate();
		}
	}
	

	

	public static void main(String[] args) {

		//M�ste laddas i b�rjan av programmet... F�rslagsvis h�r.
		// 	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mainbus mainbus = new Mainbus();
		System.out.println("created mainbus");
		//ImageProcessingMainProgram imageProcessing = new ImageProcessingMainProgram(1,mainbus);
		//System.out.println("imageprocessing initiated");

		//Setting up a Matlab Proxy Server
		/*MatlabProxyConnection matlabproxy = new MatlabProxyConnection();
		mainbus.setMatlabProxyConnection(matlabproxy);
		matlabproxy.startMatlab("quiet"); */

		//Thread t3 = new Thread(imageProcessing);
		//t3.setPriority(1);
		//t3.start(); 

		MyRunnable myRunnable = new MyRunnable(1,mainbus);
		MyRunnable2 myRunnable2 = new MyRunnable2(2,mainbus);
		AssignmentPlanerRunnable assignmentplanerrunnable = new AssignmentPlanerRunnable(3,mainbus);



		Thread t = new Thread(myRunnable);
		t.setPriority(1);
		t.start();

		/*Thread t4 = new Thread(assignmentplanerrunnable);
		t4.setPriority(1);
		t4.start();*/

		//Communication
		try{
			ControlSignal = new float[] {0, 0, 0, 0, 0};
			Communication communicationtest = new Communication(3,mainbus,"Communication");
			Thread t7 = new Thread(communicationtest);
			t7.setDaemon(true);
			t7.setPriority(1);
			t7.start();
			System.out.println("Communication-link initiated");


			NavData navdatatest = new NavData(4,mainbus,"NavData", communicationtest);	
			Thread t5 = new Thread(navdatatest);
			t5.setDaemon(true);
			t5.setPriority(1);
			t5.start();
			System.out.println("NavData-link initiated");




		} catch (Exception ex1){

			Security security = new Security(5,mainbus);
			Thread t6 = new Thread(security);
			t6.setDaemon(true);
			t6.setPriority(1);
			t6.start();
			System.out.println("Security-link initiated");

			ex1.printStackTrace();	
		}


		//
		// START MODULE	    	
		Sensorfusionmodule sensmodule = new Sensorfusionmodule(mainbus);
		Thread t8 = new Thread(sensmodule);
		t8.setDaemon(true);
		t8.setPriority(1);
		t8.start();
		while(true){
			//			System.out.println("Mainbus running");
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
	//TODO image processing bus functionallity (being implemented in IPMockMainbus)
	
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

	public synchronized float[] getControlSignal(){
		ControlSignalAll[seq][0] = (double)ControlSignal[0];
		ControlSignalAll[seq][1] = (double)ControlSignal[1];
		ControlSignalAll[seq][2] = (double)ControlSignal[2];
		ControlSignalAll[seq][3] = (double)ControlSignal[3];
		ControlSignalAll[seq][4] = (double)ControlSignal[4];
 	
		seq_signal = seq_signal + 1;
		//System.out.println("Pos 1:   " + ControlSignal[1] + "Pos 2:   " + ControlSignal[2]);
		return ControlSignal;
	}
	
	public synchronized void setNavData(double[] nd){
		NavDataOverAll[seq][0] = nd[0];
		NavDataOverAll[seq][1] = nd[1];
		NavDataOverAll[seq][2] = nd[2];
		NavDataOverAll[seq][3] = nd[3];
		NavDataOverAll[seq][4] = nd[4];
		NavDataOverAll[seq][5] = nd[5];
	
		seq = seq + 1;
		this.NavData = nd;
	}
	
	
	public synchronized void setSelfCheck(boolean b){
	    selfCheck = true;
	}
	
	public synchronized String getMode(){
		return mode; 
	}
	
	public synchronized boolean getStartPermission(){
		return StartPermission;
	}
	
	public synchronized boolean EmergencyStop(){
	return EmerStop;
//	return true;
	}
	
	
	///// 
	
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
    	    	EmerStop = true;;
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
	   	    //	new MatFileHandler().createMatFileFromFlightData("FlightData", NavDataOverAll);
	   	    //	new MatFileHandler().createMatFileFromFlightData("ControlData", ControlSignalAll);
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



	//


	
}
