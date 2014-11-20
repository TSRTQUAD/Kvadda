package kvaddakopter.control_module;
import java.util.ArrayList;

import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.control_module.signals.ControlSignal;
import kvaddakopter.interfaces.ControlMainBusInterface;


public class Mockmainbus implements ControlMainBusInterface{

	protected double[] sensorvector			= new double[]{0,0,0,0,0};
	protected ControlSignal controlsignal	= new ControlSignal();
	protected MissionObject missionobject	= new MissionObject();
	protected ArrayList<double[]> sensorarray = new ArrayList<>();
	
	protected int counter = -1;
	protected int inc = 1;
	protected int	i = 1;
	protected int	j = 1;
	
	
	
	public Mockmainbus(){
		//sensorarray.add(new double[]{Latitud,Longitud,xdot,ydot,height,yaw});
		
			sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,- Math.PI});
			sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,- Math.PI});
			sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,- Math.PI});
			sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,- Math.PI});
			sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,- Math.PI});
		
		while(i < 21){
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,0.1*i	,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,0.1*i	,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,0.1*i	,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659  ,15.620358	,0	,0.1*i	,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,0.1*i	,0	,- Math.PI});
			i ++;
		}
		

		
		i = 1;
		while(i < 1000){
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,2		,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,2		,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,2		,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659  ,15.620358	,0	,2		,0	,- Math.PI});
			sensorarray.add(new double[]{58.406659	,15.620358	,0	,2		,0	,- Math.PI});
			i ++;
		}


		
		missionobject.setTrajectory(new double[][]{ {58.406659,15.620358},
													{58.406659,15.620358},
													{58.406659,15.620358},
													{58.406659,15.620358},
													{58.406659,15.620358}});
		
		missionobject.setYaw(-Math.PI);
		missionobject.setHeight(new double[]{0,0,0,0,0});
		missionobject.setReferenceVelocity(new double[][]{ 	{0,0},
															{2,0},
															{2,0},
															{2,0},
															{2,0}});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		//sensorarray.add(new double[]{Latitud,Longitud,xdot,ydot,height,yaw});
		
		sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,0});
		sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,0});
		sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,0});
		sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,0});
		sensorarray.add(new double[]{58.406659,15.620358,0		,0,0,0});
		
		while(i < 6){
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,1.1		,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,1.05	,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,1.05	,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,1.1		,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358,1		,0,0,0});
		i ++;
		}
		
		while(i < 11){
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,0.9		,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,0.95	,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,1		,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*(i-1),15.620358,0.8		,0,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358,1		,0,0,0});
		i ++;
		}
		
		while(j < 6){
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,1,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,1,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,1,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,1,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*j	 ,0		,1,0,0});
		j++;
		}
		
		while(j < 11){
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,2,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,2,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,2,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*(j-1),0		,2,0,0});
		sensorarray.add(new double[]{58.406659+0.000009*i	,15.620358+0.000017*j	 ,0		,2,0,0});
		j++;
		}		
				
		missionobject.setTrajectory(new double[][]{ {58.406659,15.620358},
													{58.406704,15.620358},
													{58.406749,15.620358},
													{58.406749,15.620443},
													{58.406749,15.620528}});
		
		missionobject.setYaw(0);
		missionobject.setHeight(new double[]{2,2,2,2,2});
		missionobject.setReferenceVelocity(new double[][]{ 	{2,0},
															{2,0},
															{2,0},
															{2,0},
															{2,0}}); */

	}
	@Override
	public double[] getSensorVector() {
		this.counter += this.inc;
		if(this.counter== 0) this.inc = 1;
        if(this.counter == sensorarray.size() -1){this.inc = -1;}
		return sensorarray.get(this.counter);
	}

	@Override
	public ControlSignal getControlSignal() {
			
		return controlsignal;
	}

	@Override
	public MissionObject getMissionObject() {
		
		return missionobject;
	}

	@Override
	public void setControlSignal(ControlSignal csignal) {
		this.controlsignal = csignal;
	}

	@Override
	public MissionObject setMissionObject() {
		return null;
	}

}
