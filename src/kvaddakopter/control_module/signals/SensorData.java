package kvaddakopter.control_module.signals;

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
	
	
	
	// Fixes the local coordinate system XY to intitiallat, initiallon
	public void setinitial(){
		this.initiallon = Longitud;
		this.initiallat  = Latitud;	
	}

	
	
	
	/* Transforms GPS coordinates Latitude and Longitude into local 
	 *  coordinate system XY which has origo at (initiallat,initialat)
	   and Y axis pointing north. */ 
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
	
	
	/* Transforms local coordinate system XY into coordinate system 
	 * xy on platform. y pointing forward (Pitch axis) and x pointing sideways (Roll axis)
	 */
	public double[]  XY2xy(double A, double B){
		double a = Math.cos(yaw)*A + Math.sin(yaw)*B;
		double b = -Math.sin(yaw)*A + Math.sin(yaw)*B;
		double[] array = new double[]{a,b};
		return array;
	}
	
	
	// Transforms xy to local coordinate system XY
	public double[] xy2XY(double a,double b){
		double A =  Math.cos(yaw)*a - Math.sin(yaw)*b;
		double B =  Math.sin(yaw)*a + Math.sin(yaw)*b;
		double[] array = new double[]{A,B};
		return array;
	}
	

	
	
}
