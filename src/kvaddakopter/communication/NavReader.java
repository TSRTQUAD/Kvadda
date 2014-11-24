package kvaddakopter.communication;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * NavReader class implements functionality to read ordered data from a byte array
 * via a ByteBuffer.
 * 
 * Read functions: byte8, uint16, uint32, int16, int32, float32, double64, getSubArray
 * Utility: getRemainingSize
 */
public class NavReader {
	
	public static final int DEMO 				= 0;
	public static final int TIME 				= 1;
	public static final int RAW_MEASURES 		= 2;
	public static final int PHYS_MEASURES 		= 3;
	public static final int GYROS_OFFSET	 	= 4;
	public static final int EULER_ANGLES	 	= 5;
	public static final int REFERENCES	 		= 6;
	public static final int TRIMS			 	= 7;
	public static final int RC_REFERENCES	 	= 8;
	public static final int PWM			 		= 9;
	public static final int ALTITUDE	 		= 10;
	public static final int VISION_RAW	 		= 11;
	public static final int VISION_OF	 		= 12;
	public static final int VISION	 			= 13;
	public static final int VISION_PERF 		= 14;
	public static final int TRACKERS_SEND	 	= 15;
	public static final int VISION_DETECT	 	= 16;
	public static final int WATCHDOG			= 17;
	public static final int ADC_DATA_FRAME		= 18;
	public static final int VIDEO_STREAM		= 19;
	public static final int GAMES				= 20;
	public static final int PRESSURE_RAW 		= 21;
	public static final int MAGNETO	 			= 22;
	public static final int WIND_SPEED 			= 23;
	public static final int KALMAN_PRESSURE		= 24;
	public static final int HD_VIDEO_STREAM		= 25;
	public static final int WIFI		 		= 26;
	public static final int GPS 				= 27;
	public static final int CHECKSUM	 		= 65535;
	
	//Buffer containing the data array
	private ByteBuffer mBuf;
	
	/**
	 * Constrtuctor of empty NavReader
	 */
	public NavReader(){
		mBuf = ByteBuffer.allocate(10240);
	}
	
	/**
	 * Construct a NavReader with data from array
	 * @param byte array in LITTLE_ENDIAN order
	 */
	public NavReader(byte[] array){
		setData(array);
	}
	
	/**
	 * Set data and flip buffer to enable reading
	 * @param dataArray byte array in LITTLE_ENDIAN order
	 */
	public void setData(byte[] dataArray){
		mBuf = ByteBuffer.allocate(dataArray.length);
		mBuf.put(dataArray);
		mBuf.flip();
		mBuf.order(ByteOrder.LITTLE_ENDIAN);

	}
	
	/**
	 * Read next 8 bit byte from buffer
	 * @return
	 */
	public float byte8(){
		return mBuf.get();
	}
	
	/**
	 * Read next unsigned 16 bit integer from buffer
	 * @return
	 */
	public int uint16(){
		byte b0 = mBuf.get();
		byte b1 = mBuf.get();
		return (int) (( (b1 & 0xFF) << 8) | (b0 & 0xFF));
		
	}
	
	/**
	 * Read next unsigned 32 bit integer from buffer
	 * @return
	 */
	public long uint32(){
		byte b0 = mBuf.get();
		byte b1 = mBuf.get();
		byte b2 = mBuf.get();
		byte b3 = mBuf.get();
		return (b3 & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b1 & 0xFF) << 8 | (b0 & 0xFF);
	}
	
	/**
	 * Read next 16 bit integer from buffer
	 * @return
	 */
	public int int16(){
		return mBuf.getShort();
	}
	
	/**
	 * Read next 32 bit integer from buffer
	 * @return
	 */
	public int int32(){
		return mBuf.getInt();
	}
	
	/**
	 * Read next 32 bit float from buffer
	 * @return
	 */
	public float float32(){
		return mBuf.getFloat();
	}
	
	/**
	 * Read next 64 bit double from buffer
	 * @return
	 */
	public double double64(){
		return mBuf.getDouble();
	}
	
	//GET functions with offset
	
	/**
	 * Read 8 bit byte at specified location in buffer
	 * @param offset
	 * @return
	 */
	public float byte8(int offset){
		return mBuf.get(offset);
	}
	
	/**
	 * Read 16 bit unsigned int at sepcified location in buffer
	 * @param offset
	 * @return
	 */
	public int uint16(int offset){
		byte b0 = mBuf.get(offset);
		byte b1 = mBuf.get(offset+1);
		return (int) (( (b1 & 0xFF) << 8) | (b0 & 0xFF));
		
	}
	
	/**
	 * Read 32 bit unsigned int at specified location in buffer
	 * @param offset
	 * @return
	 */
	public long uint32(int offset){
		byte b0 = mBuf.get(offset);
		byte b1 = mBuf.get(offset+1);
		byte b2 = mBuf.get(offset+2);
		byte b3 = mBuf.get(offset+3);
		return (b3 & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b1 & 0xFF) << 8 | (b0 & 0xFF);
	}
	
	/**
	 * Read 16 bit integer at specified location in buffer
	 * @param offset
	 * @return
	 */
	public int int16(int offset){
		return mBuf.getShort(offset);
	}
	
	
	/**
	 * Read 32 bit integer at specified location in buffer
	 * @param offset
	 * @return
	 */
	public int int32(int offset){
		return mBuf.getInt(offset);
	}
	
	/**
	 * Read 32 bit float at specified location in buffer
	 * @param offset
	 * @return
	 */
	public float float32(int offset){
		return mBuf.getFloat(offset);
	}
	
	/**
	 * Read 64 bit double at specified location in buffer
	 * @param offset
	 * @return
	 */
	public double double64(int offset){
		return mBuf.getDouble(offset);
	}
	
	/**
	 * Read sub array of specific size from buffer
	 * @param contentLength
	 * @return
	 */
	public byte[] getSubArray(int contentLength){
		byte[] b = new byte[contentLength];
		mBuf.get(b, 0, contentLength);
		return b;
	}
	
	/**
	 * Get remaining bytes from current position in buffer
	 * @return
	 */
	public int getRemainingSize(){
		return mBuf.remaining();
	}
}


