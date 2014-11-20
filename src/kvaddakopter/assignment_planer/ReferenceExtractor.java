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
	public double[] update(MissionObject missionobject){
		
		System.out.println(this.counter);
		
		
		if (0 == this.counter ){
			//System.out.format("Reference first update no. %d%n",this.counter);
			this.start = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[this.counter][0],
												missionobject.getTrajectory()[this.counter][1],
												missionobject.getHeight()[this.counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[this.counter][0]
															 ,0,this.start,this.land};
			
			
			this.counter = this.counter + 1;
			this.start = 0;

			return returnvalue;
		}
		else if (this.counter == missionobject.getHeight().length){
			this.land = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[counter][0],
												missionobject.getTrajectory()[counter][1],
												missionobject.getHeight()[this.counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[this.counter][0]
															 ,0,start,land};
			land = 0;
			return returnvalue;
		}
		else if (this.counter > missionobject.getHeight().length){
			// Close thread TODO
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][0],
					missionobject.getTrajectory()[1][1],
					missionobject.getHeight()[1],
					missionobject.getYaw(),
					missionobject.getWaitingtime(),
					missionobject.getReferenceVelocity()[1][0]
								 ,0,start,land};
			return returnvalue;
		}
		
		
		else {		
			System.out.format("Reference update no. %d%n",this.counter);
			double[] returnvalue = new double[]{missionobject.getTrajectory()[this.counter][0],
												missionobject.getTrajectory()[this.counter][1],
												missionobject.getHeight()[this.counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[this.counter][0]
															 ,1,start,land};
			
			this.counter = this.counter + 1;			
			return returnvalue;	
		}
	}	
	
	
	
public double[] updatetest(MissionObject missionobject){			
			//System.out.format("Reference first update no. %d%n",this.counter);
			double[] returnvalue = new double[]{missionobject.getTrajectory()[this.counter][0],			// GPS lat
												missionobject.getTrajectory()[this.counter][1],			// GPS long
												2,														// Height
												0,														// Yaw
												10,														// Time at coordinate
												0,														// null
												0,														// Mission type
												this.start,												// Set start/land
												this.land};												// Set start/land
			return returnvalue;
		}	
}

