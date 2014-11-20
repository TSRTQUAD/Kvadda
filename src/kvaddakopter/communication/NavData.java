package kvaddakopter.communication;

import kvaddakopter.Mainbus.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

public class NavData implements Runnable {
	
	   private int mThreadId;
	   private volatile Mainbus mMainbus;
	
	   DatagramSocket socket_nav;
	   InetAddress inet_addr;    
	   NavData navdatatest;
	   
	   Communication communicationtest;
	   
	   float BatteryLevel; 
	   float Altitude;
	   
	   float Pitch, Yaw, Roll;

	   float Vx, Vy, Vz;
	   
	   float GPS;
	   
	   public NavData(int threadid,Mainbus mainbus, String name, Communication communicationtest)  {
		   

		   
	    	this.communicationtest = communicationtest;
	    	this.inet_addr = inet_addr;
	    	
	    	//
	    	
	    	        
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

	        this.inet_addr = inet_addr;
	    	socket_nav = new DatagramSocket(Communication.NAV_PORT);
	    	socket_nav.setSoTimeout(3000);
	    	} catch(Exception ex1) {
	    	    ex1.printStackTrace(); 
	    	}
		     
	    	System.out.println("Init NavData"); 
	     	
	   }
	   

	   
	   public void run(){
	  		   
	//	   int cnt = 0;
		   
			try {
			    byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
			    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, Communication.NAV_PORT);
			    socket_nav.send(packet_snd);
			    
		    	System.out.println("Sent trigger flag to UDP port " + Communication.NAV_PORT);    	

			    communicationtest.send_at_cmd("AT*CONFIG=" + communicationtest.get_seq() + ",\"general:navdata_demo\",\"TRUE\"");
			 
			   // communicationtest.send_at_cmd("AT*CONFIG=" + communicationtest.get_seq() + ",777060865");
			    
			  //   communicationtest.send_at_cmd("AT*CONFIG=" + communicationtest.get_seq() + ",\"enable_navdata_gps\",\"TRUE\"");
			    
		 	    byte[] buf_rcv = new byte[10240];
		 	    
			    DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);            


			    while(true) {
				try {
				    	socket_nav.receive(packet_rcv);
				  //  	System.out.println("NavData Received: " + packet_rcv.getLength() + " bytes");
				  
				    	
				    	
				    	BatteryLevel = communicationtest.get_int(buf_rcv, Communication.NAVDATA_BATTERY);   
				     	//
				    	System.out.println(BatteryLevel);

				    	Altitude = communicationtest.get_int(buf_rcv, Communication.NAVDATA_ALTITUDE);
				       	 System.out.println(Altitude);
				    	
				    	
				    	
				       	Pitch =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_PITCH))/1000;		       			       	
				       	Roll =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_ROLL))/1000;	       	
				       	Yaw =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_YAW))/1000;
				   //    	System.out.println("Pitch;   " + Pitch + "   Roll:    " + Roll + "  Yaw:   "  + Yaw);
				       			
				  //      	System.out.println("---------------------");
				       	Vx =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_VX))/1000;
				       	Vy =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_VY))/1000;
				       	Vz =  Float.intBitsToFloat(communicationtest.get_int(buf_rcv, Communication.NAVDATA_VZ))/1000;
				       	
				       	
				       	
				  //     	System.out.println("Vx:    " + Vx + "  Vy:    " + Vy + "   Vz:    " + Vz);
				       	//
				       	
				  //    	GPS =  communicationtest.get_int(buf_rcv, CommunicationTest.NAVDATA_GPS); 
				  //     	System.out.println(GPS);
				   
				       	
				       	
				  // [LAT LON X Y Z YAW]     	


				       	
				       	
				       	//
				    

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

	  
	// Convert the 32-bit binary into the decimal  
   
	   
	   
	  
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
}

		
		