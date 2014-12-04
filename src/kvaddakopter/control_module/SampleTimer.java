package kvaddakopter.control_module;
/**
 * Waits the right amount of time to get specified sampletime.
 *
 *
 */
public class SampleTimer {
double timer,sampletime, deltatime;
int counter;
	



/**
 * 
 * @param sampletime (Observe that sampletime should be specified in milliseconds)
 */
public SampleTimer(double sampletime){
	this.sampletime = sampletime;
	this.counter = 0;
}


	/**
	 * Starts timer: Should be placed first in the loop that is sampled.
	 */
	public void initiate(){
		this.timer = System.currentTimeMillis();		
	}
	
	
	/**
	 * End timer: Should be placed last in the loop that is sampled, this function waits the correct amount of time to achieve specified sampletime.
	 * If more than 5 samples in a row takes longer than specified sampletime an error msg is printed.
	 */
	public void waiter(){
		this.deltatime = this.sampletime - (System.currentTimeMillis() - this.timer);
		if (this.deltatime < 0){
			this.counter ++;
			if (5 == counter){			
				System.err.println("Sampletime is to low for the control module, " + this.counter + "samples in a row has taken longer than" + this.sampletime + "milliseconds");
				this.counter = 0;
			}
			this.counter = 0;		
		}
		else{
			try {
				Thread.sleep((long) this.deltatime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
	