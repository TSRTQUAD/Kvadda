package kvaddakopter.control_module;

import java.io.IOException;

import kvaddakopter.assignment_planer.MatFileHandler;


public class DataSaver {
double sampletime, periodoftime;
int counter = 0;
int numberofstates;
double[][] states;
protected MatFileHandler		saver				= new MatFileHandler();

/**
 * Initialize a DataSaverobject that save a "number of states" under a "period of time". Sampletime of loop should be specified.
 * @param sampletime
 * @param periodoftime
 * @param numberofstates
 */
public DataSaver(double sampletime,int periodoftime,int numberofstates){
	this.sampletime = sampletime;
	this.periodoftime = periodoftime;
	this.states = new double[(int)(periodoftime*sampletime)][numberofstates];
}


/**
 * Function that saves input States after collection over specified period of time. Data is saved under name States.m
 * @param States
 */
public void saver(double[] inputstates){
	counter ++;

	if (counter <= (int)(1/this.sampletime*this.periodoftime)){
		this.states[counter-1][0] = inputstates[0];
		this.states[counter-1][1] = inputstates[1];
	}

	if (counter == (int)(1/this.sampletime*this.periodoftime)){


		new Thread(new Runnable(){
			@Override
			public void run(){

				try {
					saver.createMatFileFromFlightData("States", states);
				} catch (IOException e) {
					System.err.println("Error with creating Matfile from DataSaver");
					e.printStackTrace();
				}
			}
		}).start();
	}

}

}		


