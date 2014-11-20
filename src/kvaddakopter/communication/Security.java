package kvaddakopter.communication;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import kvaddakopter.Mainbus.*;


public class Security implements Runnable {

		static final int CNT_PORT = 5559;
	    static final int AT_PORT  = 5556;	
		
	   InetAddress inet_addr;
	   DatagramSocket AT_socket; 
	   
	   String AT_CMD_1 = "";

	   private int mThreadId;
	   private volatile Mainbus mMainbus;
	
	   int sequence = 1;
	   int sequence_1 = 1;
	   
	public Security(int threadid, Mainbus mainbus){
			
		
        	mThreadId = threadid;
        	mMainbus = mainbus;
        	
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

	    	
	    	try{
	            inet_addr = InetAddress.getByAddress(ip_bytes);
	             
	         	 AT_socket =  new DatagramSocket(AT_PORT);
	          	 AT_socket.setSoTimeout(3000);
	          	 
	    	} catch(Exception ex1){
	    		ex1.printStackTrace();	
	    		
	    	} 	
	}	
	

	public void run(){
		while(!mMainbus.EmergencyStop()){
		}

		System.out.println("Emergency Shutdown");
		
	try {
		send_at_cmd("AT*REF=" + get_seq() + ",290717952"); 	
		
	} catch (Exception ex1){
	    ex1.printStackTrace();
		System.out.println("You're Fucked");
	}
		
	}

	
	
	
	// -------- SEND AT COMMANDS ----------// 
	public synchronized void send_at_cmd(String AT_CMD) throws Exception {
    	
//	System.out.println("AT command: " + AT_CMD);     	
    	AT_CMD_1 = AT_CMD;
    	byte[] buf_snd = (AT_CMD + "\r").getBytes();
	DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, AT_PORT);
	AT_socket.send(packet_snd); 	
	}
	
	
	public synchronized int get_seq() {
    	return sequence++;
    	}
	
	
	
}
