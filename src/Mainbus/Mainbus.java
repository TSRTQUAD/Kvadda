package Mainbus;

/**
 * Mainbus class
 * This is the main thread of the project
 * 
 * How to make new process
 * 1) Implement classes as runnables, see MyRunnable for setup
 * 2) Create thread with your runnable
 * 3) start thread
 * 
 * Ex)
 * MyRunnable myRunnable = new MyRunnable(other variables,mainbus);
 *       
 * Thread t = new Thread(myRunnable);
 * t.setPriority(1); //Sets priority (how much sheduled time the thread gets)
 * t.start(); 
 * 
 * -----------------------------------
 * If unsure about synchronization read the following:
 * http://www.javaworld.com/article/2074318/java-concurrency/java-101--understanding-java-threads--part-2--thread-synchronization.html
 * 
 */
public class Mainbus{
	
	private int var;
	public boolean condVar = false;
	
	public static void main(String[] args) {
		Mainbus mainbus = new Mainbus();
		
        MyRunnable myRunnable = new MyRunnable(1,mainbus);
        MyRunnable myRunnable2 = new MyRunnable(2,mainbus);
        
        Thread t = new Thread(myRunnable);
        t.setPriority(1);
        t.start();
        
        Thread t2 = new Thread(myRunnable2);
        t2.setPriority(2);
        t2.start();
        
        while(true){
        	//Thread.sleep(1);
        	synchronized(mainbus){
            	//System.out.println(mainbus.getVar());
        	}
        }
	}
	
	public synchronized void setVar(int i){
		var = i;
	}
	
	public synchronized int getVar(){
		return var;
	}
	
	public synchronized void waitOnCondVar(){
    	while (!condVar)
    		try{
    			wait();
    			System.out.println("Stoped waiting");
      	      }
    	catch (InterruptedException e) {}
    }
    
    public synchronized void releaseCondVar(){
           condVar = true;
           notify ();
    }
}
