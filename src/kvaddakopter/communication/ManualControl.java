package kvaddakopter.communication;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import kvaddakopter.interfaces.ManualControlInterface;

public class ManualControl extends Frame implements KeyListener, Runnable{


	private volatile ManualControlInterface mMainbus;
	float speed = (float)0.1;
	public boolean EmerStop = false;
	static float[] ControlSignal = new float[5];
    boolean shift = false;
    boolean space_bar = false; //true = Takeoff, false = Landing
    
	
	public ManualControl(int threadid, ManualControlInterface mainbus) {	
		mMainbus = mainbus;		
		
		addKeyListener(this);
        setSize(320, 160);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);	
            }		
          });	
                
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
    			/*
    			if (false == this.runcontroller){
    				this.runcontroller = true;
    			}
    			else if (true == this.runcontroller){
    				this.runcontroller = false;
    			}
    			 */
    			if (false == mMainbus.getRunController()){
    				mMainbus.setRunController(true);
    			}
    			else if (true == mMainbus.getRunController()){
    				mMainbus.setRunController(false);
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
    	    	mMainbus.setEmergencyStop(true);
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



	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			Thread.sleep(50);
			mMainbus.setSpeed(speed);
			mMainbus.setControlSignal(ControlSignal);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
}
