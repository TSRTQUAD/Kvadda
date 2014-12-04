package kvaddakopter.control_module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import kvaddakopter.assignment_planer.MatFileHandler;


public class DataSaver {
int numberofstates;
ArrayList<double[]> states = new ArrayList<double[]>();
Date currentdate = new Date();
protected MatFileHandler		saver				= new MatFileHandler();



/**
 * Function that saves input States after collection over specified period of time. Data is saved under name States.m
 * @param States
 */
public void saver(double[] inputstates,boolean save){

		this.states.add(inputstates);	
		
	if (save){		
		double[][] statesarray = (double[][]) states.toArray();
		new Thread(new Runnable(){
			@Override
			public void run(){
				try {
					@SuppressWarnings("deprecation")
					String name = (String.valueOf("States_" + currentdate.getYear()) + "_" + String.valueOf(currentdate.getMonth()+1) + "_" + String.valueOf(currentdate.getHours()) + "_" + String.valueOf(currentdate.getMinutes()));
					saver.createMatFileFromFlightData(name, statesarray);
					System.out.println("FlightData has been saved");
					
				} catch (IOException e) {
					System.err.println("Error with creating Matfile from DataSaver");
					e.printStackTrace();
				}
			}			
		}).start();
	}
}
}		


