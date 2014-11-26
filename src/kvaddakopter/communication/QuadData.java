package kvaddakopter.communication;

public class QuadData{
	   private float mBatteryLevel; 
	   private float mAltitude;
	   private float mPitch, mYaw, mRoll;
	   private float mVx, mVy, mVz;
	   private double mGPS_Lat, mGPS_Long;
	   private long mGPS_nSatelites;
	   private float mLinkQuality;
	   
	   /**
	    * Copy constructor, copies member variables of quadData
	    * @param quadData another QUadData object
	    */
	   public QuadData(QuadData quadData) {
		   mBatteryLevel = quadData.getBatteryLevel();
		   mAltitude = quadData.getAltitude();
		   mPitch = quadData.getPitch();
		   mYaw = quadData.getYaw();
		   mRoll = quadData.getRoll();
		   mVx = quadData.getVx();
		   mVy = quadData.getVy();
		   mVz = quadData.getVz();
		   mGPS_Lat = quadData.getGPSLat();
		   mGPS_Long = quadData.getGPSLong();
		   mGPS_nSatelites = quadData.getNGPSSatelites();
		   mLinkQuality = quadData.getLinkQuality();
	   }

	public synchronized float getBatteryLevel(){
		   return mBatteryLevel;
	   }
	   
	   public synchronized float getAltitude(){
		   return mAltitude;
	   }
	   
	   public synchronized  float getPitch(){
		   return mPitch;
	   }
	   
	   public synchronized float getYaw(){
		   return mYaw;
	   }
	   
	   public synchronized float getRoll(){
		   return mRoll;
	   }
	   
	   public synchronized float getVx(){
		   return mVx;
	   }
	   
	   public synchronized float getVy(){
		   return mVy;
	   }
	   
	   public synchronized float getVz(){
		   return mVz;
	   }
	   
	   public synchronized double getGPSLat(){
		   return mGPS_Lat;
	   }
	   
	   public synchronized double getGPSLong(){
		   return mGPS_Long;
	   }
	   
	   public synchronized float getLinkQuality(){
		   return mLinkQuality;
	   }
	   
	   public synchronized long getNGPSSatelites(){
		   return mGPS_nSatelites;
	   }
	   
	   //set functions
	   
	   public synchronized void setBatteryLevel(float batteryLevel){
		   mBatteryLevel = batteryLevel;
	   }
	   
	   public synchronized void setAltitude(float altitude){
		   mAltitude = altitude;
	   }
	   
	   public synchronized void setPitch(float pitch){
		   mPitch = pitch;
	   }
	   
	   public synchronized void setYaw(float yaw){
		   mYaw = yaw;;
	   }
	   
	   public synchronized void setRoll(float roll){
		   mRoll = roll;
	   }
	   
	   public synchronized void setVx(float vx){
		   mVx = vx;
	   }
	   
	   public synchronized void setVy(float vy){
		   mVy = vy;
	   }
	   
	   public synchronized void setVz(float vz){
		   mVz = vz;
	   }
	   
	   public void setGPSLat(double gpsLat){
		   mGPS_Lat = gpsLat;
	   }
	   
	   public void setGPSLong(double gpsLong){
		   mGPS_Long = gpsLong;
	   }
	   
	   public void setLinkQuality(float linkQuality){
		   mLinkQuality = linkQuality;
	   }
	   
	   public void setNGPSSatelites(long nSatelites){
		   mGPS_nSatelites = nSatelites;
	   }
}