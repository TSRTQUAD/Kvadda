package kvaddakopter.control_module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import kvaddakopter.assignment_planer.MatFileHandler;

/**
 * Collects data and saves as matfile under name: name_date.m .
 *
 *
 */
public class DataSaver {
int numberofstates;
ArrayList<double[]> states = new ArrayList<double[]>();

String name;
protected MatFileHandler		saver				= new MatFileHandler();

public DataSaver(int numberofstates, String name){
	this.numberofstates = numberofstates;
	this.name = name;
}

/**
 * Function that saves input States after collection over specified period of time. Data is saved under name States.m
 * @param States
 */
public void adddata(double[] inputstates){
		this.states.add(inputstates);
		
}
/**
 * Saves collected data under the name specified in constructor.
 */
public void savedata(){		
	Date currentdate = new Date();
	double[][] statesarray = new double[states.size()][numberofstates];
		int counter = 0;
		for (double[] row:states){			
			statesarray[counter] = row;
			counter ++;
		}
		new Thread(new Runnable(){
			@Override
			public void run(){
				try {
					@SuppressWarnings("deprecation")
					String finalname = (				(name + "_" + 
														String.valueOf(currentdate.getYear()-100) + "_" + 
														String.valueOf(currentdate.getMonth()+1) + "_" + 
														String.valueOf(currentdate.getDate()) + "_" + 
														String.valueOf(currentdate.getHours()) + "_" + 
														String.valueOf(currentdate.getMinutes())) + "_"	+
														String.valueOf(currentdate.getSeconds()));
					saver.createMatFileFromFlightData(finalname, statesarray);
					System.out.println("FlightData has been saved");
				} catch (IOException e) {
					System.err.println("Error with creating Matfile from DataSaver");
					e.printStackTrace();
				}
			}			
		}).start();

}
}		


