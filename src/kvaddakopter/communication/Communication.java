package kvaddakopter.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;

import kvaddakopter.Mainbus.*;


public class Communication implements Runnable{
    static final int NAV_PORT 	  = 5554;
    static final int VIDEO_PORT   = 5555;
    static final int AT_PORT 	  = 5556;	
    
	
	
        //NavData offset
    static final int NAVDATA_STATE    =  4;
    static final int NAVDATA_BATTERY  = 24;
    static final int NAVDATA_ALTITUDE = 40;
    
    
   static final int NAVDATA_PITCH   = 28;
   static final int NAVDATA_ROLL    = 32;
   static final int NAVDATA_YAW     = 36;
   static final int NAVDATA_VX = 44;
   static final int NAVDATA_VY = 48;
   static final int NAVDATA_VZ = 52; 
   static final int NAVDATA_GPS = 27;
    
   static final int INTERVAL = 100;
  
	
   private int mThreadId;
   private volatile Mainbus mMainbus;
   float[] ControlSignal = new float[5];
	
   InetAddress inet_addr;
   DatagramSocket AT_socket; 
   
   
   int sequence = 1;
   int sequence_1 = 1;
   
   String AT_CMD_1 = "";

   FloatBuffer fb;
   IntBuffer ib;
   
   public Communication(int threadid,Mainbus mainbus, String name)  {
        mThreadId = threadid;
        mMainbus = mainbus;
            
        System.out.println("Init Communication");        
    	
        InetAddress inet_addr;
    	String ip = "192.168.1.1";
        
    	StringTokenizer st = new StringTokenizer(ip, ".");

    	byte[] ip_bytes = new byte[4];
    	if (st.countTokens() == 4){
     	    for (int i = 0; i < 4; i++){
    		ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
    	    }
    	}
    	else {
    	    System.out.println("Incorrect IP address format: " + ip);
    	    System.exit(-1);
    	}

    	
    	try {
    	
            inet_addr = InetAddress.getByAddress(ip_bytes);
             
         	 AT_socket =  new DatagramSocket(Communication.AT_PORT);
          	 AT_socket.setSoTimeout(3000);
          	 
          	 this.inet_addr = inet_addr;
          	 
         	System.out.println("CMD-SEQ-1");
                	 
         	send_at_cmd("AT*PMODE=" + get_seq() + ",2");
         	
         	Thread.sleep(INTERVAL);
         	
         	send_at_cmd("AT*MISC=" + get_seq() + ",2,20,2000,3000");
         	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*REF=" + get_seq() + ",290717696");
         	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*COMWDG=" + get_seq());
         	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:altitude_max\",\"1000\""); //altitude max 2m
         	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:control_level\",\"0\""); //0:BEGINNER, 1:ACE, 2:MAX
         	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:navdata_demo\",\"TRUE\"");
         	Thread.sleep(INTERVAL);
        	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:navdata_options\",\"777060865\"");
         	Thread.sleep(INTERVAL);
          	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:video_enable\",\"TRUE\"");
          	Thread.sleep(INTERVAL);
         	//send_at_cmd("AT*CONFIG=" + get_seq() + ",\"network:owner_mac\",\"00:18:DE:9D:E9:5D\""); //my PC
         	//send_at_cmd("AT*CONFIG=" + get_seq() + ",\"network:owner_mac\",\"00:23:CD:5D:92:37\""); //AP
         	//Thread.sleep(INTERVAL);
         	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"pic:ultrasound_freq\",\"8\"");
         	Thread.sleep(INTERVAL);
         	
         	send_at_cmd("AT*FTRIM=" + get_seq()); //flat trim
         	//Thread.sleep(INTERVAL);
        	Thread.sleep(INTERVAL);
         	
         	send_at_cmd("AT*REF=" + get_seq() + ",290717696");
         	Thread.sleep(INTERVAL);
        	
             	send_pcmd(0, 0, 0, 0, 0);

             	Thread.sleep(INTERVAL);
         	send_at_cmd("AT*REF=" + get_seq() + ",290717696");
         	Thread.sleep(INTERVAL);
         	//send_at_cmd("AT*REF=" + get_seq() + ",290717952"); //toggle Emergency
         	//Thread.sleep(INTERVAL);
         	send_at_cmd("AT*REF=" + get_seq() + ",290717696");
       	
         	System.out.println("Init done...");
                             
    	} catch(Exception ex2) {
    	    ex2.printStackTrace(); 
    	} 
            
           
    	System.out.println("IP: " + ip);
    	
      	 
      	ByteBuffer bb = ByteBuffer.allocate(4);
       fb = bb.asFloatBuffer();
       ib = bb.asIntBuffer(); 
      	 
    	// // 	
    
    	
    }
    
	public void run()  {
		System.out.println("Main Starting....");	
		// (selfCheck() && mMainbus.getStartPermission())

		System.out.println("Waiting....For Permisson");
		
		try{
	    Thread.sleep(500);
	 	send_at_cmd("AT*REF=" + get_seq() + ",290717696"); // Landing
	 
			
		while (ControlSignal[0] == 0){
			System.out.println("Not Started Yet");
			Thread.sleep(500);
			
	        if (sequence == sequence_1) send_at_cmd(AT_CMD_1);
			send_at_cmd("AT*REF=" + get_seq() + ",290717696");
			
			ControlSignal = mMainbus.getControlSignal(); 		
		
		}
		
		System.out.println("Starting");
		
		send_at_cmd("AT*REF=" + get_seq() + ",290718208"); // Starting 
		Thread.sleep(200);
		
		while(true){	// SET STARTING CONDITION //
	        
				if(mMainbus.EmergencyStop()){
					send_at_cmd("AT*REF=" + get_seq() + ",290717952"); // FALL
					System.out.println("EmergencyStop");
					
					break;
					
					
					
				}
			
		    		Thread.sleep(50);
			        if (sequence == sequence_1) send_at_cmd(AT_CMD_1);
			        
					ControlSignal = mMainbus.getControlSignal(); 	
					
			        sequence_1 = sequence;     
			       // send_pcmd(1,0, 0, 0, 0);   

					//System.out.println("  CS[1]:  " + ControlSignal[1] + "CS[2]   " + ControlSignal[2] + "CS[3]  " + ControlSignal[3] + "CS[4]  " + ControlSignal[4]);
				    send_pcmd(1, ControlSignal[1], ControlSignal[2], ControlSignal[3], ControlSignal[4]);   	 
			        

					
					if (ControlSignal[0] == 0){
						
						send_at_cmd("AT*REF=" + get_seq() + ",290717696");	// Landing
					
					while(true){
					//	System.out.println("Landing");
					}
						
					}

		} 
					} catch(Exception ex1){
					
						try{
						send_at_cmd("AT*REF=" + get_seq() + ",290717696"); // Landing
						ex1.printStackTrace();
						}catch(Exception ex2){	
						ex2.printStackTrace();	
						}						
					} 

		
	}
	
		
	public void selfCheck (){
	
		// DO THE SELFCHECK //
    mMainbus.setSelfCheck(true);
	System.out.println("Selfcheck Complete");	
	
	}
        

	// -------- SEND AT COMMANDS ----------// 
		public synchronized void send_at_cmd(String AT_CMD) throws Exception {
	    	
	//	System.out.println("AT command: " + AT_CMD);     	
	    	AT_CMD_1 = AT_CMD;
	    	byte[] buf_snd = (AT_CMD + "\r").getBytes();
		DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, Communication.AT_PORT);
		AT_socket.send(packet_snd); 	
		}
		
		
		// -------- SEND AT COMMANDS ---------- ABOVE // 
	    
		  public void send_pcmd(int enable, float pitch, float roll, float gaz, float yaw){
	    	
		// System.out.println("PCM : Speed: " + speed);
		
		try{	  
			
		   	send_at_cmd("AT*PCMD=" + get_seq() + "," + enable + "," + intOfFloat(pitch) + "," + intOfFloat(roll)
    				+ "," + intOfFloat(gaz) + "," + intOfFloat(yaw));
		} catch(Exception ex1){
			ex1.printStackTrace();
		}

		  }
		
		//
		
		public synchronized int get_seq() {
	    	return sequence++;
	    	}
	    
	    public int intOfFloat(float f) {
	    if (f == 0){ 
	    	// System.out.println("Zero's");
	    	return 0; } 
	    	
	    fb.put(0, f);
	    return ib.get(0);
	    }
		
		
	    public static int get_int(byte[] data, int offset) {
		int tmp = 0, n = 0;

		// System.out.println("get_int(): data = " + byte2hex(data, offset, 4));  
		
		
		for (int i=3; i>=0; i--) {   
		    n <<= 8;
		    tmp = data[offset + i] & 0xFF;   
		    n |= tmp;   
		}
			
	        return n;   
	    
	    } 
	    
	    
	    
	    
	        public static String byte2hex(byte[] data, int offset, int len) {
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < len; i++) {
	            String tmp = Integer.toHexString(((int) data[offset + i]) & 0xFF);
	            for(int t = tmp.length();t<2;t++)
	            {
	                sb.append("0");
	            }
	            sb.append(tmp);
	            sb.append(" ");
	        }
	        return sb.toString();
	    }

}



