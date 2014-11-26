package kvaddakopter.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import kvaddakopter.interfaces.MainBusCommInterface;

/**
 * NavData class handles the receiving of data sent from the quad over UDP
 *
 */
public class NavData implements Runnable {

	private volatile MainBusCommInterface mMainbus;

	private DatagramSocket socket_nav;
	private InetAddress inet_addr;

	private Communication comm;
	
	private boolean mIsInitiated = false;

	// Container class for sensor data
	QuadData mQuadData = new QuadData();

	public NavData(
			int threadid,
			MainBusCommInterface mainbus,
			String name,
			Communication comm){
		
		mMainbus = mainbus;
		this.comm = comm;
	}
	
	/**
	 * Initializes the UDP socket
	 */
	public void init(){
		try {
			this.inet_addr = comm.getInetAddr();
			socket_nav = new DatagramSocket(Communication.NAV_PORT);
			socket_nav.setSoTimeout(3000);
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}

		System.out.println("Init NavData");
	}
	
	/**
	 * Checks if the communication unit is started
	 */
	public void checkIsCommRunning(){
		while(!comm.isRunning() || !comm.isInitiated()){
			synchronized(comm){
				try {
					comm.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}
		if(!mIsInitiated){
			mIsInitiated = true;
			init();
		}
	}
	
	/**
	 * Sends what information to get with AT.CONFIG messages
	 * and receives messages with {@link socket_nav.receive(packet_rcv)}
	 * 
	 * Received data is read in order with {@link NavReader class}
	 * Processes different header opts and data types
	 */
	public void run() {
		checkIsCommRunning();
		try {
			byte[] buf_snd = { 0x01, 0x00, 0x00, 0x00 };
			DatagramPacket packet_snd = new DatagramPacket(buf_snd,
					buf_snd.length, inet_addr, Communication.NAV_PORT);
			socket_nav.send(packet_snd);

			System.out.println("Sent trigger flag to UDP port "
					+ Communication.NAV_PORT);

			comm.send_at_cmd("AT*CONFIG="
					+ comm.get_seq()
					+ ",\"general:navdata_demo\",\"FALSE\"");
			comm.send_at_cmd("AT*CONFIG="
					+ comm.get_seq() + ",777060865");
			
			byte[] buf_rcv = new byte[10240];
			DatagramPacket packet_rcv = new DatagramPacket(buf_rcv,
					buf_rcv.length);

			while (true) {
				try {
					socket_nav.receive(packet_rcv);

					NavReader reader = new NavReader(packet_rcv.getData());

					// Get header information
					long header = reader.uint32();
					boolean[] droneStates = reader.droneStates32();
					long sequenceNumber = reader.uint32();
					long visionFlag = reader.uint32();
					
					if(droneStates[NavReader.COMM_WATCHDOG_PROBLEM])
						System.err.println("COMM WATCHDOG PROBLEM");
					if(droneStates[NavReader.COMM_LOST])
						System.err.println("COMM LOST");
					if(droneStates[NavReader.FLYING])
						comm.setIsFlying(true);

					// Run until checksum
					boolean finnished = false;
					while (!finnished) {
						int optionId = reader.uint16();
						int length = reader.uint16();

						// System.out.println("OptionId: " + optionId);

						byte[] content;
						// length includes 4 byte header
						content = reader.getSubArray(length - 4);
						NavReader contentReader = new NavReader(content);

						switch (optionId) {
						case NavReader.DEMO:
							int flyState = contentReader.uint16();
							int controlState = contentReader.uint16();

							mQuadData.setBatteryLevel(contentReader.uint32());
							mQuadData.setPitch(contentReader.float32() / 1000);
							mQuadData.setRoll(contentReader.float32() / 1000);
							mQuadData.setYaw(contentReader.float32() / 1000);
							mQuadData.setAltitude((float)(contentReader.int32()) / 1000);
							mQuadData.setVx(contentReader.float32() / 1000);
							mQuadData.setVy(contentReader.float32() / 1000);
							mQuadData.setVz(contentReader.float32());

							long frameIndex = contentReader.uint32();
							break;

						case NavReader.WIFI:
							mQuadData.setLinkQuality(1 - contentReader
									.float32());
							break;

						case NavReader.GPS:
							mQuadData.setGPSLat(contentReader.double64());
							mQuadData.setGPSLong(contentReader.double64());
							mQuadData.setNGPSSatelites(contentReader
									.uint32(164));
							break;
						// Checksum used to determine if message received
						// properly
						case NavReader.CHECKSUM:

							long expectedChecksum = 0;
							for (int i = 0; i < buf_rcv.length - length; i++) {
								expectedChecksum += buf_rcv[i];
							}

							long checksum = contentReader.uint32();

							if (checksum != expectedChecksum) {

								// System.err.println("Checksum fail, expected: "
								// + expectedChecksum + ", got: " + checksum);

							}

							// checksum is the last option
							finnished = true;
							// TODO sleep here and wait a bit (refreshtime)
							break;
						}
					}

					mMainbus.setQuadData(mQuadData);
					
					checkStartConditions();

					// System.out.println("Vx:    " + Vx + "  Vy:    " + Vy +
					// "   GPS_Long     :" + GPS_Long);

				} catch (SocketTimeoutException ex3) {
					System.out.println("socket_nav.receive(): Timeout");
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
			}
		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
	}
	
	public void checkStartConditions(){
		boolean wifi = false,gps=false;
		if(mQuadData.getNGPSSatelites() > 2){
			mMainbus.setGpsFixOk(true);
			wifi = true;
		}
		if(mQuadData.getLinkQuality() > 0.8){
			mMainbus.setWifiFixOk(true);
			gps=true;
		}
		
		if(wifi && gps)
			mMainbus.setIsStarted(true);
			
	}
}


//old stuff

// System.out.println("NavData Received: " +
// packet_rcv.getLength() + " bytes");
/*
 * 
 * 
 * BatteryLevel = comm.get_int(buf_rcv,
 * Communication.NAVDATA_BATTERY); //
 * System.out.println(BatteryLevel);
 * 
 * Altitude = comm.get_int(buf_rcv,
 * Communication.NAVDATA_ALTITUDE);
 * System.out.println(Altitude);
 * 
 * 
 * 
 * Pitch =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_PITCH))/1000; Roll =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_ROLL))/1000; Yaw =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_YAW))/1000; //
 * System.out.println("Pitch;   " + Pitch + "   Roll:    " +
 * Roll + "  Yaw:   " + Yaw);
 * 
 * // System.out.println("---------------------"); Vx =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_VX))/1000; Vy =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_VY))/1000; Vz =
 * Float.intBitsToFloat(comm.get_int(buf_rcv,
 * Communication.NAVDATA_VZ))/1000;
 */