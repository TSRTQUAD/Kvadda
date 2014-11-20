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
		
		if (0 == this.counter){
			System.out.format("Reference first update no. %d%n",this.counter);
			this.start = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[this.counter][0],
												missionobject.getTrajectory()[counter][1],
												missionobject.getHeight()[counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[counter][0]
															 ,0,start,land};
			
			
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
}
