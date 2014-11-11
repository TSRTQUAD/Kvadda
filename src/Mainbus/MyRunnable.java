package Mainbus;

/**
 * Example of how to make threads
 * 1) Make the classes implement runnable
 * 2) Class should have volatile member Mainbus mainbus
 * 3) Constructor of class should set mainbus reference
 * 4) Implement run() method
 * 
 * When calling mb use synchronized! This handles multiple accesses.
 * OBS! This is not needed if get/set functions are declared as synchronized in mainbus
 * OBS! Do not put entire methods inside synchronized. Just the get/set from mainbus
 * 
 * GUI can initiate other threads with the use of condition variables 
 * The other threads can wait until GUI sets their respective conditionvariables
 * See example code
 */
public class MyRunnable implements Runnable {

	//private volatile Container container;
	private Mainbus mainbus;
	
    private int mThreadId;

    public MyRunnable(int threadid, Mainbus MB) {
        mainbus = MB;
        mThreadId = threadid;
    }

    public void run() {
    	while(true){
//    		synchronized(mainbus){
//            	mainbus.setVar(mThreadId);
//    		}
    		
    		condVarExample();


    	}
    }
    
    /**
     * Example of use of condition variable
     * Thread with Id 1 waits on condition variable
     * Thread 2 sets condition variable after 5 seconds
     */
    private void condVarExample(){
    	if(mThreadId == 1){
    		mainbus.waitOnCondVar();
    	}
    	else if (mThreadId == 2){
    		try {
				Thread.sleep(5000);
				mainbus.releaseCondVar();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}

