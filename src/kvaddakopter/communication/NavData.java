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
	   
	   long BatteryLevel; 
	   float Altitude;
	   
	   float Pitch, Yaw, Roll;

	   float Vx, Vy, Vz;
	   
	   Double GPS_Lat, GPS_Long;
	   long GPS_nSatelites;
	   
	   float LinkQuality;
	   
	   double[] NavData = new double[6];
	   public NavData(int threadid,Mainbus mainbus, String name, Communication communicationtest)  {
		   	mThreadId = threadid;
	        mMainbus = mainbus;

		   
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
	   

	   public void run() {	
			try {
			    byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
			    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, Communication.NAV_PORT);
			    
			    socket_nav.send(packet_snd);
		    	    System.out.println("Sent trigger flag to UDP port " + Communication.NAV_PORT);    	
		
		 	    byte[] buf_rcv = new byte[10240];
			    DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);            
			    
			    while(true){
			    	try{
			    		socket_nav.receive(packet_rcv);
			    	}
			    	catch(SocketTimeoutException ex3) {
			    	    System.out.println("socket_nav.receive(): Timeout");
			    	    break;
			    	} catch(Exception ex1) { 
			    		ex1.printStackTrace(); 
			    	}
			    	
					NavReader reader = new NavReader(packet_rcv.getData());
					
					//Get header information
					long header 		= reader.uint32();
					long droneState		= reader.uint32(); //Should be some sort of mask to see what state the drone is in
			    	long sequenceNumber	= reader.uint32();
			    	long visionFlag     = reader.uint32();
			    	 
			    	//Run until checksum
			    	boolean finnished = false;
			    	while(!finnished){
			    		int optionId   = reader.uint16();
			    	    int length     = reader.uint16();
			    	    
			    	    //System.out.println("OptionId: " + optionId);
			    	    //System.out.println("Length: " + length);
			    	    
			    	    byte[] content;
				    	// length includes header (4 bytes)
			    		content = reader.getSubArray(length-4);
			    		NavReader contentReader = new NavReader(content);
			    	    
			    		switch(optionId){
			    		case NavReader.DEMO:
			    			System.out.println("GOT DEMO MESSAGE!");
			    			int flyState 			= contentReader.uint16();
			    			int controlState 		= contentReader.uint16();
			    			BatteryLevel 			= contentReader.uint32();
			    			Pitch					= contentReader.float32()/1000;
			    			Yaw 					= contentReader.float32()/1000;
			    			Roll	 				= contentReader.float32()/1000;
			    			Altitude	 			= contentReader.int32();
			    			Vx 						= contentReader.float32();
			    			Vy 						= contentReader.float32();
			    			Vz						= contentReader.float32();
			    			long frameIndex			= contentReader.uint32();
			    	    	break;
			    	    	
			    		case NavReader.WIFI:
			    			System.out.println("GOT WIFI MESSAGE!");
			    			LinkQuality = 1-contentReader.float32();
			    			break;
			    	    	
			    		case NavReader.GPS:
			    			System.out.println("GOT GPS MESSAGE!");
			    	    	GPS_Lat			 = contentReader.double64();		    	    	
			    	    	GPS_Long		 = contentReader.double64();
			    	    	GPS_nSatelites	 = contentReader.uint32(164);
			    	    	break;
			    	    //Checksum used to determine if message received properly
			    		case NavReader.CHECKSUM:
			    			
			    			long expectedChecksum = 0;
			    		    for (int i = 0; i < buf_rcv.length - length; i++) {
			    		      expectedChecksum += buf_rcv[i];
			    		    }

			    		    long checksum = contentReader.uint32();

			    		    if (checksum != expectedChecksum) {
			    		      System.err.println("Checksum fail, expected: " + expectedChecksum + ", got: " + checksum);
			    		    }

			    		    // checksum is the last option
			    			finnished = true;
			    			//TODO sleep here and wait a bit (refreshtime)
			    			break;
			    		}
			    	}
		    	}
			} catch(Exception ex2) {
			    ex2.printStackTrace(); 
			}
		}
}

		
		