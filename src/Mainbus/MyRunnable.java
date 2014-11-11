package Mainbus;

/**
 * Example of how to make threads
 * 1) Make the classes implement runnable
 * 2) Class should have volatile member Mainbus mainbus
 * 3) Constructor of class should set mainbus reference
 * 4) Implement run() method
 * 
 * When calling mb use synchronized! This handles multiple accesses.
 */
public class MyRunnable implements Runnable {

	//private volatile Container container;
	private volatile Mainbus mainbus;
	
    private int mThreadId;

    public MyRunnable(int threadid, Mainbus MB) {
        mainbus = MB;
        mThreadId = threadid;
    }

    public void run() {
    	while(true){
            //System.out.println(mVar);
    		synchronized(mainbus){
            	mainbus.setVar(mThreadId);
    		}
    	}
    }
}

