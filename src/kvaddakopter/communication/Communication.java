
import java.net.*;
import java.util.*;
import java.awt.*; 
import java.awt.event.*;
import java.nio.*;


class Communication extends Frame {
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
    
   static final int INTERVAL = 100;
    
   
   
    InetAddress inet_addr;
    DatagramSocket AT_socket; 
    
   //  MainBus bus;
    int sequence = 1;
    int sequence_1 = 1;
    
    String AT_CMD_1 = "";

    FloatBuffer fb;
    IntBuffer ib;
    
    
    
    
    
    	public Communication(String name, String args[], InetAddress inet_addr) throws Exception{
       	super(name);  	
    
       	
       	
       //	this.bus = bus
       	
       	// bus.setCurrentSpeed(1000);
       	// bus.getCurrentSpeed(100)
       	
       	 AT_socket =  new DatagramSocket(Communication.AT_PORT);
       	 AT_socket.setSoTimeout(3000);
       	 
       	 this.inet_addr = inet_addr;
       	 
       	ByteBuffer bb = ByteBuffer.allocate(4);
        fb = bb.asFloatBuffer();
        ib = bb.asIntBuffer(); 
       	 
       	 // Initiering av ar-drone //       	 
    
       	 if (args.length == 2) { //Commandline mode
	    send_at_cmd(args[1]);
	    System.exit(0);
	}	
       	 
	
	System.out.println("CMD-SEQ");
       	 
	send_at_cmd("AT*PMODE=" + get_seq() + ",2");	
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*MISC=" + get_seq() + ",2,20,2000,3000");
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*REF=" + get_seq() + ",290717696");
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*COMWDG=" + get_seq());
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:altitude_max\",\"2000\""); //altitude max 2m
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:control_level\",\"0\""); //0:BEGINNER, 1:ACE, 2:MAX
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:navdata_demo\",\"TRUE\"");
	Thread.sleep(INTERVAL);
	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:video_enable\",\"TRUE\"");
	Thread.sleep(INTERVAL);
	//send_at_cmd("AT*CONFIG=" + get_seq() + ",\"network:owner_mac\",\"00:18:DE:9D:E9:5D\""); //my PC
	//send_at_cmd("AT*CONFIG=" + get_seq() + ",\"network:owner_mac\",\"00:23:CD:5D:92:37\""); //AP
	//Thread.sleep(INTERVAL);
	send_at_cmd("AT*CONFIG=" + get_seq() + ",\"pic:ultrasound_freq\",\"8\"");
	Thread.sleep(INTERVAL);
	//send_at_cmd("AT*FTRIM=" + get_seq()); //flat trim
	//Thread.sleep(INTERVAL);

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
	
       	 
       	 
       	}
 	
       	
       	
       	
	public static void main(String args[]) throws Exception{	
	System.out.println("Main Starting....");
	InetAddress inet_addr;
	String ip = "192.168.1.1";

	//  Convert ip-string to inet_address // 
	
	if (args.length >= 1) {
	    ip = args[0];
	}	
	
	
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

	System.out.println("IP: " + ip);
        inet_addr = InetAddress.getByAddress(ip_bytes);


	
	// -------------   --------------    // 
	
	Communication communication = new Communication("Communication", args, inet_addr);
	Thread.sleep(Communication.INTERVAL);
	
	NavData nd = new NavData(communication, inet_addr);
	nd.start();
	Thread.sleep(Communication.INTERVAL);
	
	communication.send_at_cmd("AT*REF=" + communication.get_seq() + ",290717696");
        
	
        while (true) {
            communication.sequence_1 = communication.sequence;
            Thread.sleep(200);
            if (communication.sequence == communication.sequence_1) communication.send_at_cmd(communication.AT_CMD_1);
        
         communication.control();
        }
        	
	}	

	   
	// -------- SEND AT COMMANDS ----------// 
	public synchronized void send_at_cmd(String AT_CMD) throws Exception {
    	
	System.out.println("AT command: " + AT_CMD);     	
    	AT_CMD_1 = AT_CMD;
    	byte[] buf_snd = (AT_CMD + "\r").getBytes();
	DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, Communication.AT_PORT);
	AT_socket.send(packet_snd); 	
	}
	
	
	// -------- SEND AT COMMANDS ---------- ABOVE // 
    
	  public void send_pcmd(int enable, float pitch, float roll, float gaz, float yaw) throws Exception {
    	
	// System.out.println("PCM : Speed: " + speed);
	
    	send_at_cmd("AT*PCMD=" + get_seq() + "," + enable + "," + intOfFloat(pitch) + "," + intOfFloat(roll)
    	    				+ "," + intOfFloat(gaz) + "," + intOfFloat(yaw));
    }
	
	//
	
	public synchronized int get_seq() {
    	return sequence++;
    	}
    
    	public int intOfFloat(float f) {
        fb.put(0, f);
        return ib.get(0);
        }
	
	
        public static int get_int(byte[] data, int offset) {
	int tmp = 0, n = 0;

	//System.out.println("get_int(): data = " + byte2hex(data, offset, 4));  
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
    
   
    
    public synchronized void control() throws Exception {
    /*
    
    System.out.println("start/landing");
    
    send_at_cmd("AT*REF=" + get_seq() + ",290718208");
    Thread.sleep(1000);
    send_at_cmd("AT*REF=" + get_seq() + ",290717696");
    */
    
    
    }
	


}

class NavData extends Thread { 
    DatagramSocket socket_nav;
    InetAddress inet_addr;
    Communication communication;

    public NavData(Communication communication, InetAddress inet_addr) throws Exception {
    	this.communication = communication;
	this.inet_addr = inet_addr;

	socket_nav = new DatagramSocket(Communication.NAV_PORT);
	socket_nav.setSoTimeout(3000);
    }

    public void run() {
    	int cnt = 0;
    	
	try {
	    byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
	    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, Communication.NAV_PORT);
	    socket_nav.send(packet_snd);
    	    System.out.println("Sent trigger flag to UDP port " + Communication.NAV_PORT);    	

	    communication.send_at_cmd("AT*CONFIG=" + communication.get_seq() + ",\"general:navdata_demo\",\"TRUE\"");

 	    byte[] buf_rcv = new byte[10240];
	    DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);            

	    while(true) {
		try {
		    socket_nav.receive(packet_rcv);
		  
		    /*
		    cnt++;    
		    if (cnt >= 5) {
		    
		    	cnt = 0;
		    	System.out.println("NavData Received: " + packet_rcv.getLength() + " bytes");
		    	
		    	
		    	System.out.println("Battery: " + communication.get_int(buf_rcv, Communication.NAVDATA_BATTERY)
		    			+ "%, Altitude: " + ((float)communication.get_int(buf_rcv, Communication.NAVDATA_ALTITUDE)/1000) + "m");

		    }*/

		System.out.println("Battery: " + communication.get_int(buf_rcv, Communication.NAVDATA_BATTERY)

        + "%, Altitude: " + ((float)communication.get_int(buf_rcv, Communication.NAVDATA_ALTITUDE)/1000) + "m");

        		System.out.println("navdata pitch: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_PITCH)/1000));

        		System.out.println("navdata roll: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_ROLL)/1000));

        		System.out.println("navdata yaw: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_YAW)/1000));

        		System.out.println("navdata Velocity X: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_VX)));
                                                                                    
        		System.out.println("navdata Velocity Y: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_VY)));
        		
        		System.out.println("navdata Velocity Z: "+ ((float)communication.get_int(buf_rcv, Communication.NAVDATA_VZ)));    
	
		    
		    
		} catch(SocketTimeoutException ex3) {
	    	    System.out.println("socket_nav.receive(): Timeout");
		} catch(Exception ex1) { 
		    ex1.printStackTrace(); 
		}
	    }
	} catch(Exception ex2) {
	    ex2.printStackTrace(); 
	}
}
	
}




