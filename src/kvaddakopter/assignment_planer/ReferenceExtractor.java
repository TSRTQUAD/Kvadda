package kvaddakopter.assignment_planer;

public class ReferenceExtractor {

	protected int counter = 0;
	protected int start = 0;
	protected int land = 0;

	// Returndata  = (latitude, longitud, height, yaw, time, forward velocity, mission type, start,land)
	public double[] update(MissionObject missionobject){
		if (counter == 0){
			start = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][counter],
												missionobject.getTrajectory()[1][counter],
												missionobject.getHeight()[counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[counter][0]
															 ,0,start,land};
			
			
			this.counter ++;
			start = 0;
			return returnvalue;
		}
		if (counter == missionobject.getHeight().length){
			land = 1;
			double[] returnvalue = new double[]{missionobject.getTrajectory()[0][counter],
												missionobject.getTrajectory()[1][counter],
												missionobject.getHeight()[counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[counter][0]
															 ,0,start,land};
			land = 0;
			return returnvalue;
		}
			System.out.format("Reference update no. %d%n",counter);
			double[] returnvalue = new double[]{missionobject.getTrajectory()[counter][0],
												missionobject.getTrajectory()[counter][1],
												missionobject.getHeight()[counter],
												missionobject.getYaw(),
												missionobject.getWaitingtime(),
												missionobject.getReferenceVelocity()[counter][0]
															 ,1,start,land};
			
			this.counter ++;			
			return returnvalue;			
		}	
}
