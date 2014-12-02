package kvaddakopter.assignment_planer;

public class ReferenceExtractor {

	protected int counter;
	protected int start;
	protected int land;
	
	public ReferenceExtractor(int startcount){
		this.counter = startcount;
		this.start = 0;
		this.land = 0;		
	}

	
	// Returndata  = (latitude, longitud, height, yaw, time, forward velocity, mission type, start,land)
	
/**
 * Reads missionobject data and extracts relevant reference information. 
 * First update: Wait for 3 seconds @ coordinate
 * Last update: Landing is set to 1.
 * Returndata  = (latitude, longitud, height, Yaw, time, forward velocity, mission type, start,land) 	
 * @param missionobject
 * @return Referencevetor 
 */
	public double[] update(MissionObject missionobject){		
		if (0 == this.counter ){
			//System.out.format("Reference first update no. %d%n",this.counter);
			this.start = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][0],
												missionobject.getTrajectory()[0][1],
												missionobject.getHeight(),
												missionobject.getYaw(),
												3000,
												missionobject.getReferenceVelocity()[this.counter][0]
															 ,0,this.start,this.land};
			
			
			this.counter = this.counter + 1;
			this.start = 0;

			return returnvalue;
		}
		else if (this.counter == missionobject.getTrajectory().length){
			this.land = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][0],
												missionobject.getTrajectory()[0][1],
												missionobject.getHeight(),
												missionobject.getYaw(),
												5000,
												missionobject.getReferenceVelocity()[0][0]
															 ,0,start,land};
			land = 0;
			return returnvalue;
		}
		else if (this.counter > missionobject.getTrajectory().length){
			// Close thread TODO
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][0],
					missionobject.getTrajectory()[1][1],
					missionobject.getHeight(),
					missionobject.getYaw(),
					missionobject.getWaitingtime(),
					missionobject.getReferenceVelocity()[1][0]
								 ,0,start,land};
			return returnvalue;
		}
		
		
		else {		
			//System.out.format("Reference update no. %d%n",this.counter);
			double[] returnvalue = new double[]{missionobject.getTrajectory()[this.counter][0],
												missionobject.getTrajectory()[this.counter][1],
												missionobject.getHeight(),
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[this.counter][0]
															 ,0,start,land};
			
			this.counter = this.counter + 1;			
			return returnvalue;	
		}
	}	
	
	

public double[] updatetest(){			
			//System.out.format("Reference first update no. %d%n",this.counter);
	this.counter = this.counter + 1;
	 double angle = ((this.counter % 2) == 0) ? Math.PI : 0;
	double[] returnvalue = new double[]{		0,														// GPS lat
												0 + 2*Math.pow(-1, counter),			 				// GPS long
												1,														// Height
												0,														// Yaw
												1000,													// Time at coordinate
												0,														// null
												0,														// Mission type
												this.start,												// Set start/land
												this.land};												// Set start/land
			return returnvalue;
		}


public int getCounter() {
	// TODO Auto-generated method stub
	return this.counter;
}


}


