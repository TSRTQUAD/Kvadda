package kvaddakopter.utils;

public class Clock {

	long startTime;
	long stopTime = startTime;
	
	public void tic(){
		startTime = System.currentTimeMillis();
	}
	public void toc(){
		stopTime = System.currentTimeMillis();
	}
	
	public long getDifference(){
		return stopTime - startTime;
	}
	
	/**
	 * Uses clock to calculate the appropriate
	 * @param intendedSleep
	 * @return Sleep time
	 */
	public long getSleepTime(long intendedSleep){
		return Math.max(0, intendedSleep - (stopTime - startTime));
	}
	
	
	public long stopAndGetSleepTime(long intendedSleep){
		this.toc();
		return this.getSleepTime(intendedSleep);
	}
	
}
