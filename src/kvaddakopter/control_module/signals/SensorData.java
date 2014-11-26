package kvaddakopter.control_module.signals;

import kvaddakopter.communication.QuadData;

public class SensorData {
	private double Longitud;
	private double Latitud;
	private double X;
	private double Y;
	private double Xdot;
	private double Ydot;
	private double xdot;
	private double ydot;
	private double height;
	private double yaw;
	private double initiallon;
	private double initiallat;
	private double radius = 6371;
	
	private double Longitudold;
	private double Latitudold;
	
		/**
		 * Set sensordata object from sensordata in vector format
		 * {Latitud,Longitud,xdot,ydot,height,yaw}
		 * @param sensorvector
		 */
	public void setnewsensordata(QuadData quadData){
		synchronized(quadData){
			this.Latitud = quadData.getGPSLat();
			this.Longitud = quadData.getGPSLong();
			this.xdot = quadData.getVx();
			this.ydot = quadData.getVy();
			this.height = quadData.getAltitude();
			this.yaw = -Math.PI/180*quadData.getYaw(); //Is in degrees
		}

	}

	
	/**
	 * Fixes the local coordinate system XY to intitiallat, initiallon
	 */
	public void setinitial(){
		this.initiallon = Longitud;
		this.initiallat  = Latitud;
		this.Longitudold = Longitud;
		this.Latitudold = Latitud;
	}

	
	/**
	 *  Transforms GPS coordinates Latitude and Longitude into local 
	   coordinate system XY which has origo at (initiallat,initiallon)
	   and Y axis pointing north. 
	   */ 
	public void GPS2XY(){

		double lat1=initiallat*Math.PI/180;
		double lon1=initiallon*Math.PI/180;
		
		double lat2=Latitud*Math.PI/180;
		double lon2=Longitud*Math.PI/180;

		double deltaLat=lat2-lat1;
		double deltaLon=lon2-lon1;

		this.X=radius*deltaLon*Math.cos((lat1+lat2)/2)*1000;
		this.Y=radius*deltaLat*1000;
	}
	
	
	
	/** Transforms local coordinate system XY into coordinate system 
	 * xy on platform. y pointing forward (Pitch axis) and x pointing sideways (Roll axis)
	 * @param 
	 */
	/*
	public double[]  XY2xy(double A, double B){
		double a = Math.cos(yaw)*A + Math.sin(yaw)*B;
		double b = -Math.sin(yaw)*A + Math.cos(yaw)*B;
		double[] array = new double[]{a,b};
		return array;
	}
	*/
	
	/*
	// Transforms xy to local coordinate system XY
	public double[] xy2XY(double a,double b){
		double A =  Math.cos(yaw)*a - Math.sin(yaw)*b;
		double B =  Math.sin(yaw)*a + Math.cos(yaw)*b;
		double[] array = new double[]{A,B};
		return array;
	}
	 */
	
	
	/**
	 * Transforms quadcopters local velocities (xdot,ydot) to fixed coordinate system XY velocities
	 */
	public void xydot2XYdot(){
		Xdot =  Math.cos(yaw)*xdot - Math.sin(yaw)*ydot;
		Ydot =  Math.sin(yaw)*xdot + Math.cos(yaw)*ydot;
	}
	
	/**
	 * Transforms fixed coordinate system XY velocities to local velocities (xdot,ydot)
	 */
	public void XYdot2xydot(){
		xdot =    Math.cos(yaw)*Xdot + Math.sin(yaw)*Ydot;
		ydot =  - Math.sin(yaw)*Xdot + Math.cos(yaw)*Ydot;
	}

	
	
	
	/**
	 *  sets GPS-position from array {Latitud,longitud}
	 * @param GPS
	 */
	public void setGPSposition(double[] GPS) {
		this.Latitud = GPS[0];
		this.Longitud = GPS[1];
	}
	
	/**
	 * Calculates if there is a new GPS measurement
	 * @return true/false
	 */
	public boolean isGPSnew() {
		if(	this.Longitud == this.Longitudold && this.Latitud == this.Latitudold)		 
		return false;
		else{
			this.Longitudold = this.Longitud;
			this.Latitudold = this.Latitud;
			return true;
		}
	}
	
	/**
	 * Prints all sensordata
	 */
	public void print(){	
		System.out.format("Latitud: %.8f , Longitud %.8f%n", Latitud, Longitud);
		System.out.format("X: %.4f , Y %.4f%n", X, Y);
		System.out.format("Xdot: %.4f , Ydot %.4f%n", Xdot, Ydot);
		System.out.format("xdot: %.4f , ydot %.4f%n", xdot, ydot);
		System.out.format("Height: %.4f%n", height);
		System.out.format("Yaw: %.4f%n  %n %n",180/Math.PI*yaw);
	}
	
		/**
		 * Print at which GPS positions coordinate system XY is fixed at.
		 */
	public void printinitials(){
	System.out.format("Init.Latitud: %.8f , Init.Longitud %.8f%n", initiallat, initiallon);
	}
	
	
	public double getLongitud() {
		return Longitud;
	}


	public void setLongitud(double longitud) {
		Longitud = longitud;
	}




	public double getLatitud() {
		return Latitud;
	}




	public void setLatitud(double latitud) {
		Latitud = latitud;
	}




	public double getX() {
		return X;
	}




	public void setX(double x) {
		X = x;
	}




	public double getY() {
		return Y;
	}




	public void setY(double y) {
		Y = y;
	}




	public double getXdot() {
		return Xdot;
	}




	public void setXdot(double xdot) {
		Xdot = xdot;
	}




	public double getYdot() {
		return Ydot;
	}




	public void setYdot(double ydot) {
		Ydot = ydot;
	}




	public double getxdot() {
		return xdot;
	}




	public void setxdot(double xdot) {
		this.xdot = xdot;
	}




	public double getydot() {
		return ydot;
	}




	public void setydot(double ydot) {
		this.ydot = ydot;
	}




	public double getHeight() {
		return height;
	}




	public void setHeight(double height) {
		this.height = height;
	}




	public double getYaw() {
		return yaw;
	}




	public void setYaw(double yaw) {
		this.yaw = yaw;
	}




	public double getInitiallon() {
		return initiallon;
	}




	public double getInitiallat() {
		return initiallat;
	}



	

	
}
